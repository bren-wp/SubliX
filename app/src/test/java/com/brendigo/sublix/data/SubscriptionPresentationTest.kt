package com.brendigo.sublix.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class SubscriptionPresentationTest {
    private val sunday=LocalDate.of(2026,11,15)

    @Test fun weeklyRenewalsUseEachRealOccurrenceDate() {
        val item=Subscription(id="weekly",name="Video plan",amountCents=899,
            cycle=BillingCycle.WEEKLY,nextDue=sunday.plusDays(1))
        val charges=SubscriptionPresentation.upcomingCharges(listOf(item),sunday,sunday.plusDays(24))
        assertEquals(listOf(1L,8L,15L,22L),charges.map {
            java.time.temporal.ChronoUnit.DAYS.between(sunday,it.due)
        })
        assertEquals(listOf("Video plan","Video plan","Video plan","Video plan"),
            charges.map{it.subscription.name})
    }
    @Test fun archivedPlansAreHiddenAndLimitIsHonored() {
        val week=Subscription(id="active",name="Active",amountCents=99,
            cycle=BillingCycle.WEEKLY,nextDue=sunday)
        val archived=week.copy(id="archived",name="Hidden",archived=true)
        val charges=SubscriptionPresentation.upcomingCharges(listOf(archived,week),
            sunday,sunday.plusMonths(3),limit=3)
        assertEquals(3,charges.size)
        assertTrue(charges.all { it.subscription.id=="active" })
    }
    @Test fun equalDatesRemainStableByServiceName() {
        val date=sunday.plusDays(2)
        val z=Subscription(id="z",name="Zeta",amountCents=99,
            cycle=BillingCycle.MONTHLY,nextDue=date)
        val a=z.copy(id="a",name="Alpha")
        assertEquals(listOf("Alpha","Zeta"),
            SubscriptionPresentation.upcomingCharges(listOf(z,a),sunday,sunday.plusDays(5))
                .map { it.subscription.name })
    }
    @Test fun emptyAndReversedRangesAreSafe() {
        assertTrue(SubscriptionPresentation.upcomingCharges(emptyList(),sunday,sunday.plusDays(5)).isEmpty())
        val x=Subscription(name="A",amountCents=100,cycle=BillingCycle.MONTHLY,nextDue=sunday)
        assertTrue(SubscriptionPresentation.upcomingCharges(listOf(x),sunday.plusDays(3),sunday).isEmpty())
        assertTrue(SubscriptionPresentation.upcomingCharges(listOf(x),sunday,sunday.plusDays(10),0).isEmpty())
    }
}
