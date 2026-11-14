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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
    var query by remember { mutableStateOf("") }
    val filtered=providerTemplates.filter{it.first.contains(query,true)||it.second.contains(query,true)}
    LazyColumn(modifier,contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
        item { ScreenHeading("Service library","Start with a template or add your own.") }
        item { OutlinedTextField(query,{query=it.take(60)},label={Text("Search services")},
            leadingIcon={Icon(Icons.Outlined.Search,null)},singleLine=true,modifier=Modifier.fillMaxWidth()) }
        items(filtered) { (name,category)->
            ElevatedSection(Modifier.clickable { onChoose(if(name=="Custom service") "" else name,category) }) {
                Row(verticalAlignment=Alignment.CenterVertically) {
                    Box(Modifier.size(44.dp).background(Raised,RoundedCornerShape(13.dp)),contentAlignment=Alignment.Center) {
                        Text(name.first().toString(),fontWeight=FontWeight.Bold,color=MaterialTheme.colorScheme.primary)
                    }
                    Spacer(Modifier.width(13.dp))
                    Column(Modifier.weight(1f)) {Text(name,fontWeight=FontWeight.SemiBold);Text(category,color=Muted,fontSize=12.sp)}
                    Icon(Icons.Outlined.ChevronRight,contentDescription="Add $name")
                }
            }
        }
        item { Text("Templates are generic examples. SubliX is not affiliated with any provider.",fontSize=11.sp,color=Muted) }
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
    val prefs=remember{context.getSharedPreferences(ReminderScheduler.PREFS,Context.MODE_PRIVATE)}
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
            Text("Alerts include the subscription name and expected amount. Review lock-screen notification visibility in Android settings.",color=Muted,fontSize=13.sp)
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
        if(!pro) item {ElevatedSection {Text("Available in SubliX Pro",fontWeight=FontWeight.Bold);Text("Pro is a separate app. There is no account or subscription purchase in Free.",color=Muted)}}
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
        item {ScreenHeading("Savings insights","Review the subscriptions you use least.")}
        item {Callout("Potential annual equivalent",BillingMath.format(possible*12),"Scenario only: assumes every marked subscription is cancelled successfully, with no fees.")}
        if(BuildConfig.FLAVOR!="pro"){
            item{ElevatedSection {Text("Available in SubliX Pro");Text("Mark usage frequency in the separate Pro edition.",color=Muted)}}
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
    val today=LocalDate.now()
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

@Composable internal fun ProScreen(modifier:Modifier) {
    val pro=BuildConfig.FLAVOR=="pro"
    LazyColumn(modifier,contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(14.dp)){
        item{ScreenHeading("SubliX Pro","Every subscription. Under control.")}
        item{Callout("Your edition",if(pro)"Pro" else "Free","Two independently installed apps, by BRENDIGO.")}
        item{ElevatedSection {
            Text("Pro capabilities",fontSize=19.sp,fontWeight=FontWeight.Bold)
            Text("Budget tracking · Savings scenarios · Advanced subscription usage labels",color=Muted)
            Text(if(pro)"Pro features are available in this app." else "Pro is installed separately. Export JSON from Free and import into Pro to move your records.",color=Muted)
        }}
        item{Text("No account. No online upgrade screen or payment processing. Publicly released Pro APKs are available to anyone with the download link.",color=Muted,fontSize=12.sp)}
    }
}

@Composable internal fun PrivacyScreen(modifier:Modifier) {
    LazyColumn(modifier,contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(15.dp)){
        item{ScreenHeading("Privacy","Your records are under your control.")}
        item{Callout("Privacy first","Offline","No login, bank integration, cloud database or ad tracking in the Android app.")}
        items(listOf(
            "Local storage" to "Subscriptions, categories and manually recorded payments are stored inside this app's Android sandbox.",
            "Notifications" to "Optional alerts show a service and expected cost. Android lock-screen settings control their visibility.",
            "Backups" to "Manual JSON backups are unencrypted. Choose a private location and remove copies you no longer need.",
            "Separate editions" to "Free and Pro have independent app databases. Transfers require an explicit export and import.",
            "Provider cancellations" to "SubliX never cancels or pays for external subscriptions. Contact the original provider."
        )){(heading,body)-> ElevatedSection{Text(heading,fontWeight=FontWeight.Bold);Text(body,color=Muted,fontSize=13.sp)} }
        item{Text("SubliX by BRENDIGO · v${BuildConfig.VERSION_NAME}",color=Muted,fontSize=12.sp)}
    }
}
