package com.brendigo.sublix.data

import java.time.LocalDate
import java.time.YearMonth
import java.util.Locale
import java.util.PriorityQueue

data class UpcomingCharge(val subscription: Subscription, val due: LocalDate)

enum class CalendarPeriod(val label:String) {
    ALL("Next 3 months"), THIS_MONTH("This month"),
    NEXT_MONTH("Next month"), FOLLOWING_MONTH("In 2 months")
}

/**
 * Stable renewal occurrence query for Home and Calendar.
 *
 * Keep only the earliest [limit] events in memory instead of sorting every
 * occurrence for every subscription, which could grow excessively for large
 * offline databases. The same date/name/id comparator is used for the
 * bounded queue and final displayed list.
 */
object SubscriptionPresentation {
    /**
     * Query each selected month on its own: filtering the already capped
     * 3-month top 120 would hide later months in large databases.
     */
    fun calendarCharges(items:List<Subscription>,today:LocalDate,
        period:CalendarPeriod,limit:Int=120):List<UpcomingCharge> {
        if(period==CalendarPeriod.ALL) {
            return upcomingCharges(items,today,today.plusMonths(3),limit)
        }
        val offset=when(period) {
            CalendarPeriod.THIS_MONTH->0L
            CalendarPeriod.NEXT_MONTH->1L
            CalendarPeriod.FOLLOWING_MONTH->2L
            CalendarPeriod.ALL->error("Handled above")
        }
        val month=YearMonth.from(today).plusMonths(offset)
        return upcomingCharges(items,maxOf(today,month.atDay(1)),month.atEndOfMonth(),limit)
    }

    fun upcomingCharges(
        items: List<Subscription>,
        from: LocalDate,
        through: LocalDate,
        limit: Int = 8
    ): List<UpcomingCharge> {
        if (limit <= 0 || through.isBefore(from)) return emptyList()
        val order = compareBy<UpcomingCharge> { it.due }
            .thenBy { it.subscription.name.lowercase(Locale.ROOT) }
            .thenBy { it.subscription.id }
        val latestFirst = PriorityQueue<UpcomingCharge>(minOf(limit, 256), order.reversed())

        for (item in items) {
            if (item.archived) continue
            // Once full, a candidate after the current last due date cannot
            // enter the top N. Keep ties so name/id can still break them.
            val throughForPlan = if (latestFirst.size == limit)
                minOf(through, latestFirst.peek().due) else through
            for (due in BillingMath.upcoming(item, from, throughForPlan, limit)) {
                val event = UpcomingCharge(item, due)
                if (latestFirst.size < limit) {
                    latestFirst.add(event)
                } else if (order.compare(event, latestFirst.peek()) < 0) {
                    latestFirst.poll()
                    latestFirst.add(event)
                }
            }
        }
        return latestFirst.toList().sortedWith(order)
    }
}
