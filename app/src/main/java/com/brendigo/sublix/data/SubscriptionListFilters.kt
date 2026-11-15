package com.brendigo.sublix.data

import java.text.Normalizer
import java.time.LocalDate
import java.util.Locale

/** Local-only list refinements; never send a plan's name, notes or category off-device. */
enum class PlanQuickFilter { ALL, DUE_SOON, OVERDUE, TRIAL_ENDING }
enum class PlanSortOrder { NEXT_PAYMENT, NAME, HIGHEST_COST, LOWEST_COST }

object SubscriptionListFilters {
    /** Match diacritic-insensitive user search without changing stored plan content. */
    private fun normalized(value: String): String = Normalizer
        .normalize(value, Normalizer.Form.NFD)
        .replace(Regex("\\p{M}+"), "")
        .lowercase(Locale.ROOT)

    fun select(
        subscriptions: List<Subscription>,
        query: String = "",
        archived: Boolean = false,
        quickFilter: PlanQuickFilter = PlanQuickFilter.ALL,
        category: String? = null,
        sortOrder: PlanSortOrder = PlanSortOrder.NEXT_PAYMENT,
        today: LocalDate = LocalDate.now()
    ): List<Subscription> {
        val words = normalized(query).trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
        val through = today.plusDays(7)
        return subscriptions.asSequence()
            .filter { it.archived == archived }
            .filter { item ->
                when (quickFilter) {
                    PlanQuickFilter.ALL -> true
                    PlanQuickFilter.DUE_SOON ->
                        !archived && !item.nextDue.isBefore(today) && !item.nextDue.isAfter(through)
                    PlanQuickFilter.OVERDUE ->
                        !archived && item.nextDue.isBefore(today)
                    PlanQuickFilter.TRIAL_ENDING ->
                        !archived && item.trialEnd?.let { !it.isBefore(today) && !it.isAfter(through) } == true
                }
            }
            .filter { category.isNullOrBlank() || it.category == category }
            .filter { item ->
                if (words.isEmpty()) true
                else {
                    val searchable = normalized(item.name) + "\n" + normalized(item.category) +
                        "\n" + normalized(item.note)
                    words.all(searchable::contains)
                }
            }
            .toList()
            .let { filtered ->
                when (sortOrder) {
                    PlanSortOrder.NEXT_PAYMENT -> filtered.sortedWith(
                        compareBy<Subscription> { it.nextDue }
                            .thenBy { normalized(it.name) }.thenBy { it.id }
                    )
                    PlanSortOrder.NAME -> filtered.sortedWith(
                        compareBy<Subscription> { normalized(it.name) }
                            .thenBy { it.nextDue }.thenBy { it.id }
                    )
                    PlanSortOrder.HIGHEST_COST -> filtered.sortedWith(
                        compareByDescending<Subscription> { BillingMath.monthly(it) }
                            .thenBy { it.nextDue }.thenBy { normalized(it.name) }.thenBy { it.id }
                    )
                    PlanSortOrder.LOWEST_COST -> filtered.sortedWith(
                        compareBy<Subscription> { BillingMath.monthly(it) }
                            .thenBy { it.nextDue }.thenBy { normalized(it.name) }.thenBy { it.id }
                    )
                }
            }
    }
}
