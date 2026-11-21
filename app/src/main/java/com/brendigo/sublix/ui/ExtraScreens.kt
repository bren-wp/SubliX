package com.brendigo.sublix.ui

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brendigo.sublix.BuildConfig
import com.brendigo.sublix.data.*
import com.brendigo.sublix.notifications.ReminderScheduler
import java.time.LocalDate
import java.time.temporal.ChronoUnit

private val conceptShape = RoundedCornerShape(22.dp)
private val standardCategories = listOf("Entertainment", "Productivity", "Cloud", "Utilities", "Health", "Learning", "Other")
private val providerTemplates = listOf(
    "Streaming" to "Entertainment", "Music" to "Entertainment", "Cloud storage" to "Cloud",
    "Productivity suite" to "Productivity", "Gaming" to "Entertainment",
    "Fitness" to "Health", "Online course" to "Learning", "Hosting" to "Cloud",
    "Internet" to "Utilities", "Custom service" to "Other"
)

/** Local preference, no account/network. Every built-in category remains available. */
internal fun allCategories(context: Context): List<String> {
    val saved = context.getSharedPreferences("sublix_local_categories",Context.MODE_PRIVATE)
        .getStringSet("names",emptySet()) ?: emptySet()
    return (standardCategories+saved).distinct().sorted()
}

@Composable private fun ScreenHeading(title:String,desc:String) {
    Text(title,fontSize=29.sp,fontWeight=FontWeight.Bold)
    Text(desc,color=Muted,fontSize=13.sp)
}
@Composable private fun ElevatedSection(modifier:Modifier=Modifier, content:@Composable ColumnScope.()->Unit) {
    Column(modifier.fillMaxWidth().background(Surface,conceptShape).padding(19.dp),verticalArrangement=Arrangement.spacedBy(11.dp),content=content)
}
@Composable private fun Callout(label:String,value:String,description:String?=null) {
    Column(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary.copy(alpha=.18f),conceptShape).padding(23.dp),
        verticalArrangement=Arrangement.spacedBy(8.dp)) {
        Text(label,color=MaterialTheme.colorScheme.primary,fontWeight=FontWeight.Medium,fontSize=14.sp)
        Text(value,fontSize=33.sp,fontWeight=FontWeight.ExtraBold)
        if(description!=null) Text(description,color=Muted,fontSize=12.sp)
    }
}


@Composable internal fun ServiceLibraryScreen(modifier:Modifier,onChoose:(String,String)->Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    val filtered=remember(query) { ServiceTemplateSearch.find(providerTemplates,query) }
    LazyColumn(modifier.testTag("library-list"),contentPadding=PaddingValues(20.dp),
        verticalArrangement=Arrangement.spacedBy(12.dp)) {
        item { ScreenHeading("Service library","Start with a template or add your own.") }
        item { OutlinedTextField(value=query,onValueChange={query=it.take(60)},
            label={Text("Search services")},leadingIcon={Icon(Icons.Outlined.Search,null)},
            singleLine=true,modifier=Modifier.fillMaxWidth().testTag("library-search")) }
        if(query.isNotBlank()) item {
            TextButton(onClick={query=""},modifier=Modifier.testTag("library-clear-search")) {
                Text("Clear search")
            }
        }
        if(filtered.isEmpty()) item {
            ElevatedSection {
                Text("No service templates match your search.",color=Muted)
                OutlinedButton(onClick={onChoose("","Other")},
                    modifier=Modifier.fillMaxWidth().heightIn(min=48.dp)
                        .testTag("library-use-custom")) {
                    Text("Add custom subscription")
                }
            }
        }
        items(filtered,key={it.first}) { (name,category) ->
            ElevatedSection(Modifier
                .semantics { contentDescription = "Use " + name + " template, " + category }
                .clickable(role=Role.Button,
                    onClick={onChoose(if(name=="Custom service") "" else name,category)})) {
                Row(verticalAlignment=Alignment.CenterVertically) {
                    Box(Modifier.size(44.dp).background(Raised,RoundedCornerShape(13.dp)),
                        contentAlignment=Alignment.Center) {
                        Text(name.first().toString(),fontWeight=FontWeight.Bold,
                            color=MaterialTheme.colorScheme.primary)
                    }
                    Spacer(Modifier.width(13.dp))
                    Column(Modifier.weight(1f)) {
                        Text(name,fontWeight=FontWeight.SemiBold)
                        Text(category,color=Muted,fontSize=12.sp)
                    }
                    Icon(Icons.Outlined.ChevronRight,contentDescription=null)
                }
            }
        }
        item { Text("Templates are generic examples. SubliX is not affiliated with any provider.",
            fontSize=11.sp,color=Muted) }
    }
}

