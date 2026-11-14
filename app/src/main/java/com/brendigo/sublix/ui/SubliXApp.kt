package com.brendigo.sublix.ui

import android.Manifest
import android.app.DatePickerDialog
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.brendigo.sublix.BuildConfig
import com.brendigo.sublix.data.*
import com.brendigo.sublix.notifications.ReminderScheduler
import kotlinx.coroutines.launch
import java.io.InputStream
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

private enum class Screen { HOME, PLANS, CALENDAR, INSIGHTS, EDIT, DETAILS, SETTINGS, HISTORY, ARCHIVE, ABOUT }
private val primaryScreens = listOf(Screen.HOME, Screen.PLANS, Screen.CALENDAR, Screen.INSIGHTS)
private val contentShape = RoundedCornerShape(20.dp)
private val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
private val categories = listOf("Entertainment", "Productivity", "Cloud", "Utilities", "Health", "Learning", "Other")
private val isPro: Boolean get() = BuildConfig.FLAVOR == "pro"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubliXApp(vm: SubscriptionViewModel = viewModel()) {
    val plans by vm.subscriptions.collectAsStateWithLifecycle()
    val payments by vm.payments.collectAsStateWithLifecycle()
    var screen by remember { mutableStateOf(Screen.HOME) }
    var selectedId by remember { mutableStateOf<String?>(null) }
    val selected=plans.firstOrNull { it.id==selectedId }
    val scope=rememberCoroutineScope()
    val snackbar=remember { SnackbarHostState() }
    val context=LocalContext.current
    val notify:(String)->Unit={ message->scope.launch { snackbar.showSnackbar(message) } }
    val exporter=rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if(uri!=null) try {
            context.contentResolver.openOutputStream(uri)?.use { output ->
                output.write(BackupCodec.encode(plans).toByteArray(Charsets.UTF_8))
            } ?: error("Cannot open destination")
            notify("Backup saved")
        } catch (_:Exception){notify("Export failed. Choose another destination.")}
    }
    val importer=rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if(uri!=null) try {
            val input=context.contentResolver.openInputStream(uri) ?: error("File not found")
            val contents=input.use { readLimited(it,BackupCodec.MAX_BYTES) }
            val items=BackupCodec.decode(contents)
            vm.importItems(items)
            notify("Imported ${items.size} subscriptions")
        } catch (_:Exception){notify("Invalid or unsupported backup")}
    }
    Scaffold(
        containerColor=Background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Column(horizontalAlignment=Alignment.CenterHorizontally) {
                    Row(verticalAlignment=Alignment.CenterVertically) {
                        Text("Subli",fontWeight=FontWeight.ExtraBold,fontSize=22.sp)
                        Text("X",fontWeight=FontWeight.ExtraBold,fontSize=22.sp,color=MaterialTheme.colorScheme.primary)
                    }
                    Text("BY BRENDIGO",fontSize=8.sp,letterSpacing=1.7.sp,color=Muted)
                } },
                navigationIcon = { if(screen !in primaryScreens) IconButton(onClick={screen=if(screen==Screen.EDIT && selected!=null) Screen.DETAILS else Screen.PLANS}) {
                    Icon(Icons.Outlined.ArrowBack,contentDescription="Back")
                } },
                actions = { if(screen in primaryScreens) IconButton(onClick={screen=Screen.SETTINGS}) {
                    Icon(Icons.Outlined.Settings,contentDescription="Settings")
                } },
                colors=TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor=Background)
            )
        },
        bottomBar = {
            if(screen in primaryScreens) NavigationBar(containerColor=Surface) {
                listOf(
                    Triple(Screen.HOME,"Home",Icons.Outlined.Home),
                    Triple(Screen.PLANS,"Plans",Icons.Outlined.ListAlt),
                    Triple(Screen.CALENDAR,"Calendar",Icons.Outlined.CalendarMonth),
                    Triple(Screen.INSIGHTS,"Insights",Icons.Outlined.QueryStats)
                ).forEach { (target,label,icon) ->
                    NavigationBarItem(
                        selected=screen==target,
                        onClick={screen=target},
                        icon={Icon(icon,contentDescription=label)},
                        label={Text(label,fontSize=10.sp)},
                        colors=NavigationBarItemDefaults.colors(indicatorColor=MaterialTheme.colorScheme.primary.copy(alpha=.16f))
                    )
                }
            }
        },
        floatingActionButton = {
            if(screen==Screen.HOME || screen==Screen.PLANS) FloatingActionButton(onClick={
                selectedId=null;screen=Screen.EDIT
            },containerColor=MaterialTheme.colorScheme.primary,contentColor=MaterialTheme.colorScheme.onPrimary) {
                Icon(Icons.Outlined.Add,contentDescription="Add subscription")
            }
        },
        snackbarHost={SnackbarHost(snackbar)}
    ) { scaffoldPadding ->
        Box(Modifier.fillMaxSize().padding(scaffoldPadding),contentAlignment=Alignment.TopCenter) {
            val pageModifier=Modifier.fillMaxSize().widthIn(max=850.dp)
            when(screen) {
                Screen.HOME -> HomeScreen(plans,onOpen={selectedId=it.id;screen=Screen.DETAILS},pageModifier)
                Screen.PLANS -> PlansScreen(plans,onOpen={selectedId=it.id;screen=Screen.DETAILS},pageModifier)
                Screen.CALENDAR -> CalendarScreen(plans,onOpen={selectedId=it.id;screen=Screen.DETAILS},pageModifier)
                Screen.INSIGHTS -> InsightsScreen(plans,pageModifier)
                Screen.EDIT -> EditScreen(existing=selected,modifier=pageModifier,onSave={
                    vm.save(it);selectedId=it.id;screen=Screen.DETAILS;notify("Subscription saved")
                })
                Screen.DETAILS -> if(selected!=null) DetailScreen(selected!!,pageModifier,
                    onEdit={screen=Screen.EDIT},
                    onArchive={vm.save(selected!!.copy(archived=!selected!!.archived));screen=Screen.PLANS},
                    onDelete={vm.delete(selected!!.id);selectedId=null;screen=Screen.PLANS},
                    onPaid={vm.paid(selected!!);notify("Payment recorded")}
                ) else EmptyScreen("Subscription not found",pageModifier)
                Screen.SETTINGS -> SettingsScreen(
                    modifier=pageModifier,
                    onHistory={screen=Screen.HISTORY},
                    onArchive={screen=Screen.ARCHIVE},
                    onAbout={screen=Screen.ABOUT},
                    onExport={exporter.launch("sublix-backup.json")},
                    onImport={importer.launch(arrayOf("application/json","text/plain","application/octet-stream"))}
                )
                Screen.HISTORY -> HistoryScreen(payments,pageModifier)
                Screen.ARCHIVE -> ArchiveScreen(plans.filter { it.archived },pageModifier,
                    onRestore={vm.save(it.copy(archived=false))},onOpen={selectedId=it.id;screen=Screen.DETAILS})
                Screen.ABOUT -> AboutScreen(pageModifier)
            }
        }
    }
}

