package com.brendigo.sublix.ui

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brendigo.sublix.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.YearMonth

private val proCardShape=RoundedCornerShape(20.dp)

@Composable private fun ProHeading(title:String,description:String) {
    Text(title,fontSize=29.sp,fontWeight=FontWeight.Bold)
    Text(description,color=Muted,fontSize=13.sp)
}
@Composable private fun ProCard(modifier:Modifier=Modifier,body:@Composable ColumnScope.()->Unit) {
    Column(modifier.fillMaxWidth().background(Surface,proCardShape).padding(19.dp),
        verticalArrangement=Arrangement.spacedBy(12.dp),content=body)
}
@Composable private fun ProKpi(name:String,value:String,subtext:String) {
    Column(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary.copy(alpha=.16f),proCardShape).padding(20.dp),
        verticalArrangement=Arrangement.spacedBy(6.dp)){
        Text(name,color=MaterialTheme.colorScheme.primary,fontSize=13.sp,fontWeight=FontWeight.SemiBold)
        Text(value,fontWeight=FontWeight.ExtraBold,fontSize=32.sp)
        Text(subtext,fontSize=12.sp,color=Muted)
    }
}

/** Five meaningful premium tools: timeline, category limits, what-if planning, CSV reports, price changes. */
@Composable internal fun ProSuiteHome(modifier:Modifier,onForecast:()->Unit,onCategoryBudgets:()->Unit,
    onPlanner:()->Unit,onReports:()->Unit,onPriceHistory:()->Unit) {
    val tools=listOf(
        Triple("12-month payment forecast","Visualize upcoming charges and cash requirements.",onForecast),
        Triple("Category spending limits","Assign different monthly budgets to each category.",onCategoryBudgets),
        Triple("What-if savings planner","Model possible savings without changing actual plans.",onPlanner),
        Triple("Export financial reports","Create subscription, payment and forecast CSVs locally.",onReports),
        Triple("Price change history","See price increases and decreases recorded on your device.",onPriceHistory)
    )
    LazyColumn(modifier,contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(14.dp)){
        item {ProHeading("SubliX Pro","Advanced tools. Still private and offline.")}
        item {ProKpi("YOUR EDITION","Pro","No login, no cloud dependency, no bank connection.")}
        items(tools){(name,desc,action)->ProCard(Modifier.clickable(onClick=action)) {
            Row(verticalAlignment=Alignment.CenterVertically) {
                Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(6.dp)) {
                    Text(name,fontWeight=FontWeight.Bold,fontSize=17.sp)
                    Text(desc,color=Muted,fontSize=12.sp)
                }
                Icon(Icons.Outlined.ChevronRight,"Open $name",tint=MaterialTheme.colorScheme.primary)
            }
        }}
        item {Text("These tools analyze your own records only; totals are estimates and never bank-confirmed.",fontSize=12.sp,color=Muted)}
    }
}

@Composable internal fun ProForecastScreen(plans:List<Subscription>,modifier:Modifier) {
    var months by remember{mutableIntStateOf(12)}
    val forecast=remember(plans,months){ProAnalytics.forecast(plans,YearMonth.from(AppClock.today()),months)}
    val maxCents=(forecast.maxOfOrNull{it.totalCents}?:0L).coerceAtLeast(1L)
    val sum=forecast.sumOf{it.totalCents}
    LazyColumn(modifier,contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(13.dp)){
        item {ProHeading("Payment forecast","See when expected renewals will occur.")}
        item {ProKpi("Projected cost for next $months months",BillingMath.format(sum),
            "Actual charges can differ. No external service is queried.")}
        item {Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            listOf(6,12,24).forEach {n->FilterChip(selected=months==n,onClick={months=n},label={Text("$n months")})}
        }}
        items(forecast,key={it.month.toString()}){ month->ProCard {
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
                Column{Text(month.month.month.name.lowercase().replaceFirstChar{it.uppercase()}+" "+month.month.year,
                    fontWeight=FontWeight.SemiBold)
                    Text("${month.chargeCount} scheduled charges",color=Muted,fontSize=12.sp)}
                Text(BillingMath.format(month.totalCents),fontWeight=FontWeight.Bold)
            }
            LinearProgressIndicator(progress={(month.totalCents.toFloat()/maxCents).coerceIn(0f,1f)},
                modifier=Modifier.fillMaxWidth(),color=MaterialTheme.colorScheme.primary,trackColor=Raised)
        }}
        item{Text("Forecasts are calculated from recurring rules, including billing days at month end. Currency: EUR.",color=Muted,fontSize=11.sp)}
    }
}

