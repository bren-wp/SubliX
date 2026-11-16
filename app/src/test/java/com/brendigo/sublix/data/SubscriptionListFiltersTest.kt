package com.brendigo.sublix.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class SubscriptionListFiltersTest {
    private val today = LocalDate.of(2026, 11, 15)

    private fun plan(
        id: String,
        name: String,
        due: LocalDate = today,
        category: String = "Other",
        note: String = "",
        archived: Boolean = false,
        trialEnd: LocalDate? = null,
        cents: Long = 1000,
        cycle: BillingCycle = BillingCycle.MONTHLY
    ) = Subscription(
        id = id, name = name, amountCents = cents, nextDue = due,
        category = category, note = note, archived = archived, trialEnd = trialEnd,
        cycle = cycle
    )

    private fun ids(items: List<Subscription>) = items.map { it.id }

    @Test fun dueSoonIncludesTodayAndDaySevenButNotOverdue() {
        val items = listOf(
            plan("old", "Old", today.minusDays(1)),
            plan("today", "Today"),
            plan("seven", "Seven", today.plusDays(7)),
            plan("eight", "Eight", today.plusDays(8)),
            plan("archived", "Archived", today.plusDays(2), archived = true)
        )
        assertEquals(listOf("today", "seven"),
            ids(SubscriptionListFilters.select(items,
                quickFilter = PlanQuickFilter.DUE_SOON, today = today)))
        assertEquals(listOf("old"),
            ids(SubscriptionListFilters.select(items,
                quickFilter = PlanQuickFilter.OVERDUE, today = today)))
    }

    @Test fun trialFilterFindsUpcomingTrialEndButNotOrdinaryRenewal() {
        val items = listOf(
            plan("end-today", "Trial A", trialEnd = today),
            plan("end-week", "Trial B", trialEnd = today.plusDays(7)),
            plan("already-ended", "Trial C", trialEnd = today.minusDays(1)),
            plan("later", "Trial D", trialEnd = today.plusDays(8)),
            plan("no-trial", "Plain", due = today.plusDays(1))
        )
        assertEquals(listOf("end-today", "end-week"),
            ids(SubscriptionListFilters.select(items,
                quickFilter = PlanQuickFilter.TRIAL_ENDING, today = today)))
    }

    @Test fun searchMatchesCategoryAndNotesIgnoringAccentsAndCase() {
        val items = listOf(
            plan("cafe", "Čaj & Kava", category = "Učenje", note = "Mjesečna pretplata"),
            plan("stream", "Video", category = "Entertainment", note = "Obiteljski paket")
        )
        assertEquals(listOf("cafe"), ids(SubscriptionListFilters.select(items,
            query = " CAJ UCENJE ", today = today)))
        assertEquals(listOf("stream"), ids(SubscriptionListFilters.select(items,
            query = "obiteljski", today = today)))
        assertTrue(SubscriptionListFilters.select(items, query = "no-match", today = today).isEmpty())
    }

    @Test fun archiveNeverLeaksIntoActiveQuickFiltersOrSearch() {
        val active = plan("active", "Music", category = "Entertainment")
        val hidden = active.copy(id = "hidden", archived = true)
        assertEquals(listOf("active"),
            ids(SubscriptionListFilters.select(listOf(hidden, active), query = "music", today = today)))
        assertEquals(listOf("hidden"),
            ids(SubscriptionListFilters.select(listOf(hidden, active),
                archived = true, today = today)))
        assertTrue(SubscriptionListFilters.select(listOf(hidden),
            archived = true, quickFilter = PlanQuickFilter.DUE_SOON, today = today).isEmpty())
    }

    @Test fun priceSortingUsesMonthlyEquivalentNotRawCharge() {
        val weekly = plan("weekly", "Weekly", cents = 500, cycle = BillingCycle.WEEKLY)
        val yearly = plan("yearly", "Yearly", cents = 12000, cycle = BillingCycle.YEARLY)
        val monthly = plan("monthly", "Monthly", cents = 2000)
        assertEquals(listOf("weekly", "monthly", "yearly"),
            ids(SubscriptionListFilters.select(listOf(yearly, monthly, weekly),
                sortOrder = PlanSortOrder.HIGHEST_COST, today = today)))
        assertEquals(listOf("yearly", "monthly", "weekly"),
            ids(SubscriptionListFilters.select(listOf(yearly, monthly, weekly),
                sortOrder = PlanSortOrder.LOWEST_COST, today = today)))
    }

    @Test fun equalDueDatesAreOrderedDeterministicallyAcrossDatabaseOrder() {
        val zeta = plan("z", "Zeta")
        val alphaZ = plan("z-duplicate", "Alpha")
        val alphaA = plan("a-duplicate", "Alpha")
        val expected = listOf("a-duplicate", "z-duplicate", "z")
        assertEquals(expected, ids(SubscriptionListFilters.select(listOf(zeta, alphaZ, alphaA), today = today)))
        assertEquals(expected, ids(SubscriptionListFilters.select(listOf(alphaZ, zeta, alphaA), today = today)))
    }

    @Test fun archiveSearchIgnoresActiveRowsAndPreservesAlphabeticalOrder() {
        val archivedZ = plan("z", "Žičana TV", archived = true, note = "Obiteljski paket")
        val archivedA = plan("a", "Aplikacija", archived = true, category = "Učenje")
        val active = plan("live", "Žičana TV", archived = false, note = "Obiteljski paket")
        val all = listOf(archivedZ, active, archivedA)
        assertEquals(listOf("a", "z"), ids(SubscriptionListFilters.select(all,
            archived = true, sortOrder = PlanSortOrder.NAME, today = today)))
        assertEquals(listOf("z"), ids(SubscriptionListFilters.select(all,
            archived = true, query = "obiteljski", sortOrder = PlanSortOrder.NAME, today = today)))
        assertEquals(listOf("a"), ids(SubscriptionListFilters.select(all,
            archived = true, query = "UCENJE", sortOrder = PlanSortOrder.NAME, today = today)))
        assertTrue(SubscriptionListFilters.select(all,
            archived = true, query = "unmatched", today = today).isEmpty())
    }

    @Test fun categoryFilteringRemainsExactWhileSearchCanUseOtherFields() {
        val a = plan("a", "Analytics", category = "Productivity", note = "Cloud work")
        val b = plan("b", "Cloud backup", category = "Cloud")
        assertEquals(listOf("a"),
            ids(SubscriptionListFilters.select(listOf(a, b),
                query = "cloud", category = "Productivity", today = today)))
        assertTrue(SubscriptionListFilters.select(listOf(a, b),
            query = "cloud", category = "Health", today = today).isEmpty())
    }
}