private fun readLimited(input: InputStream, maximum: Int): String {
    val data=java.io.ByteArrayOutputStream()
    val buffer=ByteArray(8192)
    var total=0
    while(true) {
        val count=input.read(buffer)
        if(count<0) break
        total+=count
        require(total<=maximum) { "Backup too large" }
        data.write(buffer,0,count)
    }
    return data.toString(Charsets.UTF_8.name())
}

@Composable private fun SectionTitle(title:String,subtitle:String?=null) {
    Text(title,fontWeight=FontWeight.Bold,fontSize=28.sp,color=MaterialTheme.colorScheme.onBackground)
    if(subtitle!=null) Text(subtitle,color=Muted,fontSize=13.sp)
}
@Composable private fun SurfaceCard(modifier:Modifier=Modifier,content:@Composable ColumnScope.()->Unit) {
    Column(modifier.background(Surface,contentShape).padding(19.dp),verticalArrangement=Arrangement.spacedBy(8.dp),content=content)
}
@Composable private fun EmptyScreen(message:String,modifier:Modifier=Modifier) {
    Box(modifier.padding(24.dp),contentAlignment=Alignment.Center) {
        Column(horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(10.dp)){
            Icon(Icons.Outlined.AccountBalanceWallet,contentDescription=null,tint=MaterialTheme.colorScheme.primary,modifier=Modifier.size(50.dp))
            Text(message,color=Muted)
        }
    }
}

