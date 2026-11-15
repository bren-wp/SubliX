package com.brendigo.sublix.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit

class RecurrenceCoverageTest {
    private fun sub(date:String, cycle:BillingCycle) =
        Subscription(name="Test subscription",amountCents=799,cycle=cycle,nextDue=LocalDate.parse(date))

    @Test fun historicWeeklyEntryStillForecastsEvents() {
        val s=sub("2000-01-01",BillingCycle.WEEKLY)
        val from=LocalDate.parse("2026-11-15")
        val events=BillingMath.upcoming(s,from,from.plusDays(30))
        assertTrue(events.size in 4..5)
        assertTrue(events.first()>=from)
        events.forEach { assertEquals(0L,ChronoUnit.DAYS.between(s.nextDue,it)%7) }
    }
    @Test fun monthlyEntryMoreThanCenturyOldShowsMonthEnd() {
        val s=sub("1900-01-31",BillingCycle.MONTHLY)
        assertEquals(listOf(LocalDate.parse("2026-02-28"),LocalDate.parse("2026-03-31")),
            BillingMath.upcoming(s,LocalDate.parse("2026-02-01"),LocalDate.parse("2026-03-31")))
    }
    @Test fun ancientQuarterlyDateClampsWithoutLosingPeriod() {
        val s=sub("2001-01-31",BillingCycle.QUARTERLY)
        assertEquals(listOf(LocalDate.parse("2026-04-30")),
            BillingMath.upcoming(s,LocalDate.parse("2026-02-01"),LocalDate.parse("2026-04-30")))
    }
    @Test fun annualLeapDayStillRecursAfterTwoDecades() {
        val s=sub("2000-02-29",BillingCycle.YEARLY)
        assertEquals(listOf(LocalDate.parse("2027-02-28"),LocalDate.parse("2028-02-29")),
            BillingMath.upcoming(s,LocalDate.parse("2027-01-01"),LocalDate.parse("2028-03-01")))
    }
    @Test fun beforeFirstDueDateHasNoEvents() {
        val s=sub("2028-04-15",BillingCycle.MONTHLY)
        assertTrue(BillingMath.upcoming(s,LocalDate.parse("2027-04-01"),LocalDate.parse("2027-04-30")).isEmpty())
    }
    @Test fun archivedAndReverseRangesAreEmpty() {
        val s=sub("2000-01-01",BillingCycle.WEEKLY)
        assertTrue(BillingMath.upcoming(s.copy(archived=true),LocalDate.parse("2026-11-01"),LocalDate.parse("2026-12-01")).isEmpty())
        assertTrue(BillingMath.upcoming(s,LocalDate.parse("2026-12-01"),LocalDate.parse("2026-11-01")).isEmpty())
    }
    @Test fun proForecastIncludesHistoricRecurringSubscription() {
        val s=sub("1900-01-31",BillingCycle.MONTHLY)
        assertEquals(listOf(799L,799L,799L),
            ProAnalytics.forecast(listOf(s),YearMonth.of(2026,11),3).map{it.totalCents})
    }
}