private fun budgetKey(category:String):String="limit_"+category.lowercase().hashCode().toUInt().toString(16)

@Composable internal fun ProCategoryBudgetScreen(plans:List<Subscription>,modifier:Modifier) {
    val context=LocalContext.current
    val prefs=remember{context.getSharedPreferences("sublix_category_budgets",Context.MODE_PRIVATE)}
    val totals=remember(plans){ProAnalytics.categoryMonthly(plans)}
    val cats=remember(plans){(allCategories(context)+totals.keys).distinct().sorted()}
    val inputs=remember{mutableStateMapOf<String,String>()}
    val saved=remember{mutableStateMapOf<String,Boolean>()}
    LazyColumn(modifier,contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
        item{ProHeading("Category budgets","Create spending targets for each category.")}
        item{ProKpi("Monthly equivalent",BillingMath.format(BillingMath.monthlyEquivalent(plans)),"Totals based on your active subscriptions.")}
        items(cats,key={it}){category->
            val budget=prefs.getLong(budgetKey(category),0L)
            val spent=totals[category]?:0L
            val input=inputs.getOrPut(category){if(budget==0L)"" else String.format(java.util.Locale.ROOT,"%.2f",budget/100.0)}
            val parsed=BillingMath.parse(input)
            ProCard {
                Text(category,fontWeight=FontWeight.Bold,fontSize=18.sp)
                Text("Monthly spending: ${BillingMath.format(spent)}",color=Muted,fontSize=12.sp)
                OutlinedTextField(value=input,onValueChange={inputs[category]=it.take(15);saved[category]=false},
                    label={Text("Category limit · EUR")},singleLine=true,
                    keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Decimal),modifier=Modifier.fillMaxWidth())
                if(budget>0L) {
                    LinearProgressIndicator(progress={(spent.toFloat()/budget).coerceIn(0f,1f)},modifier=Modifier.fillMaxWidth())
                    Text(if(spent>budget) "Over by ${BillingMath.format(spent-budget)}" else "Within limit by ${BillingMath.format(budget-spent)}",
                        color=if(spent>budget)MaterialTheme.colorScheme.error else Muted,fontSize=12.sp)
                }
                Row(horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                    Button(onClick={
                        if(parsed!=null&&parsed>0L) {
                            prefs.edit().putLong(budgetKey(category),parsed).apply();saved[category]=true
                        }
                    },enabled=parsed!=null&&parsed>0L){Text("Save limit")}
                    if(budget>0L) TextButton(onClick={
                        prefs.edit().remove(budgetKey(category)).apply();inputs[category]="";saved[category]=false
                    }){Text("Clear limit")}
                }
                if(saved[category]==true)Text("Saved on this device.",color=MaterialTheme.colorScheme.primary,fontSize=12.sp)
            }
        }
    }
}

@Composable internal fun ProWhatIfScreen(plans:List<Subscription>,modifier:Modifier) {
    val candidates=remember(plans){plans.filterNot{it.archived}.sortedByDescending{BillingMath.monthly(it)}}
    val checked=remember { mutableStateMapOf<String,Boolean>() }
    val selected=checked.filterValues{it}.keys
    val saved=ProAnalytics.whatIfMonthlySavings(plans,selected)
    val baseline=BillingMath.monthlyEquivalent(plans)
    LazyColumn(modifier,contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
        item{ProHeading("What-if savings","Choose plans to model a possible cancellation.")}
        item{ProKpi("Hypothetical yearly savings",BillingMath.format(saved*12),"This is a scenario, not an executed cancellation.")}
        item{ProCard {
            Text("Estimated monthly spend if selected plans stopped",fontWeight=FontWeight.SemiBold)
            Text(BillingMath.format((baseline-saved).coerceAtLeast(0L)),fontSize=25.sp,fontWeight=FontWeight.Bold)
            Text("Current monthly equivalent: ${BillingMath.format(baseline)}",fontSize=12.sp,color=Muted)
        }}
        if(candidates.isEmpty())item{ProCard{Text("No active plans yet. Add a subscription first.",color=Muted)}}
        items(candidates,key={it.id}){s->
            // One selectable surface with one accessibility action; nested
            // click handlers can double-toggle the scenario or duplicate focus.
            ProCard(Modifier.toggleable(value=checked[s.id]==true,role=Role.Checkbox,
                onValueChange={checked[s.id]=it})){
                Row(verticalAlignment=Alignment.CenterVertically) {
                    Checkbox(checked=checked[s.id]==true,onCheckedChange=null)
                    Column(Modifier.weight(1f)){
                        Text(s.name,fontWeight=FontWeight.SemiBold)
                        Text("${BillingMath.format(BillingMath.monthly(s))} monthly equivalent",color=Muted,fontSize=12.sp)
                    }
                    if(s.usage==Usage.RARELY||s.usage==Usage.NEVER) Text("Low use",color=MaterialTheme.colorScheme.primary,fontSize=11.sp)
                }
            }
        }
        item{Text("Before cancelling: verify contract notice periods, fees and provider instructions. SubliX never cancels external subscriptions.",color=Muted,fontSize=12.sp)}
    }
}

