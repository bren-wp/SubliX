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
    fun upcoming(item: Subscription, from: LocalDate, through: LocalDate): List<LocalDate> {
        if (item.archived || through.isBefore(from)) return emptyList()
        val result = ArrayList<LocalDate>()
        for (i in 0..800) { // bounded; at most 800 weekly periods
            val due = nextOccurrence(item, i)
            if (due.isAfter(through)) break
            if (!due.isBefore(from)) result.add(due)
        }
        return result
    }
    fun daysUntil(item: Subscription, now: LocalDate): Long? = upcoming(item, now, now.plusDays(7)).firstOrNull()?.let { ChronoUnit.DAYS.between(now, it) }
    fun format(cents: Long): String = NumberFormat.getCurrencyInstance(Locale.GERMANY).apply { currency = Currency.getInstance("EUR") }.format(cents / 100.0)
    fun parse(amount: String): Long? = try {
        val number = BigDecimal(amount.trim().replace(',', '.'))
        if (number.signum() < 0 || number > BigDecimal("999999999.99")) null
        else number.movePointRight(2).setScale(0, RoundingMode.UNNECESSARY).longValueExact()
    } catch (_: Exception) { null }
}