@Composable internal fun PaymentDetailsScreen(record:PaidRecord?,modifier:Modifier) {
    LazyColumn(modifier,contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(14.dp)){
        item { ScreenHeading("Payment details","Your manually recorded payment.") }
        if(record==null) item{ ElevatedSection {Text("Select a payment from History.",color=Muted)} }
        else {
            item { Callout("Payment recorded",BillingMath.format(record.amountCents),"This is not a bank transaction.") }
            item { ElevatedSection {
                Text(record.subscriptionName,fontWeight=FontWeight.SemiBold,fontSize=21.sp)
                Text("Recorded on: ${record.paidOn}",color=Muted)
                Text("Reference: #${record.id}",color=Muted,fontSize=12.sp)
            } }
            item { Text("SubliX does not handle payments. This record reflects your confirmation only.",color=Muted,fontSize=12.sp) }
        }
    }
}

@Composable internal fun ReminderSettingsScreen(modifier:Modifier) {
    val context=LocalContext.current
    var enabled by remember{mutableStateOf(ReminderScheduler.isEnabled(context))}
    var days by remember { mutableStateOf(ReminderScheduler.getDays(context)) }
    val grant=rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted->
        enabled=granted;ReminderScheduler.setEnabled(context,granted)
    }
    LazyColumn(modifier,contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(16.dp)){
        item { ScreenHeading("Reminders","Know before your next renewal.") }
        item { Callout("Payment alerts",if(enabled)"Enabled" else "Disabled","Local and approximate scheduling; not guaranteed at the exact minute.") }
        item { ElevatedSection {
            Row(verticalAlignment=Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {Text("Enable alerts",fontWeight=FontWeight.SemiBold);Text("Optional Android notifications",color=Muted,fontSize=12.sp)}
                Switch(checked=enabled,onCheckedChange={value->
                    if(value && !ReminderScheduler.hasPermission(context) && Build.VERSION.SDK_INT>=33)
                        grant.launch(Manifest.permission.POST_NOTIFICATIONS)
                    else {enabled=value;ReminderScheduler.setEnabled(context,value)}
                })
            }
        } }
        item {Text("Remind me before renewal",fontWeight=FontWeight.Bold,fontSize=20.sp)}
        item {Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(9.dp)) {
            listOf(7,3,1,0).forEach { day->
                FilterChip(selected=day in days,onClick={
                    days=if(day in days) days-day else days+day
                    ReminderScheduler.setDays(context,days)
                },label={Text(if(day==0)"Due date" else "$day days")})
            }
        }}
        item { ElevatedSection {
            Text("Notification privacy",fontWeight=FontWeight.SemiBold)
            Text("Renewal details appear in the notification shade. On the lock screen, the preview hides service names and amounts unless device settings override privacy.",color=Muted,fontSize=13.sp)
        } }
    }
}

