package com.brendigo.sublix.data

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit
import java.util.Currency
import java.util.Locale
import java.util.UUID

/** EUR-only initial release. A future FX engine must not silently add unlike currencies. */
enum class BillingCycle(val label: String) { WEEKLY("Weekly"), MONTHLY("Monthly"), QUARTERLY("Quarterly"), YEARLY("Yearly") }
data class Subscription(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val amountCents: Long,
    val nextDue: LocalDate,
    val cycle: BillingCycle,
    val category: String = "Other",
    val note: String = "",
    val archived: Boolean = false,
    val anchorDay: Int = nextDue.dayOfMonth,
    val trialEnd: LocalDate? = null,
    val cancellationUrl: String = "",
    val usage: Usage = Usage.UNKNOWN
)
enum class Usage { UNKNOWN, REGULAR, RARELY, NEVER }

data class PaidRecord(val id: Long, val subscriptionName: String, val amountCents: Long, val paidOn: LocalDate)

object BillingMath {
    fun monthlyEquivalent(items: List<Subscription>): Long = items.filterNot { it.archived }.sumOf { monthly(it) }
    fun monthly(item: Subscription): Long {
        val factor = when (item.cycle) {
            BillingCycle.WEEKLY -> BigDecimal(52).divide(BigDecimal(12), 12, RoundingMode.HALF_UP)
            BillingCycle.MONTHLY -> BigDecimal.ONE
            BillingCycle.QUARTERLY -> BigDecimal.ONE.divide(BigDecimal(3), 12, RoundingMode.HALF_UP)
            BillingCycle.YEARLY -> BigDecimal.ONE.divide(BigDecimal(12), 12, RoundingMode.HALF_UP)
        }
        return BigDecimal(item.amountCents).multiply(factor).setScale(0, RoundingMode.HALF_UP).longValueExact()
    }
    fun nextOccurrence(subscription: Subscription, count: Int): LocalDate {
        require(count >= 0)
        val anchor = subscription.nextDue
        return when (subscription.cycle) {
            BillingCycle.WEEKLY -> anchor.plusWeeks(count.toLong())
            BillingCycle.MONTHLY -> monthAt(anchor, count.toLong(), subscription.anchorDay)
            BillingCycle.QUARTERLY -> monthAt(anchor, count * 3L, subscription.anchorDay)
            BillingCycle.YEARLY -> monthAt(anchor, count * 12L, subscription.anchorDay)
        }
    }
    private fun monthAt(anchor: LocalDate, months: Long, anchorDay: Int): LocalDate {
        val target = YearMonth.from(anchor).plusMonths(months)
        return target.atDay(minOf(anchorDay, target.lengthOfMonth()))
    }
    /**
     * Advance a renewal after the user records a payment, including old overdue plans.
     * Never leave the next due date in the past after a manual confirmation.
     * Keeps the original day-of-month anchor for end-of-month renewals.
     */
    fun nextDueAfter(item: Subscription, reference: LocalDate): LocalDate {
        val monthsPassed=java.time.temporal.ChronoUnit.MONTHS.between(
            YearMonth.from(item.nextDue), YearMonth.from(reference)).coerceAtLeast(0L)
        var count=when(item.cycle) {
            BillingCycle.WEEKLY -> java.time.temporal.ChronoUnit.WEEKS.between(item.nextDue,reference).coerceAtLeast(0L)+1L
            BillingCycle.MONTHLY -> monthsPassed.coerceAtLeast(1L)
            BillingCycle.QUARTERLY -> (monthsPassed/3L).coerceAtLeast(1L)
            BillingCycle.YEARLY -> (monthsPassed/12L).coerceAtLeast(1L)
        }
        var result=nextOccurrence(item,count.toInt())
        while(!result.isAfter(reference)) {
            count++
            require(count<=Int.MAX_VALUE.toLong()) {"Renewal out of range"}
            result=nextOccurrence(item,count.toInt())
        }
        return result
    }
    /**
     * Skip old recurrences rather than iterating from the first due date.
     * A one-period lookback covers month-end day clamping (29/30/31).
     */
    fun upcoming(item: Subscription, from: LocalDate, through: LocalDate): List<LocalDate> {
        if (item.archived || through.isBefore(from)) return emptyList()
        val monthDelta = ChronoUnit.MONTHS.between(
            YearMonth.from(item.nextDue), YearMonth.from(from)).coerceAtLeast(0L)
        val approx = when (item.cycle) {
            BillingCycle.WEEKLY -> ChronoUnit.DAYS.between(item.nextDue, from).coerceAtLeast(0L) / 7L
            BillingCycle.MONTHLY -> monthDelta
            BillingCycle.QUARTERLY -> monthDelta / 3L
            BillingCycle.YEARLY -> monthDelta / 12L
        }
        var count = (approx - 1L).coerceAtLeast(0L)
        val dates = ArrayList<LocalDate>()
        while (count <= Int.MAX_VALUE.toLong() && dates.size < 800) {
            val due = nextOccurrence(item, count.toInt())
            if (due.isAfter(through)) break
            if (!due.isBefore(from)) dates.add(due)
            count++
        }
        return dates
    }
    fun daysUntil(item: Subscription, now: LocalDate): Long? = upcoming(item, now, now.plusDays(7)).firstOrNull()?.let { ChronoUnit.DAYS.between(now, it) }
    fun format(cents: Long): String = NumberFormat.getCurrencyInstance(Locale.GERMANY).apply { currency = Currency.getInstance("EUR") }.format(cents / 100.0)
    fun parse(amount: String): Long? = try {
        val number = BigDecimal(amount.trim().replace(',', '.'))
        if (number.signum() < 0 || number > BigDecimal("999999999.99")) null
        else number.movePointRight(2).setScale(0, RoundingMode.UNNECESSARY).longValueExact()
    } catch (_: Exception) { null }
}