@Composable private fun PlanRow(item:Subscription,onClick:()->Unit) {
    SurfaceCard(Modifier.fillMaxWidth().clickable(onClick=onClick)) {
        Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(14.dp)) {
            Box(Modifier.size(46.dp).clip(RoundedCornerShape(14.dp)).background(MaterialTheme.colorScheme.primary.copy(alpha=.16f)),contentAlignment=Alignment.Center) {
                Text(item.name.take(1).uppercase(Locale.ENGLISH),fontWeight=FontWeight.ExtraBold,color=MaterialTheme.colorScheme.primary,fontSize=20.sp)
            }
            Column(Modifier.weight(1f)) {
                Text(item.name,fontWeight=FontWeight.SemiBold,maxLines=1,overflow=TextOverflow.Ellipsis)
                Text("${item.category} · ${item.cycle.label}",color=Muted,fontSize=12.sp)
                Text("Next ${item.nextDue.format(dateFormatter)}",color=Muted,fontSize=11.sp)
            }
            Text(BillingMath.format(item.amountCents),fontWeight=FontWeight.Bold,fontSize=16.sp)
            Icon(Icons.Outlined.ChevronRight,null,tint=Muted)
        }
    }
}

@Composable private fun HomeScreen(items:List<Subscription>,onOpen:(Subscription)->Unit,modifier:Modifier) {
    val today=LocalDate.now()
    val active=items.filterNot { it.archived }
    val monthly=BillingMath.monthlyEquivalent(items)
    val next=active.flatMap { s -> BillingMath.upcoming(s,today,today.plusDays(45)).map { d->d to s } }
        .sortedBy { it.first }.take(8)
    val month=YearMonth.from(today)
    val dueThisMonth=active.sumOf { s -> BillingMath.upcoming(s,today,month.atEndOfMonth()).size.toLong()*s.amountCents }
    LazyColumn(modifier,contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(17.dp)) {
        item {
            Column(verticalArrangement=Arrangement.spacedBy(6.dp)) {
                Text("OVERVIEW",fontSize=11.sp,fontWeight=FontWeight.Bold,letterSpacing=2.sp,color=MaterialTheme.colorScheme.primary)
                SectionTitle("Your subscriptions","A clear view of what matters.")
            }
        }
        item {
            Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(25.dp)).background(
                Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary.copy(alpha=.62f),Color(0xFF3E3567)))
            ).padding(23.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
                Text("Monthly recurring equivalent",fontSize=14.sp,color=Color(0xFFE8E3FF))
                Text(BillingMath.format(monthly),fontWeight=FontWeight.ExtraBold,fontSize=43.sp,color=Color.White)
                HorizontalDivider(color=Color.White.copy(alpha=.19f))
                Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
                    Column {Text("Annual equivalent",fontSize=11.sp,color=Color(0xFFD9D2F0));Text(BillingMath.format(monthly*12),fontWeight=FontWeight.SemiBold)}
                    Column(horizontalAlignment=Alignment.End) {Text("Due this month",fontSize=11.sp,color=Color(0xFFD9D2F0));Text(BillingMath.format(dueThisMonth),fontWeight=FontWeight.SemiBold)}
                }
            }
        }
        item { Row(horizontalArrangement=Arrangement.SpaceBetween,modifier=Modifier.fillMaxWidth()) {
            Text("Upcoming payments",fontWeight=FontWeight.Bold,fontSize=19.sp)
            Text("${active.size} active",color=Muted,fontSize=12.sp)
        } }
        if(next.isEmpty()) item { SurfaceCard(Modifier.fillMaxWidth()) { Text("No upcoming renewals. Add a subscription to get started.",color=Muted) } }
        items(next) { pair -> PlanRow(pair.second) { onOpen(pair.second) } }
        item { Text("Amounts are forecasts based on your entries; external services may charge differently.",fontSize=11.sp,color=Muted) }
    }
}

