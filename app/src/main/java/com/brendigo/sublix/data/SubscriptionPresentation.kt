package com.brendigo.sublix.data

import java.time.LocalDate
import java.util.Locale
import java.util.PriorityQueue

data class UpcomingCharge(val subscription: Subscription, val due: LocalDate)

/**
 * Stable renewal occurrence query for Home and Calendar.
 *
 * Keep only the earliest [limit] events in memory instead of sorting every
 * occurrence for every subscription, which could grow excessively for large
 * offline databases. The same date/name/id comparator is used for the
 * bounded queue and final displayed list.
 */
object SubscriptionPresentation {
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
