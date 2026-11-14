package com.brendigo.sublix.preview

import android.content.Context
import android.content.Intent
import com.brendigo.sublix.data.*
import java.time.LocalDate

/** Synthetic screenshot fixtures loaded only into DEBUG builds on an isolated emulator. */
object PreviewSeed {
    fun prepare(context: Context, intent: Intent?) {
        if (intent?.getBooleanExtra("sublix_screenshot", false) != true) return
        val today = LocalDate.now()
        val examples = listOf(
            Subscription(id="demo-streaming", name="Streaming", amountCents=1299, nextDue=today.plusDays(4), cycle=BillingCycle.MONTHLY, category="Entertainment",trialEnd=today.plusDays(2),usage=Usage.RARELY),
            Subscription(id="demo-cloud", name="Cloud storage", amountCents=999, nextDue=today.plusDays(7), cycle=BillingCycle.MONTHLY, category="Cloud"),
            Subscription(id="demo-music", name="Music", amountCents=1099, nextDue=today.plusDays(10), cycle=BillingCycle.MONTHLY, category="Entertainment"),
            Subscription(id="demo-productivity", name="Productivity suite", amountCents=899, nextDue=today.plusDays(15), cycle=BillingCycle.MONTHLY, category="Productivity"),
            Subscription(id="demo-software", name="Photo editor", amountCents=1999, nextDue=today.plusDays(18), cycle=BillingCycle.YEARLY, category="Productivity"),
            Subscription(id="demo-archive", name="Archived service", amountCents=499, nextDue=today.plusDays(9), cycle=BillingCycle.MONTHLY, category="Other", archived=true)
        )
        SubscriptionsDb(context).use { db ->
            if (db.subscriptions().isEmpty()) {
                examples.forEach(db::save)
                db.markPaid(examples.first(),today.minusDays(3))
            }
        }
    }
}
