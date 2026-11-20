package com.brendigo.sublix.data

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

class ProAnalyticsTest {
    private fun sub(name:String, cents:Long, date:String, cycle:BillingCycle=BillingCycle.MONTHLY, category:String="Media") =
        Subscription(id=name,name=name,amountCents=cents,nextDue=LocalDate.parse(date),cycle=cycle,category=category)

    @Test fun twelveMonthForecastUsesActualDueMonths() {
        val first=YearMonth.of(2027,1)
        val items=listOf(
            sub("Jan 31",1000,"2027-01-31"),
            sub("Yearly",12000,"2027-07-01",BillingCycle.YEARLY)
        )
        val result=ProAnalytics.forecast(items,first,12)
        assertEquals(12,result.size)
        assertEquals(1000L,result[0].totalCents)
        assertEquals(1000L,result[1].totalCents)
        assertEquals(13000L,result[6].totalCents)
        assertEquals(2,result[6].chargeCount)
    }

    @Test fun forecastOmitsArchivedSubscriptionsAndPreservesQuarterlyDates() {
        val items=listOf(
            sub("active",3000,"2027-01-30",BillingCycle.QUARTERLY),
            sub("archived",9999,"2027-01-02").copy(archived=true)
        )
        val f=ProAnalytics.forecast(items,YearMonth.of(2027,1),5)
        assertEquals(listOf(3000L,0L,0L,3000L,0L),f.map{it.totalCents})
    }

    @Test fun budgetsAndScenarioUseActivePlansOnly() {
        val plans=listOf(
            sub("Music",1200,"2027-01-01"),
            sub("Old",3000,"2027-01-01").copy(archived=true),
            sub("Cloud",6000,"2027-01-31",BillingCycle.YEARLY,category="Cloud")
        )
        assertEquals(1200L,ProAnalytics.categoryMonthly(plans)["Media"])
        assertEquals(500L,ProAnalytics.categoryMonthly(plans)["Cloud"])
        assertEquals(1700L,ProAnalytics.whatIfMonthlySavings(plans,setOf("Music","Old","Cloud")))
        assertEquals(0L,ProAnalytics.whatIfMonthlySavings(plans,emptySet()))
    }

    @Test fun rejectsForecastIntervalsOutsideSupportedRange() {
        try {
            ProAnalytics.forecast(emptyList(),YearMonth.now(),0)
            fail("Expected invalid interval")
        } catch (_:IllegalArgumentException) {}
    }

    @Test fun csvNeutralizesFormulaInjectionAndEscapesQuotes() {
        val records=listOf(sub("=HYPERLINK(\"https://evil.test\")",990,"2027-05-01"))
        val csv=ProCsv.subscriptions(records)
        assertTrue(csv.contains("\"'=HYPERLINK("))
        assertTrue(csv.contains("\"\"https://evil.test\"\""))
        assertTrue(csv.contains("9.90"))
        assertTrue(csv.startsWith("Name,Category"))
    }

    @Test fun reportsContainActualForecastAndHistory() {
        val items=listOf(sub("Example",300,"2027-01-31"))
        val report=ProCsv.forecast(items,YearMonth.of(2027,1))
        assertTrue(report.contains("\"2027-02\",3.00,1"))
        val paid=ProCsv.payments(listOf(PaidRecord(1,"Local",299,LocalDate.of(2027,1,1))))
        assertTrue(paid.contains("2.99"))
    }
}