@Composable private fun PlansScreen(items:List<Subscription>,onOpen:(Subscription)->Unit,modifier:Modifier) {
    var search by remember { mutableStateOf("") }
    var showArchived by remember { mutableStateOf(false) }
    val visible=items.filter{it.archived==showArchived && it.name.contains(search,ignoreCase=true)}
    LazyColumn(modifier,contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
        item { SectionTitle("Subscriptions","All your plans in one place.") }
        item { OutlinedTextField(value=search,onValueChange={search=it},label={Text("Search subscriptions")},
            modifier=Modifier.fillMaxWidth(),singleLine=true,leadingIcon={Icon(Icons.Outlined.Search,null)}) }
        item { Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            FilterChip(selected=!showArchived,onClick={showArchived=false},label={Text("Active")})
            FilterChip(selected=showArchived,onClick={showArchived=true},label={Text("Archived")})
        } }
        if(visible.isEmpty()) item { SurfaceCard(Modifier.fillMaxWidth()) { Text("No matching subscriptions.",color=Muted) } }
        items(visible,key={it.id}) { item -> PlanRow(item){onOpen(item)} }
    }
}

@Composable private fun CalendarScreen(items:List<Subscription>,onOpen:(Subscription)->Unit,modifier:Modifier) {
    val start=LocalDate.now()
    val events=items.filterNot{it.archived}
        .flatMap { s -> BillingMath.upcoming(s,start,start.plusMonths(3)).map { d -> d to s } }
        .sortedBy{it.first}.take(120)
    LazyColumn(modifier,contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
        item { SectionTitle("Renewal calendar","Next 3 months of scheduled payments.") }
        if(events.isEmpty()) item { SurfaceCard(Modifier.fillMaxWidth()) { Text("No renewals scheduled in this period.",color=Muted) } }
        items(events) { (date,s) ->
            SurfaceCard(Modifier.fillMaxWidth().clickable{onOpen(s)}) {
                Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(15.dp)) {
                    Column(Modifier.width(63.dp),horizontalAlignment=Alignment.CenterHorizontally) {
                        Text(date.dayOfMonth.toString(),fontWeight=FontWeight.Bold,fontSize=26.sp,color=MaterialTheme.colorScheme.primary)
                        Text(date.month.name.take(3),color=Muted,fontSize=11.sp)
                    }
                    Column(Modifier.weight(1f)) {
                        Text(s.name,fontWeight=FontWeight.SemiBold)
                        Text(date.format(dateFormatter),color=Muted,fontSize=11.sp)
                    }
                    Text(BillingMath.format(s.amountCents),fontWeight=FontWeight.Bold)
                }
            }
        }
    }
}

@Composable private fun InsightsScreen(items:List<Subscription>,modifier:Modifier) {
    val active=items.filterNot { it.archived }
    val monthly=BillingMath.monthlyEquivalent(items)
    val sums=active.groupBy{it.category}.mapValues{(_,list)->BillingMath.monthlyEquivalent(list)}.toList().sortedByDescending{it.second}
    val context=LocalContext.current
    val prefs=remember { context.getSharedPreferences(ReminderScheduler.PREFS,Context.MODE_PRIVATE) }
    var budget by remember { mutableStateOf((prefs.getLong("budget_cents",10000L)/100).toString()) }
    LazyColumn(modifier,contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
        item { SectionTitle("Spending insights","Understand where your budget goes.") }
        item { SurfaceCard(Modifier.fillMaxWidth()) {
            Text("Recurring monthly equivalent",color=Muted)
            Text(BillingMath.format(monthly),fontWeight=FontWeight.ExtraBold,fontSize=33.sp)
            Text("Annual equivalent: ${BillingMath.format(monthly*12)}",color=Muted,fontSize=12.sp)
        } }
        item { Text("By category",fontWeight=FontWeight.Bold,fontSize=20.sp) }
        if(sums.isEmpty()) item { Text("Add a subscription to see a breakdown.",color=Muted) }
        items(sums) { (category,amount) ->
            SurfaceCard(Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
                    Text(category); Text(BillingMath.format(amount),fontWeight=FontWeight.SemiBold)
                }
                LinearProgressIndicator(progress={ if(monthly<=0) 0f else (amount.toFloat()/monthly).coerceIn(0f,1f) },
                    modifier=Modifier.fillMaxWidth(),color=MaterialTheme.colorScheme.primary,trackColor=Raised)
            }
        }
        if(isPro) {
            item { Text("Pro · Monthly budget",fontWeight=FontWeight.Bold,fontSize=20.sp) }
            item { SurfaceCard(Modifier.fillMaxWidth()) {
                Text("Set an estimated recurring-cost ceiling.",color=Muted,fontSize=13.sp)
                OutlinedTextField(value=budget,onValueChange={budget=it},label={Text("Monthly budget (EUR)")},
                    singleLine=true,keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Decimal))
                val cap=BillingMath.parse(budget)
                if(cap!=null && cap>0L) {
                    LinearProgressIndicator(progress={(monthly.toFloat()/cap).coerceIn(0f,1f)},modifier=Modifier.fillMaxWidth())
                    Text(if(monthly>cap) "Estimated spending exceeds your budget." else "Estimated spending is within your budget.",
                        color=if(monthly>cap) MaterialTheme.colorScheme.error else Muted,fontSize=12.sp)
                }
                Button(onClick={cap?.let{prefs.edit().putLong("budget_cents",it).apply()}},enabled=cap!=null && cap>0L) { Text("Save budget") }
            } }
        }
        item { Text("Figures are estimated from your records, not bank transactions. Weekly equivalents use 52/12 weeks.",color=Muted,fontSize=11.sp) }
    }
}

