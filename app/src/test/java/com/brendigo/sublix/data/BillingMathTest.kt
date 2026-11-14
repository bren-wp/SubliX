package com.brendigo.sublix.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class BillingMathTest {
    private fun subscription(cents:Long, cycle:BillingCycle,day:String="2026-01-31") = Subscription(name="Example",amountCents=cents,cycle=cycle,nextDue=LocalDate.parse(day))
    @Test fun monthlyEquivalentIsExplicit() {
        assertEquals(999L,BillingMath.monthly(subscription(999,BillingCycle.MONTHLY)))
        assertEquals(1000L,BillingMath.monthly(subscription(12000,BillingCycle.YEARLY)))
        assertEquals(1000L,BillingMath.monthly(subscription(3000,BillingCycle.QUARTERLY)))
    }
    @Test fun monthEndDoesNotDrift() {
        val s=subscription(999,BillingCycle.MONTHLY)
        assertEquals(LocalDate.parse("2026-02-28"),BillingMath.nextOccurrence(s,1))
        assertEquals(LocalDate.parse("2026-03-31"),BillingMath.nextOccurrence(s,2))
    }
    @Test fun leapYearIsHandled() {
        val s=subscription(500,BillingCycle.YEARLY,"2024-02-29")
        assertEquals(LocalDate.parse("2025-02-28"),BillingMath.nextOccurrence(s,1))
        assertEquals(LocalDate.parse("2028-02-29"),BillingMath.nextOccurrence(s,4))
    }
    @Test fun advancingMonthPreservesOriginalBillingDay() {
        val first=subscription(999,BillingCycle.MONTHLY)
        val afterPayment=first.copy(nextDue=BillingMath.nextOccurrence(first,1))
        assertEquals(LocalDate.parse("2026-03-31"),BillingMath.nextOccurrence(afterPayment,1))
    }
    @Test fun trialMetadataDoesNotChangeRecurringCost() {
        val s=subscription(1200,BillingCycle.MONTHLY).copy(trialEnd=LocalDate.parse("2026-02-15"),usage=Usage.RARELY)
        assertEquals(1200L,BillingMath.monthly(s))
        assertEquals(1,BillingMath.upcoming(s,LocalDate.parse("2026-01-31"),LocalDate.parse("2026-02-01")).size)
    }
    @Test fun invalidAmountsRejected() {
        assertNull(BillingMath.parse("-1"))
        assertNull(BillingMath.parse("NaN"))
        assertNull(BillingMath.parse("1.234"))
        assertEquals(1234L,BillingMath.parse("12,34"))
    }
    @Test fun archivedItemsNotCounted() {
        val active=subscription(999,BillingCycle.MONTHLY)
        assertEquals(999L,BillingMath.monthlyEquivalent(listOf(active,active.copy(archived=true))))
    }
}
