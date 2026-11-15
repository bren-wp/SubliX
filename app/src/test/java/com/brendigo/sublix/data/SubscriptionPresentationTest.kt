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
    @Test fun largeSubscriptionListOnlyNeedsEightRenewalsPerPlan() {
        val items = (0 until 1500).map { i ->
            Subscription(
                id = "plan-${i.toString().padStart(4, '0')}",
                name = "Service ${i.toString().padStart(4, '0')}",
                amountCents = 999,
                nextDue = sunday.plusDays(1),
                cycle = BillingCycle.WEEKLY
            )
        }
        val result = SubscriptionPresentation.upcomingCharges(
            items, sunday, sunday.plusYears(3), limit = 8
        )
        assertEquals(8, result.size)
        assertEquals((0 until 8).map { "plan-${it.toString().padStart(4, '0')}" },
            result.map { it.subscription.id })
        assertTrue(result.all { it.due == sunday.plusDays(1) })
    }

    @Test fun perPlanLimitPreservesEarliestRenewals() {
        val weekly = Subscription(
            id = "weekly-limits", name = "Weekly", amountCents = 599,
            nextDue = sunday, cycle = BillingCycle.WEEKLY
        )
        val occurrences = BillingMath.upcoming(weekly, sunday, sunday.plusYears(5), limit = 3)
        assertEquals(
            listOf(sunday, sunday.plusWeeks(1), sunday.plusWeeks(2)),
            occurrences
        )
        assertTrue(BillingMath.upcoming(weekly, sunday, sunday.plusMonths(1), limit = 0).isEmpty())
    }

    @Test fun calendarTop120NeverLosesEarlierChargesFromLaterPlans() {
        val many = (0 until 1200).map { i ->
            Subscription(
                id = "late-" + i,
                name = "Later " + i,
                amountCents = 400, nextDue = sunday.plusDays(20),
                cycle = BillingCycle.WEEKLY
            )
        }
        val earliest = Subscription(
            id = "earliest", name = "Earliest", amountCents = 900,
            nextDue = sunday, cycle = BillingCycle.YEARLY
        )
        val result = SubscriptionPresentation.upcomingCharges(
            many + earliest, sunday, sunday.plusMonths(3), 120
        )
        assertEquals(120, result.size)
        assertEquals("earliest", result.first().subscription.id)
        assertEquals(sunday, result.first().due)
        assertTrue(result.zipWithNext().all { (a, b) -> !a.due.isAfter(b.due) })
    }

    @Test fun topKRetainsAlphabeticTieBreaksWhenHeapIsFull() {
        val plans = listOf("Zulu", "Beta", "Alpha", "Aardvark", "Gamma", "Omega")
            .mapIndexed { index, name ->
                Subscription(
                    id = "plan-" + index, name = name,
                    amountCents = 100, nextDue = sunday,
                    cycle = BillingCycle.YEARLY
                )
            }
        val result = SubscriptionPresentation.upcomingCharges(
            plans, sunday, sunday.plusDays(1), 3
        )
        assertEquals(listOf("Aardvark", "Alpha", "Beta"),
            result.map { it.subscription.name })
    }

    @Test fun emptyAndReversedRangesAreSafe() {
        assertTrue(SubscriptionPresentation.upcomingCharges(emptyList(),sunday,sunday.plusDays(5)).isEmpty())
        val x=Subscription(name="A",amountCents=100,cycle=BillingCycle.MONTHLY,nextDue=sunday)
        assertTrue(SubscriptionPresentation.upcomingCharges(listOf(x),sunday.plusDays(3),sunday).isEmpty())
        assertTrue(SubscriptionPresentation.upcomingCharges(listOf(x),sunday,sunday.plusDays(10),0).isEmpty())
    }
}