@Composable internal fun ProReportsScreen(plans:List<Subscription>,payments:List<PaidRecord>,modifier:Modifier) {
    val context=LocalContext.current
    var result by remember { mutableStateOf("Choose an export and an on-device destination.") }
    val scope = rememberCoroutineScope()
    fun save(uri:android.net.Uri?, buildCsv: () -> String) {
        if (uri == null) return
        scope.launch {
            val outcome = withContext(Dispatchers.IO) {
                runCatching {
                    val csv = buildCsv()
                    val stream = context.contentResolver.openOutputStream(uri)
                        ?: error("Could not open output")
                    stream.use { it.write(csv.toByteArray(Charsets.UTF_8)) }
                }
            }
            result = if (outcome.isSuccess)
                "CSV saved. Handle the file as private financial information."
            else
                "Export failed. Choose a different destination and retry."
        }
    }
    val plansExport=rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")){ uri ->
        val snapshot=plans.toList()
        save(uri) { ProCsv.subscriptions(snapshot) }
    }
    val forecastExport=rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")){ uri ->
        val snapshot=plans.toList()
        val startMonth=YearMonth.from(AppClock.today())
        save(uri) { ProCsv.forecast(snapshot,startMonth) }
    }
    val historyExport=rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")){ uri ->
        val snapshot=payments.toList()
        save(uri) { ProCsv.payments(snapshot) }
    }
    LazyColumn(modifier,contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(14.dp)){
        item{ProHeading("CSV reports","Export your data without an account or server.")}
        item{ProKpi("Exportable records","${plans.size} plans","Your files are saved only to the location you select.")}
        item{ProCard{
            Text("Subscriptions report",fontWeight=FontWeight.Bold,fontSize=18.sp)
            Text("Names, category, price, recurrence, next date and monthly equivalent.",color=Muted)
            Button(onClick={plansExport.launch("sublix-subscriptions.csv")},modifier=Modifier.fillMaxWidth()){Text("Save subscriptions CSV")}
        }}
        item{ProCard{
            Text("12-month forecast",fontWeight=FontWeight.Bold,fontSize=18.sp)
            Text("Monthly scheduled expenses and expected payment counts.",color=Muted)
            Button(onClick={forecastExport.launch("sublix-forecast.csv")},modifier=Modifier.fillMaxWidth()){Text("Save forecast CSV")}
        }}
        item{ProCard{
            Text("Payment history",fontWeight=FontWeight.Bold,fontSize=18.sp)
            Text("Only user-confirmed records; no banking data.",color=Muted)
            Button(onClick={historyExport.launch("sublix-history.csv")},modifier=Modifier.fillMaxWidth()){Text("Save payment CSV")}
        }}
        item{Text(result,color=Muted,fontSize=12.sp)}
        item{Text("CSV is unencrypted. Review where you export files. Risky spreadsheet formulas in names are neutralized.",color=Muted,fontSize=11.sp)}
    }
}

@Composable internal fun ProPriceHistoryScreen(events:List<PriceEvent>,modifier:Modifier) {
    val increases=events.count{it.newCents>it.oldCents}
    val decreases=events.count{it.newCents<it.oldCents}
    LazyColumn(modifier,contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(13.dp)){
        item{ProHeading("Price change history","Changes you recorded while editing plans.")}
        item{ProKpi("Recorded changes",events.size.toString(),"$increases increases · $decreases decreases")}
        if(events.isEmpty())item{ProCard{
            Text("No price changes recorded yet.",color=Muted)
            Text("When you edit an existing subscription and change its price, SubliX saves an offline event.",color=Muted,fontSize=12.sp)
        }}
        items(events,key={it.id}){event->
            ProCard {
                Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){
                    Text(event.serviceName,fontWeight=FontWeight.Bold)
                    Text(event.changedOn.toString(),color=Muted,fontSize=11.sp)
                }
                Text("${BillingMath.format(event.oldCents)} → ${BillingMath.format(event.newCents)}",fontSize=17.sp,
                    color=if(event.newCents>event.oldCents)MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
                Text("Recorded locally · EUR",fontSize=11.sp,color=Muted)
            }
        }
    }
}