@Composable private fun EditScreen(existing:Subscription?,modifier:Modifier,onSave:(Subscription)->Unit) {
    val context=LocalContext.current
    var name by remember(existing?.id){mutableStateOf(existing?.name?:"")}
    var amount by remember(existing?.id){mutableStateOf(existing?.amountCents?.let{String.format(Locale.US,"%.2f",it/100.0)}?:"")}
    var cycle by remember(existing?.id){mutableStateOf(existing?.cycle?:BillingCycle.MONTHLY)}
    var category by remember(existing?.id){mutableStateOf(existing?.category?:"Other")}
    var due by remember(existing?.id){mutableStateOf(existing?.nextDue?:LocalDate.now().plusDays(7))}
    var note by remember(existing?.id){mutableStateOf(existing?.note?:"")}
    val amountCents=BillingMath.parse(amount)
    val valid=name.trim().length in 1..120 && amountCents!=null
    LazyColumn(modifier,contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(15.dp)) {
        item { SectionTitle(if(existing==null)"Add subscription" else "Edit subscription","Set up your recurring payment.") }
        item { OutlinedTextField(value=name,onValueChange={name=it.take(120)},label={Text("Service name")},singleLine=true,modifier=Modifier.fillMaxWidth()) }
        item { OutlinedTextField(value=amount,onValueChange={amount=it.take(18)},label={Text("Amount (EUR)")},
            singleLine=true,keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Decimal),modifier=Modifier.fillMaxWidth(),
            isError=amount.isNotEmpty() && amountCents==null,supportingText={Text("Use an amount with no more than 2 decimal places.")}) }
        item { Text("Billing frequency",fontWeight=FontWeight.SemiBold) }
        item { Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(8.dp)){
            BillingCycle.entries.forEach { f -> FilterChip(selected=cycle==f,onClick={cycle=f},label={Text(f.label)}) }
        } }
        item { Text("Category",fontWeight=FontWeight.SemiBold) }
        item { Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(8.dp)){
            categories.forEach { c->FilterChip(selected=category==c,onClick={category=c},label={Text(c)}) }
        } }
        item { OutlinedButton(onClick={
            DatePickerDialog(context,{ _,year,month,day->due=LocalDate.of(year,month+1,day)},due.year,due.monthValue-1,due.dayOfMonth).show()
        },modifier=Modifier.fillMaxWidth()) {
            Icon(Icons.Outlined.CalendarToday,null);Spacer(Modifier.width(10.dp))
            Text("Next renewal: ${due.format(dateFormatter)}")
        } }
        item { OutlinedTextField(value=note,onValueChange={note=it.take(500)},label={Text("Notes (optional)")},modifier=Modifier.fillMaxWidth(),minLines=2,maxLines=4) }
        item { Button(onClick={
            val cents=amountCents ?: return@Button
            val s=existing?.copy(name=name.trim(),amountCents=cents,cycle=cycle,category=category,nextDue=due,note=note,anchorDay=due.dayOfMonth)
                ?: Subscription(name=name.trim(),amountCents=cents,cycle=cycle,category=category,nextDue=due,note=note)
            onSave(s)
        },enabled=valid,modifier=Modifier.fillMaxWidth().heightIn(min=52.dp)) {
            Icon(Icons.Outlined.Check,null);Spacer(Modifier.width(8.dp));Text("Save subscription")
        } }
    }
}