@Composable internal fun CategoriesScreen(modifier:Modifier) {
    val context=LocalContext.current
    val prefs=remember{context.getSharedPreferences("sublix_local_categories",Context.MODE_PRIVATE)}
    var custom by remember{mutableStateOf(prefs.getStringSet("names",emptySet())?.toSet() ?: emptySet())}
    var newName by remember{mutableStateOf("")}
    var error by remember{mutableStateOf<String?>(null)}
    LazyColumn(modifier,contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(13.dp)){
        item {ScreenHeading("Categories","Organize subscriptions on this device.")}
        item { Callout("Manage categories",allCategories(context).size.toString(),"Built-in groups plus your custom categories.") }
        item { ElevatedSection {
            OutlinedTextField(newName,{newName=it.take(40);error=null},label={Text("New category")},
                singleLine=true,modifier=Modifier.fillMaxWidth())
            if(error!=null) Text(error.orEmpty(),color=MaterialTheme.colorScheme.error,fontSize=12.sp)
            Button(onClick={
                val trimmed=newName.trim()
                if(trimmed.isEmpty()||allCategories(context).any{it.equals(trimmed,true)}) error="Enter a unique category name."
                else {
                    custom=custom+trimmed
                    prefs.edit().putStringSet("names",custom).apply()
                    newName=""
                }
            },modifier=Modifier.fillMaxWidth()){Text("Add category")}
        } }
        items((standardCategories+custom).distinct().sorted()) { name->
            ElevatedSection {
                Row(verticalAlignment=Alignment.CenterVertically) {
                    Text(name,modifier=Modifier.weight(1f),fontWeight=FontWeight.SemiBold)
                    if(name in custom) TextButton(onClick={
                        custom=custom-name
                        prefs.edit().putStringSet("names",custom).apply()
                    }) { Text("Remove") }
                    else Text("Built-in",color=Muted,fontSize=12.sp)
                }
            }
        }
        item { Text("Removing a custom category does not delete existing subscription records.",color=Muted,fontSize=11.sp) }
    }
}

@Composable internal fun BackupScreen(modifier:Modifier,onExport:()->Unit,onImport:()->Unit) {
    LazyColumn(modifier,contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(16.dp)){
        item { ScreenHeading("Backup & restore","Your records remain on your device.") }
        item { Callout("Local backup","No account","Choose where exported files are saved.") }
        item { ElevatedSection {
            Text("Export subscriptions",fontWeight=FontWeight.SemiBold,fontSize=18.sp)
            Text("Create a JSON file containing your subscription records. This file is NOT encrypted.",color=Muted)
            Button(onClick=onExport,modifier=Modifier.fillMaxWidth()){Icon(Icons.Outlined.FileUpload,null);Spacer(Modifier.width(8.dp));Text("Export backup")}
        } }
        item { ElevatedSection {
            Text("Import backup",fontWeight=FontWeight.SemiBold,fontSize=18.sp)
            Text("Read a previous SubliX JSON backup; subscription IDs are merged. Keep another backup before importing.",color=Muted)
            OutlinedButton(onClick=onImport,modifier=Modifier.fillMaxWidth()){Icon(Icons.Outlined.FileDownload,null);Spacer(Modifier.width(8.dp));Text("Import backup")}
        } }
        item {Text("Store exported files privately. The app never uploads them to a SubliX server.",color=Muted,fontSize=11.sp)}
    }
}

