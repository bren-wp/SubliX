package com.brendigo.sublix.data

import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.max

/** Financial projections are based on user-entered schedules, never bank transactions. */
data class MonthlyForecast(val month: YearMonth, val totalCents: Long, val chargeCount: Int)
data class PriceEvent(val id: Long, val subscriptionId: String, val serviceName: String, val oldCents: Long, val newCents: Long, val changedOn: LocalDate)

/** Pro's forecast, category budget and scenario logic. Pure Kotlin for repeatable JVM tests. */
object ProAnalytics {
    fun forecast(items: List<Subscription>, firstMonth: YearMonth, months: Int = 12): List<MonthlyForecast> {
        require(months in 1..36)
        val active = items.filterNot { it.archived }
        return (0 until months).map { offset ->
            val month=firstMonth.plusMonths(offset.toLong())
            val events=active.flatMap { sub ->
                BillingMath.upcoming(sub,month.atDay(1),month.atEndOfMonth()).map { sub.amountCents }
            }
            MonthlyForecast(month,events.sum(),events.size)
        }
    }

    fun categoryMonthly(items: List<Subscription>): Map<String,Long> =
        items.asSequence().filterNot { it.archived }.groupBy { it.category }
            .mapValues { (_,values) -> BillingMath.monthlyEquivalent(values) }
            .toSortedMap()

    fun budgetOverage(category: String, items: List<Subscription>, budgetCents: Long): Long {
        require(budgetCents>=0L)
        return max(0L,(categoryMonthly(items)[category] ?: 0L)-budgetCents)
    }

    /** Model a hypothetical cancellation but do not change any plan or contact a provider. */
    fun whatIfMonthlySavings(items: List<Subscription>, proposedIds: Set<String>): Long =
        items.filter { !it.archived && it.id in proposedIds }.sumOf { BillingMath.monthly(it) }
}

/** Export intended for explicit user-directed local save via the Android Storage Access Framework.
 * Prevent CSV formulas in spreadsheet viewers by prefixing potentially active cells with an apostrophe.
 */
object ProCsv {
    private fun escape(value: String): String {
        val normalized=value.replace("\u0000","").replace("\r"," ").replace("\n"," ").trimStart()
        val inert=if(normalized.firstOrNull() in listOf('=','+','-','@','\t')) "'"+normalized else normalized
        return "\"" + inert.replace("\"","\"\"") + "\""
    }
    fun subscriptions(items: List<Subscription>): String = buildString {
        appendLine("Name,Category,Amount EUR,Frequency,Next expected date,Status,Monthly equivalent EUR")
        items.sortedWith(compareBy({it.archived},{it.name.lowercase()})).forEach { s ->
            appendLine(listOf(
                escape(s.name),escape(s.category),money(s.amountCents),escape(s.cycle.label),
                escape(s.nextDue.toString()),escape(if(s.archived)"Archived" else "Active"),money(BillingMath.monthly(s))
            ).joinToString(","))
        }
    }
    fun forecast(items: List<Subscription>, firstMonth: YearMonth): String = buildString {
        appendLine("Month,Projected amount EUR,Payment count")
        ProAnalytics.forecast(items,firstMonth).forEach { row ->
            appendLine(listOf(escape(row.month.toString()),money(row.totalCents),row.chargeCount.toString()).joinToString(","))
        }
    }
    fun payments(records: List<PaidRecord>): String = buildString {
        appendLine("Service,Recorded amount EUR,Recorded date")
        records.forEach { row->appendLine(listOf(escape(row.subscriptionName),money(row.amountCents),escape(row.paidOn.toString())).joinToString(",")) }
    }
    private fun money(cents:Long): String = "%d.%02d".format(java.util.Locale.ROOT,cents/100,cents%100)
}