@Composable private fun DetailScreen(item:Subscription,modifier:Modifier,onEdit:()->Unit,onArchive:()->Unit,onDelete:()->Unit,onPaid:()->Unit) {
    var confirmDelete by remember { mutableStateOf(false) }
    var confirmPaid by remember { mutableStateOf(false) }
    if(confirmDelete) AlertDialog(onDismissRequest={confirmDelete=false},title={Text("Delete subscription?")},
        text={Text("This deletes the subscription from this device. Previous payment records remain in history.")},
        confirmButton={TextButton(onClick={confirmDelete=false;onDelete()}){Text("Delete")}},
        dismissButton={TextButton(onClick={confirmDelete=false}){Text("Keep")}})
    if(confirmPaid) AlertDialog(onDismissRequest={confirmPaid=false},title={Text("Record this payment?")},
        text={Text("This adds a local payment record and moves the renewal date to the next cycle. It does not charge money.")},
        confirmButton={TextButton(onClick={confirmPaid=false;onPaid()}){Text("Record payment")}},
        dismissButton={TextButton(onClick={confirmPaid=false}){Text("Cancel")}})
    LazyColumn(modifier,contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(15.dp)) {
        item { SectionTitle(item.name,item.category) }
        item { SurfaceCard(Modifier.fillMaxWidth()) {
            Text("Next expected payment",color=Muted)
            Text(BillingMath.format(item.amountCents),fontSize=36.sp,fontWeight=FontWeight.ExtraBold)
            Text("${item.nextDue.format(dateFormatter)} · ${item.cycle.label}",color=Muted)
            if(item.archived) Text("Archived",color=MaterialTheme.colorScheme.primary)
        } }
        item { SurfaceCard(Modifier.fillMaxWidth()) {
            Text("Monthly equivalent",color=Muted)
            Text(BillingMath.format(BillingMath.monthly(item)),fontWeight=FontWeight.Bold,fontSize=22.sp)
            if(item.note.isNotBlank()) { HorizontalDivider();Text(item.note) }
        } }
        item { Button(onClick=onEdit,modifier=Modifier.fillMaxWidth()) { Icon(Icons.Outlined.Edit,null);Spacer(Modifier.width(8.dp));Text("Edit subscription") } }
        if(!item.archived) item { OutlinedButton(onClick={confirmPaid=true},modifier=Modifier.fillMaxWidth()) {
            Icon(Icons.Outlined.CheckCircle,null);Spacer(Modifier.width(8.dp));Text("Record payment")
        } }
        item { OutlinedButton(onClick=onArchive,modifier=Modifier.fillMaxWidth()) {
            Icon(Icons.Outlined.Inventory2,null);Spacer(Modifier.width(8.dp));Text(if(item.archived)"Restore subscription" else "Archive subscription")
        } }
        item { TextButton(onClick={confirmDelete=true},modifier=Modifier.fillMaxWidth()) {
            Icon(Icons.Outlined.Delete,null);Spacer(Modifier.width(8.dp));Text("Delete subscription",color=MaterialTheme.colorScheme.error)
        } }
        item { Text("Archive and Delete only change records in SubliX. Cancel subscriptions directly with their providers.",fontSize=11.sp,color=Muted) }
    }
}

