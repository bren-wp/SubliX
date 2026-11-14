package com.brendigo.sublix.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class RenewalAfterPaymentTest {
    private fun plan(due:String,cycle:BillingCycle,anchor:Int=LocalDate.parse(due).dayOfMonth) =
        Subscription(name="Sample",amountCents=1099,nextDue=LocalDate.parse(due),cycle=cycle,anchorDay=anchor)

    @Test fun monthlyOverduePaymentAdvancesToNextFutureMonthEnd() {
        val item=plan("2026-01-31",BillingCycle.MONTHLY)
        assertEquals(LocalDate.parse("2026-11-30"),BillingMath.nextDueAfter(item,LocalDate.parse("2026-11-14")))
        assertEquals(LocalDate.parse("2026-12-31"),BillingMath.nextDueAfter(item,LocalDate.parse("2026-11-30")))
    }

    @Test fun earlyPaymentAdvancesOneCycleAndKeepsOriginalAnchor() {
        val item=plan("2026-06-30",BillingCycle.MONTHLY)
        assertEquals(LocalDate.parse("2026-07-30"),BillingMath.nextDueAfter(item,LocalDate.parse("2026-06-10")))
    }

    @Test fun weeklyOverdueDoesNotLeaveDueDateInPast() {
        val item=plan("2026-01-01",BillingCycle.WEEKLY)
        assertEquals(LocalDate.parse("2026-02-05"),BillingMath.nextDueAfter(item,LocalDate.parse("2026-02-01")))
    }

    @Test fun yearlyLeapDayAnchorIsPreservedThroughLeapCycle() {
        val item=plan("2024-02-29",BillingCycle.YEARLY)
        assertEquals(LocalDate.parse("2027-02-28"),BillingMath.nextDueAfter(item,LocalDate.parse("2026-03-01")))
        assertEquals(LocalDate.parse("2028-02-29"),BillingMath.nextDueAfter(item,LocalDate.parse("2027-02-28")))
    }

    @Test fun quarterlySkipsMissedPeriods() {
        val item=plan("2026-01-31",BillingCycle.QUARTERLY)
        assertEquals(LocalDate.parse("2026-10-31"),BillingMath.nextDueAfter(item,LocalDate.parse("2026-08-01")))
    }

    @Test fun allRenewalTypesReturnStrictlyFutureDates() {
        val date=LocalDate.parse("2026-11-14")
        BillingCycle.entries.forEach {cycle->
            val item=plan("2021-01-29",cycle)
            assertTrue(BillingMath.nextDueAfter(item,date).isAfter(date))
        }
    }
}
