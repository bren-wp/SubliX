package com.brendigo.sublix.data

import java.time.LocalDate
import java.util.Locale

data class UpcomingCharge(val subscription: Subscription, val due: LocalDate)

/** Stable per-renewal occurrences for the Home list, not just each plan's stored nextDue. */
object SubscriptionPresentation {
    fun upcomingCharges(
        items: List<Subscription>,
        from: LocalDate,
        through: LocalDate,
        limit: Int = 8
    ): List<UpcomingCharge> {
        if (limit <= 0 || through.isBefore(from)) return emptyList()
        return items.asSequence()
            .filterNot { it.archived }
            .flatMap { item ->
                BillingMath.upcoming(item,from,through,limit).asSequence()
                    .map { date -> UpcomingCharge(item,date) }
            }
            .sortedWith(compareBy<UpcomingCharge> { it.due }
                .thenBy { it.subscription.name.lowercase(Locale.ROOT) }
                .thenBy { it.subscription.id })
            .take(limit)
            .toList()
    }
}