@Composable private fun SettingsScreen(modifier:Modifier,onHistory:()->Unit,onArchive:()->Unit,onAbout:()->Unit,onExport:()->Unit,onImport:()->Unit) {
    val context=LocalContext.current
    var enabled by remember { mutableStateOf(ReminderScheduler.isEnabled(context)) }
    val permissionLauncher=rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        enabled=granted
        ReminderScheduler.setEnabled(context,granted)
    }
    val row:@Composable (String,String,()->Unit)->Unit={title,subtitle,onClick->
        SurfaceCard(Modifier.fillMaxWidth().clickable(onClick=onClick)){
            Text(title,fontWeight=FontWeight.SemiBold)
            Text(subtitle,color=Muted,fontSize=12.sp)
        }
    }
    LazyColumn(modifier,contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
        item { SectionTitle("Settings","Private. Offline. Your choice.") }
        item { SurfaceCard(Modifier.fillMaxWidth()) {
            Row(verticalAlignment=Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) { Text("Renewal reminders",fontWeight=FontWeight.SemiBold);Text("Local notifications, approximate scheduling",color=Muted,fontSize=12.sp) }
                Switch(checked=enabled,onCheckedChange={ on ->
                    if(on && !ReminderScheduler.hasPermission(context) && Build.VERSION.SDK_INT>=33) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else { enabled=on;ReminderScheduler.setEnabled(context,on) }
                })
            }
        } }
        item { Text("Your data",fontWeight=FontWeight.Bold,fontSize=19.sp) }
        item { row("Export JSON backup","Manual, unencrypted backup. Store it securely.",onExport) }
        item { row("Import JSON backup","Merge a SubliX backup into this device.",onImport) }
        item { row("Payment history","Only payments you explicitly recorded.",onHistory) }
        item { row("Archived subscriptions","Restore or review old plans.",onArchive) }
        item { Text("Information",fontWeight=FontWeight.Bold,fontSize=19.sp) }
        item { row("Privacy & About","SubliX by BRENDIGO · Edition ${if(isPro)"Pro" else "Free"}",onAbout) }
        item { Text("Both editions are local-only. The exported JSON file is not encrypted; do not share it publicly.",color=Muted,fontSize=12.sp) }
    }
}

@Composable private fun HistoryScreen(records:List<PaidRecord>,modifier:Modifier) {
    LazyColumn(modifier,contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
        item { SectionTitle("Payment history","Only transactions you marked as paid.") }
        if(records.isEmpty()) item { SurfaceCard(Modifier.fillMaxWidth()){Text("No confirmed payments yet.",color=Muted)} }
        items(records,key={it.id}) { record -> SurfaceCard(Modifier.fillMaxWidth()){
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
                Column {Text(record.subscriptionName,fontWeight=FontWeight.SemiBold);Text(record.paidOn.format(dateFormatter),color=Muted,fontSize=12.sp)}
                Text(BillingMath.format(record.amountCents),fontWeight=FontWeight.Bold)
            }
        } }
    }
}

@Composable private fun ArchiveScreen(plans:List<Subscription>,modifier:Modifier,onRestore:(Subscription)->Unit,onOpen:(Subscription)->Unit) {
    LazyColumn(modifier,contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
        item { SectionTitle("Archive","Inactive subscriptions.") }
        if(plans.isEmpty()) item { SurfaceCard(Modifier.fillMaxWidth()){Text("Nothing archived yet.",color=Muted)} }
        items(plans,key={it.id}) { plan ->
            SurfaceCard(Modifier.fillMaxWidth()) {
                PlanRow(plan){onOpen(plan)}
                OutlinedButton(onClick={onRestore(plan)}){Text("Restore")}
            }
        }
    }
}

@Composable private fun AboutScreen(modifier:Modifier) {
    LazyColumn(modifier,contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(14.dp)) {
        item { SectionTitle("About SubliX","Every subscription. Under control.") }
        item { SurfaceCard(Modifier.fillMaxWidth()) {
            Text("SubliX ${if(isPro)"Pro" else "Free"}",fontWeight=FontWeight.Bold,fontSize=23.sp)
            Text("Version ${BuildConfig.VERSION_NAME}",color=Muted)
            Text("by BRENDIGO",fontWeight=FontWeight.SemiBold,color=MaterialTheme.colorScheme.primary)
        } }
        item { SurfaceCard(Modifier.fillMaxWidth()) {
            Text("Privacy",fontWeight=FontWeight.Bold)
            Text("Records stay on this device. No sign-in, banking access, ad SDKs or tracking. Reminders are optional. Android system backups for app data are disabled.",color=Muted,fontSize=13.sp)
            Text("Exports are unencrypted JSON files and should be stored securely.",color=Muted,fontSize=13.sp)
        } }
        item { SurfaceCard(Modifier.fillMaxWidth()) {
            Text("Important",fontWeight=FontWeight.Bold)
            Text("SubliX does not cancel external subscriptions, process payments or synchronize with providers. Dates and amounts are based on what you entered.",color=Muted,fontSize=13.sp)
        } }
        item { Text("© 2026 BRENDIGO · brendigo.com",color=Muted,fontSize=12.sp) }
    }
}
