package com.brendigo.sublix.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.json.JSONObject
import org.junit.runner.RunWith
import java.time.LocalDate
import java.util.UUID

/**
 * Runs against a real Android SQLite database, isolated from the user's app data.
 * Every test uses a fresh database file and removes it in finally.
 */
@RunWith(AndroidJUnit4::class)
class SubscriptionImportTransactionTest {
    private val due = LocalDate.of(2026, 11, 15)

    private fun plan(id: String, cents: Long = 999) = Subscription(
        id = id,
        name = "Test subscription $id",
        amountCents = cents,
        nextDue = due,
        cycle = BillingCycle.MONTHLY
    )

    private fun withDatabase(test: (SubscriptionsDb) -> Unit) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "sublix-import-test-${UUID.randomUUID()}.db"
        val db = SubscriptionsDb(context, name)
        try {
            test(db)
        } finally {
            db.close()
            context.deleteDatabase(name)
        }
    }

    @Test fun malformedBackupManagementUrlNeverReachesDatabase() = withDatabase { db ->
        val existing = plan("existing", cents = 1499)
        db.save(existing)
        val json = JSONObject(BackupCodec.encode(listOf(plan("new"))))
        json.getJSONArray("subscriptions").getJSONObject(0)
            .put("cancellationUrl", "https://")
        val decoded = runCatching { BackupCodec.decode(json.toString()) }
        assertTrue("Malformed provider link must be rejected before import", decoded.isFailure)
        assertEquals(listOf("existing"), db.subscriptions().map { it.id })
        assertTrue(db.priceEvents().isEmpty())
    }

    @Test fun oversizedBackupFieldsAreRejectedWithoutTruncation() = withDatabase { db ->
        db.save(plan("existing"))
        val json=JSONObject(BackupCodec.encode(listOf(plan("new"))))
        val item=json.getJSONArray("subscriptions").getJSONObject(0)
        item.put("category", "X".repeat(81))
        assertTrue(runCatching { BackupCodec.decode(json.toString()) }.isFailure)
        item.put("category", "Other")
        item.put("note", "N".repeat(501))
        assertTrue(runCatching { BackupCodec.decode(json.toString()) }.isFailure)
        item.put("note", "")
        item.put("id", "bad" + 0.toChar() + "id")
        assertTrue(runCatching { BackupCodec.decode(json.toString()) }.isFailure)
        assertEquals(listOf("existing"), db.subscriptions().map { it.id })
        assertTrue(db.priceEvents().isEmpty())
    }

    @Test fun directInvalidRecordsAbortTheEntireSQLiteImport() = withDatabase { db ->
        db.save(plan("existing"))
        val invalid = plan("bad").copy(cancellationUrl = "javascript:alert(1)")
        assertTrue(runCatching { db.importItems(listOf(plan("good"),invalid)) }.isFailure)
        assertEquals(listOf("existing"), db.subscriptions().map { it.id })
        assertTrue(db.priceEvents().isEmpty())
    }

    @Test fun validHttpsLinkRoundTripsThroughOfflineBackup() = withDatabase { db ->
        val item = plan("provider").copy(cancellationUrl = "https://provider.example/billing")
        val decoded = BackupCodec.decode(BackupCodec.encode(listOf(item)))
        db.importItems(decoded)
        assertEquals(item.cancellationUrl, db.subscriptions().single().cancellationUrl)
    }

    @Test fun invalidSecondEntryRollsBackEveryImportedPlan() = withDatabase { db ->
        val good = plan("new-1")
        val invalid = plan("invalid", cents = -1)
        val failure = runCatching { db.importItems(listOf(good, invalid)) }
        assertTrue("Malformed entry must fail import", failure.isFailure)
        assertTrue("First item must not survive a failed import", db.subscriptions().isEmpty())
        assertTrue(db.priceEvents().isEmpty())
    }

    @Test fun aFailedImportDoesNotChangeExistingPriceOrHistory() = withDatabase { db ->
        val original = plan("existing", cents = 999)
        db.save(original)
        val changed = original.copy(amountCents = 1999)
        val invalid = plan("bad", cents = -1)
        assertTrue(runCatching { db.importItems(listOf(changed, invalid)) }.isFailure)
        assertEquals(999L, db.subscriptions().single().amountCents)
        assertTrue("Price history must be rolled back too", db.priceEvents().isEmpty())
    }

    @Test fun validImportCommitsAllPlansAndPriceHistory() = withDatabase { db ->
        val original = plan("existing", cents = 999)
        db.save(original)
        db.importItems(listOf(original.copy(amountCents = 1999), plan("new")))
        val imported = db.subscriptions()
        assertEquals(2, imported.size)
        assertEquals(1999L, imported.single { it.id == "existing" }.amountCents)
        assertEquals(1, db.priceEvents().size)
    }

    @Test fun doublePaymentForStaleRenewalDoesNotDuplicateHistory() = withDatabase { db ->
        val item = plan("recurring")
        db.save(item)
        db.markPaid(item, due)
        val afterFirstPayment = db.subscriptions().single().nextDue
        val stalePayment = runCatching { db.markPaid(item, due) }
        assertTrue("Stale payment must fail, not double-charge the history", stalePayment.isFailure)
        assertEquals(1, db.payments().size)
        assertEquals(afterFirstPayment, db.subscriptions().single().nextDue)
    }

    @Test fun paymentForDeletedSubscriptionNeverCreatesOrphanHistory() = withDatabase { db ->
        val removed = plan("deleted")
        val failure = runCatching { db.markPaid(removed, due) }
        assertTrue(failure.isFailure)
        assertTrue(db.payments().isEmpty())
    }
    @Test fun stalePriceEditCannotRecordAnIncorrectPayment() = withDatabase { db ->
        val old = plan("price-updated", cents = 999)
        db.save(old)
        db.save(old.copy(amountCents = 1999))
        assertEquals(1, db.priceEvents().size)

        val failure = runCatching { db.markPaid(old, due) }
        assertTrue("An old amount must not enter payment history", failure.isFailure)
        assertTrue("The failed payment must roll back atomically", db.payments().isEmpty())
        assertEquals(1999L, db.subscriptions().single().amountCents)
        assertEquals(due, db.subscriptions().single().nextDue)
        assertEquals(1, db.priceEvents().size)
    }

    @Test fun staleCycleOrAnchorCannotAdvanceTheWrongSchedule() = withDatabase { db ->
        val old = plan("schedule-updated")
        db.save(old)
        db.save(old.copy(cycle = BillingCycle.YEARLY, anchorDay = 28))
        assertTrue(runCatching { db.markPaid(old, due) }.isFailure)
        assertTrue(db.payments().isEmpty())
        assertEquals(BillingCycle.YEARLY, db.subscriptions().single().cycle)
        assertEquals(28, db.subscriptions().single().anchorDay)
        assertEquals(due, db.subscriptions().single().nextDue)
    }

    @Test fun renamedSubscriptionCannotRecordAnOutdatedServiceName() = withDatabase { db ->
        val old = plan("renamed")
        db.save(old)
        db.save(old.copy(name = "Updated service name"))
        assertTrue(runCatching { db.markPaid(old, due) }.isFailure)
        assertTrue(db.payments().isEmpty())
        assertEquals("Updated service name", db.subscriptions().single().name)
    }

}