@Composable internal fun BudgetScreen(plans:List<Subscription>,modifier:Modifier) {
    val pro=BuildConfig.FLAVOR=="pro"
    val context=LocalContext.current
    val prefs=remember{context.getSharedPreferences(ReminderScheduler.PREFS,Context.MODE_PRIVATE)}
    var text by remember{mutableStateOf((prefs.getLong("budget_cents",10000L)/100.0).toString())}
    var saved by remember{mutableStateOf(false)}
    val spent=BillingMath.monthlyEquivalent(plans)
    val cap=BillingMath.parse(text)
    LazyColumn(modifier,contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(16.dp)){
        item {ScreenHeading("Monthly budget","Set your own recurring-cost limit.")}
        item {Callout("Monthly recurring cost",BillingMath.format(spent),"Estimated equivalent across active plans.")}
        if(!pro) item {ElevatedSection {
            Text("Spending overview",fontWeight=FontWeight.SemiBold)
            Text("Current monthly equivalent: ${BillingMath.format(spent)}",fontSize=16.sp)
            Text("Annual equivalent: ${BillingMath.format(spent*12)}",color=Muted)
            Text("These estimates use your saved renewal schedules.",fontSize=12.sp,color=Muted)
        }}
        else {
            item {ElevatedSection {
                OutlinedTextField(text,{text=it;saved=false},label={Text("Budget (EUR)")},modifier=Modifier.fillMaxWidth(),singleLine=true)
                if(cap!=null&&cap>0L){
                    LinearProgressIndicator(progress={(spent.toFloat()/cap).coerceIn(0f,1f)},modifier=Modifier.fillMaxWidth())
                    Text(if(spent>cap)"Over budget by ${BillingMath.format(spent-cap)}" else "${BillingMath.format(cap-spent)} of headroom",
                        color=if(spent>cap) MaterialTheme.colorScheme.error else Muted)
                }
                Button(onClick={ if(cap!=null&&cap>0L){prefs.edit().putLong("budget_cents",cap).apply();saved=true} },
                    enabled=cap!=null&&cap>0L,modifier=Modifier.fillMaxWidth()){Text("Save monthly budget")}
                if(saved) Text("Budget saved on this device.",color=MaterialTheme.colorScheme.primary)
            }}
        }
    }
}

@Composable internal fun SavingsScreen(plans:List<Subscription>,modifier:Modifier,onSave:(Subscription)->Unit) {
    val active=plans.filterNot { it.archived }
    val candidates=active.filter { it.usage==Usage.RARELY||it.usage==Usage.NEVER }
    val possible=BillingMath.monthlyEquivalent(candidates)
    LazyColumn(modifier,contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
        item {ScreenHeading("Savings insights",if(BuildConfig.FLAVOR=="pro")"Review the subscriptions you use least." else "Review your largest recurring costs.")}
        if(BuildConfig.FLAVOR=="pro") item {Callout("Potential annual savings",BillingMath.format(possible*12),"Estimate for services marked rarely or never used; not a cancellation.")}
        else item {Callout("Yearly recurring equivalent",BillingMath.format(BillingMath.monthlyEquivalent(active)*12),"Estimate across your active subscriptions.")}
        if(BuildConfig.FLAVOR!="pro"){
            item { ElevatedSection {
                Text("Your largest recurring costs",fontWeight=FontWeight.SemiBold)
                Text("Review these services before renewal to decide what you still need.",color=Muted,fontSize=13.sp)
            } }
            if(active.isEmpty()) item { ElevatedSection { Text("Add a subscription to see your spending.",color=Muted) } }
            items(active.sortedByDescending { BillingMath.monthly(it) }.take(8),key={it.id}) { sub ->
                ElevatedSection {
                    Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
                        Text(sub.name,fontWeight=FontWeight.SemiBold,modifier=Modifier.weight(1f))
                        Text(BillingMath.format(BillingMath.monthly(sub))+"/mo",fontWeight=FontWeight.Bold)
                    }
                }
            }
        } else {
            item { Text("Mark how often you use each service",fontWeight=FontWeight.Bold,fontSize=19.sp) }
            if(active.isEmpty())item {ElevatedSection {Text("Add a plan to begin.",color=Muted)}}
            items(active,key={it.id}){ sub->
                ElevatedSection {
                    Row {
                        Text(sub.name,fontWeight=FontWeight.SemiBold,modifier=Modifier.weight(1f))
                        Text(BillingMath.format(BillingMath.monthly(sub))+"/mo",color=Muted,fontSize=12.sp)
                    }
                    Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                        Usage.entries.forEach { usage->
                            FilterChip(selected=sub.usage==usage,onClick={onSave(sub.copy(usage=usage))},
                                label={Text(usage.name.lowercase().replaceFirstChar{it.uppercase()})})
                        }
                    }
                }
            }
        }
        item{Text("SubliX does not cancel external services. Always confirm contract terms before acting.",fontSize=12.sp,color=Muted)}
    }
}

@Composable internal fun TrialTrackerScreen(plans:List<Subscription>,modifier:Modifier,onOpen:(Subscription)->Unit) {
    val today=AppClock.today()
    val trials=plans.filter { !it.archived&&it.trialEnd!=null }.sortedBy{it.trialEnd}
    LazyColumn(modifier,contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
        item {ScreenHeading("Trial tracker","Keep track of trial end dates.")}
        item{Callout("Trials with saved end dates",trials.size.toString(),"Trial dates are based on your entries, not provider confirmation.")}
        if(trials.isEmpty()) item{ElevatedSection {Text("No trials recorded yet. Add a trial end date when editing a subscription.",color=Muted)}}
        items(trials,key={it.id}) { sub->
            ElevatedSection(Modifier.clickable{onOpen(sub)}) {
                Text(sub.name,fontWeight=FontWeight.SemiBold)
                val days=ChronoUnit.DAYS.between(today,sub.trialEnd)
                Text(if(days>=0)"Ends in $days days" else "Ended ${-days} days ago",color=MaterialTheme.colorScheme.primary)
                Text("End date: ${sub.trialEnd}",color=Muted,fontSize=12.sp)
            }
        }
    }
}

/** Free-only help surface; no purchase, upgrade, or Pro cross-promotion. */
@Composable internal fun FreeGuideScreen(modifier:Modifier) {
    LazyColumn(modifier,contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(14.dp)) {
        item { ScreenHeading("Getting started","Keep subscriptions organized, privately and offline.") }
        items(listOf(
            "Add a subscription" to "Save the amount, billing cycle and expected renewal date.",
            "Check upcoming renewals" to "Use Home and Calendar to review future payments.",
            "Avoid surprises" to "Enable optional reminders in Settings before a renewal.",
            "Review regular costs" to "Use Insights, Monthly budget and Savings insights to inspect your spending.",
            "Keep a copy of your data" to "Use Backup & restore to export a JSON file to a location you choose.",
            "Cancel at the provider" to "Archiving a plan in SubliX does not cancel its external billing."
        )) { (title,description) ->
            ElevatedSection {
                Text(title,fontWeight=FontWeight.Bold)
                Text(description,color=Muted,fontSize=13.sp)
            }
        }
    }
}

@Composable internal fun PrivacyScreen(modifier:Modifier) {
    LazyColumn(modifier,contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(15.dp)){
        item{ScreenHeading("Privacy","Your records are under your control.")}
        item{Callout("Privacy first","Offline","No login, bank integration, cloud database or ad tracking in the Android app.")}
        items(listOf(
            "Local storage" to "Subscriptions, categories and manually recorded payments are stored inside this app's Android sandbox.",
            "Notifications" to "Optional renewal alerts include service details when unlocked; the private lock-screen preview hides names and amounts. Android privacy settings still apply.",
            "Backups" to "Manual JSON backups are unencrypted. Choose a private location and remove copies you no longer need.",
            "Data transfers" to "This app keeps its own private records. To move them to another installation, export and import JSON manually.",
            "Provider cancellations" to "SubliX never cancels or pays for external subscriptions. Contact the original provider."
        )){(heading,body)-> ElevatedSection{Text(heading,fontWeight=FontWeight.Bold);Text(body,color=Muted,fontSize=13.sp)} }
        item{Text("SubliX by BRENDIGO · v${BuildConfig.VERSION_NAME}",color=Muted,fontSize=12.sp)}
    }
}
