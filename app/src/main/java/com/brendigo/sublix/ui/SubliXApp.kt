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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.brendigo.sublix.BuildConfig
import com.brendigo.sublix.data.*
import com.brendigo.sublix.notifications.ReminderScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

private enum class Screen { HOME, PLANS, CALENDAR, INSIGHTS, EDIT, LIBRARY, DETAILS, PAYMENT, REMINDERS, SETTINGS, CATEGORIES, HISTORY, ARCHIVE, BACKUP, BUDGET, SAVINGS, TRIALS, PRO, PRIVACY, ABOUT, PRO_FORECAST, PRO_CATEGORY_BUDGETS, PRO_PLANNER, PRO_REPORTS, PRO_PRICE_HISTORY }
private val primaryScreens = listOf(Screen.HOME, Screen.PLANS, Screen.CALENDAR, Screen.INSIGHTS)
private val contentShape = RoundedCornerShape(20.dp)
private val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
private val categories = listOf("Entertainment", "Productivity", "Cloud", "Utilities", "Health", "Learning", "Other")
private val isPro: Boolean get() = BuildConfig.FLAVOR == "pro"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubliXApp(startScreen: String? = null, vm: SubscriptionViewModel = viewModel()) {
    val plans by vm.subscriptions.collectAsStateWithLifecycle()
    val payments by vm.payments.collectAsStateWithLifecycle()
    val priceChanges by vm.priceEvents.collectAsStateWithLifecycle()
    var screen by rememberSaveable(startScreen) { mutableStateOf(Screen.entries.firstOrNull { it.name.equals(startScreen, ignoreCase = true) } ?: Screen.HOME) }
    var selectedId by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedPaymentId by rememberSaveable { mutableStateOf<Long?>(null) }
    var presetName by rememberSaveable { mutableStateOf("") }
    var presetCategory by rememberSaveable { mutableStateOf("Other") }
    val selected=plans.firstOrNull { it.id==selectedId } ?: if (startScreen == "DETAILS") plans.firstOrNull { !it.archived } else null
    val selectedPayment=payments.firstOrNull { it.id==selectedPaymentId } ?: if (startScreen == "PAYMENT") payments.firstOrNull() else null
    val scope=rememberCoroutineScope()
    val snackbar=remember { SnackbarHostState() }
    val context=LocalContext.current
    val notify:(String)->Unit={ message->scope.launch { snackbar.showSnackbar(message) } }
    val exporter=rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if(uri!=null) {
            val snapshot = plans.toList()
            scope.launch {
                val result = withContext(Dispatchers.IO) {
                    runCatching {
                        context.contentResolver.openOutputStream(uri)?.use { output ->
                            output.write(BackupCodec.encode(snapshot).toByteArray(Charsets.UTF_8))
                        } ?: error("Cannot open destination")
                    }
                }
                result.onSuccess { notify("Backup saved") }
                    .onFailure { notify("Export failed. Choose another destination.") }
            }
        }
    }
    val importer=rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if(uri!=null) scope.launch {
            val decoded = withContext(Dispatchers.IO) {
                runCatching {
                    val input=context.contentResolver.openInputStream(uri) ?: error("File not found")
                    BackupCodec.decode(input.use { readLimited(it,BackupCodec.MAX_BYTES) })
                }
            }
            decoded.onSuccess { items ->
                vm.importItems(items) { outcome ->
                    outcome.onSuccess { count -> notify("Imported $count subscriptions") }
                        .onFailure { notify("Import failed. No changes were made.") }
                }
            }.onFailure { notify("Invalid or unsupported backup") }
        }
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
                navigationIcon = { if(screen !in primaryScreens) IconButton(onClick={screen=when(screen) {
                    Screen.PRO_FORECAST,Screen.PRO_CATEGORY_BUDGETS,Screen.PRO_PLANNER,Screen.PRO_REPORTS,Screen.PRO_PRICE_HISTORY -> Screen.PRO
                    Screen.EDIT -> if(selected!=null) Screen.DETAILS else Screen.PLANS
                    Screen.LIBRARY -> Screen.EDIT
                    Screen.PAYMENT -> Screen.HISTORY
                    Screen.DETAILS -> Screen.PLANS
                    else -> Screen.SETTINGS
                }}) {
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
                        modifier=Modifier.testTag("tab-${target.name.lowercase(Locale.ROOT)}"),
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
                selectedId=null;presetName="";presetCategory="Other";screen=Screen.EDIT
            },containerColor=MaterialTheme.colorScheme.primary,contentColor=MaterialTheme.colorScheme.onPrimary) {
                Icon(Icons.Outlined.Add,contentDescription="Add subscription")
            }
        },
        snackbarHost={SnackbarHost(snackbar)}
    ) { scaffoldPadding ->
        Box(Modifier.fillMaxSize().padding(scaffoldPadding),contentAlignment=Alignment.TopCenter) {
            val pageModifier=Modifier.fillMaxSize().widthIn(max=850.dp)
            when(screen) {
                Screen.HOME -> HomeScreen(plans,onOpen={selectedId=it.id;screen=Screen.DETAILS},pageModifier,onProTools={screen=Screen.PRO})
                Screen.PLANS -> PlansScreen(plans,onOpen={selectedId=it.id;screen=Screen.DETAILS},pageModifier,onLibrary={screen=Screen.LIBRARY})
                Screen.CALENDAR -> CalendarScreen(plans,onOpen={selectedId=it.id;screen=Screen.DETAILS},pageModifier)
                Screen.INSIGHTS -> InsightsScreen(plans,pageModifier,onProTools={screen=Screen.PRO})
                Screen.EDIT -> EditScreen(existing=selected,modifier=pageModifier,initialName=presetName,initialCategory=presetCategory,onLibrary={screen=Screen.LIBRARY},onSave={
                    vm.save(it);selectedId=it.id;screen=Screen.DETAILS;notify("Subscription saved")
                })
                Screen.LIBRARY -> ServiceLibraryScreen(pageModifier,onChoose={name,category -> presetName=name;presetCategory=category;selectedId=null;screen=Screen.EDIT})
                Screen.DETAILS -> if(selected!=null) DetailScreen(selected!!,pageModifier,
                    onEdit={screen=Screen.EDIT},
                    onArchive={vm.save(selected!!.copy(archived=!selected!!.archived));screen=Screen.PLANS},
                    onDelete={vm.delete(selected!!.id);selectedId=null;screen=Screen.PLANS},
                    onPaid={vm.paid(selected!!);notify("Payment recorded")}
                ) else EmptyScreen("Subscription not found",pageModifier)
                Screen.PAYMENT -> PaymentDetailsScreen(selectedPayment,pageModifier)
                Screen.REMINDERS -> ReminderSettingsScreen(pageModifier)
                Screen.SETTINGS -> SettingsScreen(
                    modifier=pageModifier,
                    onNavigate={screen=it},
                    onExport={exporter.launch("sublix-backup.json")},
                    onImport={importer.launch(arrayOf("application/json","text/plain","application/octet-stream"))}
                )
                Screen.CATEGORIES -> CategoriesScreen(pageModifier)
                Screen.HISTORY -> HistoryScreen(payments,pageModifier,onOpen={selectedPaymentId=it.id;screen=Screen.PAYMENT})
                Screen.ARCHIVE -> ArchiveScreen(plans.filter { it.archived },pageModifier,
                    onRestore={vm.save(it.copy(archived=false))},onOpen={selectedId=it.id;screen=Screen.DETAILS})
                Screen.BACKUP -> BackupScreen(pageModifier,onExport={exporter.launch("sublix-backup.json")},onImport={importer.launch(arrayOf("application/json","text/plain","application/octet-stream"))})
                Screen.BUDGET -> BudgetScreen(plans,pageModifier)
                Screen.SAVINGS -> SavingsScreen(plans,pageModifier,onSave=vm::save)
                Screen.TRIALS -> TrialTrackerScreen(plans,pageModifier,onOpen={selectedId=it.id;screen=Screen.DETAILS})
                Screen.PRO -> if(isPro) ProSuiteHome(pageModifier,
                    onForecast={screen=Screen.PRO_FORECAST},onCategoryBudgets={screen=Screen.PRO_CATEGORY_BUDGETS},
                    onPlanner={screen=Screen.PRO_PLANNER},onReports={screen=Screen.PRO_REPORTS},
                    onPriceHistory={screen=Screen.PRO_PRICE_HISTORY}) else ProScreen(pageModifier)
                Screen.PRO_FORECAST -> if(isPro) ProForecastScreen(plans,pageModifier) else ProScreen(pageModifier)
                Screen.PRO_CATEGORY_BUDGETS -> if(isPro) ProCategoryBudgetScreen(plans,pageModifier) else ProScreen(pageModifier)
                Screen.PRO_PLANNER -> if(isPro) ProWhatIfScreen(plans,pageModifier) else ProScreen(pageModifier)
                Screen.PRO_REPORTS -> if(isPro) ProReportsScreen(plans,payments,pageModifier) else ProScreen(pageModifier)
                Screen.PRO_PRICE_HISTORY -> if(isPro) ProPriceHistoryScreen(priceChanges,pageModifier) else ProScreen(pageModifier)
                Screen.PRIVACY -> PrivacyScreen(pageModifier)
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

@Composable private fun PlanRow(item:Subscription,dueDate:LocalDate=item.nextDue,onClick:()->Unit) {
    SurfaceCard(Modifier.fillMaxWidth()
        .semantics { contentDescription = "${item.name}, ${BillingMath.format(item.amountCents)}, ${item.category}, ${item.cycle.label}, next ${dueDate.format(dateFormatter)}"; role = Role.Button }
        .clickable(onClick=onClick,role=Role.Button)) {
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            // Keep text legible at narrow widths and larger system font scales.
            val compact = maxWidth < 390.dp
            Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                Box(Modifier.size(46.dp).clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha=.16f)),
                    contentAlignment=Alignment.Center) {
                    Text(item.name.take(1).uppercase(Locale.ENGLISH),
                        fontWeight=FontWeight.ExtraBold,color=MaterialTheme.colorScheme.primary,fontSize=20.sp)
                }
                Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(2.dp)) {
                    Text(item.name,fontWeight=FontWeight.SemiBold,maxLines=1,overflow=TextOverflow.Ellipsis)
                    Text("${item.category} · ${item.cycle.label}",color=Muted,fontSize=12.sp,
                        maxLines=1,overflow=TextOverflow.Ellipsis)
                    if(compact) Text(BillingMath.format(item.amountCents),fontSize=15.sp,
                        fontWeight=FontWeight.Bold,maxLines=1,overflow=TextOverflow.Ellipsis)
                    Text("Next ${dueDate.format(dateFormatter)}",color=Muted,fontSize=11.sp,
                        maxLines=1,overflow=TextOverflow.Ellipsis)
                }
                if(!compact) Text(BillingMath.format(item.amountCents),
                    fontWeight=FontWeight.Bold,fontSize=15.sp,maxLines=1,
                    overflow=TextOverflow.Ellipsis)
                Icon(Icons.Outlined.ChevronRight,contentDescription=null,tint=Muted)
            }
        }
    }
}

@Composable private fun HomeScreen(items:List<Subscription>,onOpen:(Subscription)->Unit,modifier:Modifier,onProTools:()->Unit) {
    val today=AppClock.today()
    val active=items.filterNot { it.archived }
    val monthly=BillingMath.monthlyEquivalent(items)
    val next=SubscriptionPresentation.upcomingCharges(items,today,today.plusDays(45),limit=8)
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
                Text(BillingMath.format(monthly),fontWeight=FontWeight.ExtraBold,
                    fontSize=when {
                        BillingMath.format(monthly).length>15 -> 29.sp
                        BillingMath.format(monthly).length>12 -> 35.sp
                        else -> 43.sp
                    },color=Color.White,maxLines=1,overflow=TextOverflow.Ellipsis)
                HorizontalDivider(color=Color.White.copy(alpha=.19f))
                Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
                    Column(Modifier.weight(1f)) {
                         Text("Annual equivalent",fontSize=11.sp,color=Color(0xFFD9D2F0))
                         Text(BillingMath.format(monthly*12),fontWeight=FontWeight.SemiBold,maxLines=1,overflow=TextOverflow.Ellipsis)
                     }
                    Column(Modifier.weight(1f),horizontalAlignment=Alignment.End) {
                         Text("Upcoming this month",fontSize=11.sp,color=Color(0xFFD9D2F0),maxLines=1,overflow=TextOverflow.Ellipsis)
                         Text(BillingMath.format(dueThisMonth),fontWeight=FontWeight.SemiBold,maxLines=1,overflow=TextOverflow.Ellipsis)
                     }
                }
            }
        }
        if(isPro) item {
            SurfaceCard(Modifier.fillMaxWidth().clickable(onClick=onProTools)) {
                Row(verticalAlignment=Alignment.CenterVertically) {
                    Icon(Icons.Outlined.AutoGraph,"Pro tools",tint=MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("SubliX Pro toolkit",fontWeight=FontWeight.Bold)
                        Text("Forecasts, budgets, scenarios, reports, price history",fontSize=12.sp,color=Muted)
                    }
                    Icon(Icons.Outlined.ChevronRight,null,tint=Muted)
                }
            }
        }
        item {
            Row(modifier=Modifier.fillMaxWidth(),
                verticalAlignment=Alignment.CenterVertically,
                horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                Text("Upcoming payments",modifier=Modifier.weight(1f),fontWeight=FontWeight.Bold,
                    fontSize=19.sp,maxLines=2)
                Text("${active.size} active",color=Muted,fontSize=12.sp,maxLines=1)
            }
        }
        if(next.isEmpty()) item { SurfaceCard(Modifier.fillMaxWidth()) { Text("No upcoming renewals. Add a subscription to get started.",color=Muted) } }
        items(next,key={it.subscription.id+":"+it.due.toString()}) { charge ->
            PlanRow(charge.subscription,onClick={onOpen(charge.subscription)},dueDate=charge.due)
        }
        item { Text("Amounts are forecasts based on your entries; external services may charge differently.",fontSize=11.sp,color=Muted) }
    }
}

@Composable private fun PlansScreen(items:List<Subscription>,onOpen:(Subscription)->Unit,modifier:Modifier,onLibrary:()->Unit) {
    var search by remember { mutableStateOf("") }
    var showArchived by remember { mutableStateOf(false) }
    var sortBy by remember { mutableStateOf("Next payment") }
    var categoryFilter by remember { mutableStateOf("All") }
    val categories=remember(items){items.map{it.category}.distinct().sorted()}
    val visible=items.filter{it.archived==showArchived && it.name.contains(search,ignoreCase=true)
        && (!isPro || categoryFilter=="All" || it.category==categoryFilter)}
        .let{ filtered->if(!isPro) filtered else when(sortBy) {
            "Highest cost"->filtered.sortedByDescending { BillingMath.monthly(it) }
            "Lowest cost"->filtered.sortedBy { BillingMath.monthly(it) }
            "Name"->filtered.sortedBy { it.name.lowercase() }
            else->filtered.sortedBy{it.nextDue}
        } }
    LazyColumn(modifier,contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
        item { SectionTitle("Subscriptions","All your plans in one place.") }
        item { OutlinedButton(onClick=onLibrary,modifier=Modifier.fillMaxWidth()) {Icon(Icons.Outlined.LibraryAdd,null);Spacer(Modifier.width(8.dp));Text("Browse service library")} }
        item { OutlinedTextField(value=search,onValueChange={search=it},label={Text("Search subscriptions")},
            modifier=Modifier.fillMaxWidth(),singleLine=true,leadingIcon={Icon(Icons.Outlined.Search,null)}) }
        item { Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            FilterChip(selected=!showArchived,onClick={showArchived=false},label={Text("Active")})
            FilterChip(selected=showArchived,onClick={showArchived=true},label={Text("Archived")})
        } }
        if(isPro) item {
            Column(verticalArrangement=Arrangement.spacedBy(6.dp)) {
                Text("Pro filters",fontSize=12.sp,color=MaterialTheme.colorScheme.primary,fontWeight=FontWeight.SemiBold)
                Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                    listOf("Next payment","Highest cost","Lowest cost","Name").forEach{option->
                        FilterChip(selected=sortBy==option,onClick={sortBy=option},label={Text(option)})
                    }
                }
                Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                    listOf("All").plus(categories).forEach{option->
                        FilterChip(selected=categoryFilter==option,onClick={categoryFilter=option},label={Text(option)})
                    }
                }
            }
        }
        if(visible.isEmpty()) item { SurfaceCard(Modifier.fillMaxWidth()) { Text("No matching subscriptions.",color=Muted) } }
        items(visible,key={it.id}) { item -> PlanRow(item){onOpen(item)} }
    }
}

@Composable private fun CalendarScreen(items:List<Subscription>,onOpen:(Subscription)->Unit,modifier:Modifier) {
    val start=AppClock.today()
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

@Composable private fun InsightsScreen(items:List<Subscription>,modifier:Modifier,onProTools:()->Unit) {
    val active=items.filterNot { it.archived }
    val monthly=BillingMath.monthlyEquivalent(items)
    val sums=active.groupBy{it.category}.mapValues{(_,list)->BillingMath.monthlyEquivalent(list)}.toList().sortedByDescending{it.second}
    val context=LocalContext.current
    val prefs=remember { context.getSharedPreferences(ReminderScheduler.PREFS,Context.MODE_PRIVATE) }
    var budget by remember { mutableStateOf((prefs.getLong("budget_cents",10000L)/100).toString()) }
    LazyColumn(modifier,contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
        item { SectionTitle("Spending insights","Understand where your budget goes.") }
        if(isPro) item { OutlinedButton(onClick=onProTools,modifier=Modifier.fillMaxWidth()){Icon(Icons.Outlined.AutoGraph,null);Spacer(Modifier.width(8.dp));Text("Open advanced Pro toolkit")} }
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

@Composable private fun EditScreen(existing:Subscription?,modifier:Modifier,initialName:String,initialCategory:String,onLibrary:()->Unit,onSave:(Subscription)->Unit) {
    val context=LocalContext.current
    var name by rememberSaveable(existing?.id,initialName){mutableStateOf(existing?.name?:initialName)}
    var amount by rememberSaveable(existing?.id){mutableStateOf(existing?.amountCents?.let{String.format(Locale.US,"%.2f",it/100.0)}?:"")}
    var cycle by rememberSaveable(existing?.id){mutableStateOf(existing?.cycle?:BillingCycle.MONTHLY)}
    var category by rememberSaveable(existing?.id,initialCategory){mutableStateOf(existing?.category?:initialCategory)}
    val availableCategories=remember(existing?.id,initialCategory) {allCategories(context)}
    var trialDate by rememberSaveable(existing?.id){mutableStateOf(existing?.trialEnd)}
    var cancelUrl by rememberSaveable(existing?.id){mutableStateOf(existing?.cancellationUrl?:"")}
    var usage by rememberSaveable(existing?.id){mutableStateOf(existing?.usage?:Usage.UNKNOWN)}
    var due by rememberSaveable(existing?.id){mutableStateOf(existing?.nextDue?:AppClock.today().plusDays(7))}
    var note by rememberSaveable(existing?.id){mutableStateOf(existing?.note?:"")}
    val amountCents=BillingMath.parse(amount)
    val valid=name.trim().length in 1..120 && amountCents!=null && (cancelUrl.isBlank() || (cancelUrl.startsWith("https://") && cancelUrl.length <= 512))
    LazyColumn(modifier.testTag("edit-form"),contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(15.dp)) {
        item { SectionTitle(if(existing==null)"Add subscription" else "Edit subscription","Set up your recurring payment.") }
        if(existing==null) item { OutlinedButton(onClick=onLibrary,modifier=Modifier.fillMaxWidth()) {Text("Choose a service template")} }
        item { OutlinedTextField(value=name,onValueChange={name=it.take(120)},label={Text("Service name")},singleLine=true,modifier=Modifier.fillMaxWidth().testTag("edit-name")) }
        item { OutlinedTextField(value=amount,onValueChange={amount=it.take(18)},label={Text("Amount (EUR)")},
            singleLine=true,keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Decimal),modifier=Modifier.fillMaxWidth().testTag("edit-amount"),
            isError=amount.isNotEmpty() && amountCents==null,supportingText={Text("Use an amount with no more than 2 decimal places.")}) }
        item { Text("Billing frequency",fontWeight=FontWeight.SemiBold) }
        item { Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(8.dp)){
            BillingCycle.entries.forEach { f -> FilterChip(selected=cycle==f,onClick={cycle=f},label={Text(f.label)}) }
        } }
        item { Text("Category",fontWeight=FontWeight.SemiBold) }
        item { Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(8.dp)){
            availableCategories.forEach { c->FilterChip(selected=category==c,onClick={category=c},label={Text(c)}) }
        } }
        item { OutlinedButton(onClick={
            DatePickerDialog(context,{ _,year,month,day->due=LocalDate.of(year,month+1,day)},due.year,due.monthValue-1,due.dayOfMonth).show()
        },modifier=Modifier.fillMaxWidth()) {
            Icon(Icons.Outlined.CalendarToday,null);Spacer(Modifier.width(10.dp))
            Text("Next renewal: ${due.format(dateFormatter)}")
        } }
        item { OutlinedButton(onClick={
            val initial=trialDate?:due
            DatePickerDialog(context,{ _,year,month,day->trialDate=LocalDate.of(year,month+1,day)},initial.year,initial.monthValue-1,initial.dayOfMonth).show()
        },modifier=Modifier.fillMaxWidth()) {
            Icon(Icons.Outlined.Timer,null);Spacer(Modifier.width(8.dp))
            Text("Trial end: ${trialDate?.format(dateFormatter) ?: "Not set"}")
        } }
        if(trialDate!=null) item{ TextButton(onClick={trialDate=null},modifier=Modifier.fillMaxWidth()){Text("Remove trial end date")} }
        item { OutlinedTextField(value=cancelUrl,onValueChange={cancelUrl=it.take(512)},label={Text("Manage/cancel URL (https:// only)")},
            supportingText={Text("Opens only when you choose; does not automatically cancel.")},isError=cancelUrl.isNotBlank()&&!cancelUrl.startsWith("https://"),
            singleLine=true,modifier=Modifier.fillMaxWidth()) }
        item { OutlinedTextField(value=note,onValueChange={note=it.take(500)},label={Text("Notes (optional)")},modifier=Modifier.fillMaxWidth(),minLines=2,maxLines=4) }
        item { Button(onClick={
            val cents=amountCents ?: return@Button
            val s=existing?.copy(name=name.trim(),amountCents=cents,cycle=cycle,category=category,nextDue=due,note=note,anchorDay=due.dayOfMonth,trialEnd=trialDate,cancellationUrl=cancelUrl.trim(),usage=usage)
                ?: Subscription(name=name.trim(),amountCents=cents,cycle=cycle,category=category,nextDue=due,note=note,trialEnd=trialDate,cancellationUrl=cancelUrl.trim(),usage=usage)
            onSave(s)
        },enabled=valid,modifier=Modifier.fillMaxWidth().heightIn(min=52.dp).testTag("save-subscription")) {
            Icon(Icons.Outlined.Check,null);Spacer(Modifier.width(8.dp));Text("Save subscription")
        } }
    }
}

@Composable private fun DetailScreen(item:Subscription,modifier:Modifier,onEdit:()->Unit,onArchive:()->Unit,onDelete:()->Unit,onPaid:()->Unit) {
    val context=LocalContext.current
    val safeManagementUrl=item.cancellationUrl.takeIf { url ->
        url.startsWith("https://") && runCatching {
            val uri=android.net.Uri.parse(url)
            uri.scheme == "https" && !uri.host.isNullOrBlank()
        }.getOrDefault(false)
    }
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
        if(safeManagementUrl!=null) item {
            OutlinedButton(onClick={
                try {
                    val open=android.content.Intent(android.content.Intent.ACTION_VIEW,android.net.Uri.parse(safeManagementUrl))
                    open.addCategory(android.content.Intent.CATEGORY_BROWSABLE)
                    context.startActivity(open)
                } catch (_:android.content.ActivityNotFoundException) {
                    android.widget.Toast.makeText(context,"No browser is available.",android.widget.Toast.LENGTH_SHORT).show()
                } catch (_:SecurityException) {
                    android.widget.Toast.makeText(context,"The link could not be opened.",android.widget.Toast.LENGTH_SHORT).show()
                }
            },modifier=Modifier.fillMaxWidth()) {
                Icon(Icons.Outlined.OpenInNew,null)
                Spacer(Modifier.width(8.dp))
                Text("Open provider management page")
            }
        }
        item { OutlinedButton(onClick=onArchive,modifier=Modifier.fillMaxWidth()) {
            Icon(Icons.Outlined.Inventory2,null);Spacer(Modifier.width(8.dp));Text(if(item.archived)"Restore subscription" else "Archive subscription")
        } }
        item { TextButton(onClick={confirmDelete=true},modifier=Modifier.fillMaxWidth()) {
            Icon(Icons.Outlined.Delete,null);Spacer(Modifier.width(8.dp));Text("Delete subscription",color=MaterialTheme.colorScheme.error)
        } }
        item { Text("Archive and Delete only change records in SubliX. Cancel subscriptions directly with their providers.",fontSize=11.sp,color=Muted) }
    }
}

@Composable private fun SettingsScreen(modifier:Modifier,onNavigate:(Screen)->Unit,onExport:()->Unit,onImport:()->Unit) {
    val row:@Composable (String,String,()->Unit)->Unit={title,subtitle,onClick->
        SurfaceCard(Modifier.fillMaxWidth()
            .testTag("settings-${title.lowercase(Locale.ROOT).replace(" ","-")}")
            .clickable(onClick=onClick)){
            Text(title,fontWeight=FontWeight.SemiBold)
            Text(subtitle,color=Muted,fontSize=12.sp)
        }
    }
    LazyColumn(modifier.testTag("settings-list"),contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
        item { SectionTitle("Settings","Private. Offline. Your choice.") }
        item { Text("MY SUBLIX",fontSize=11.sp,color=MaterialTheme.colorScheme.primary,fontWeight=FontWeight.Bold) }
        item { row("Service library","Choose a template for a new plan.",{onNavigate(Screen.LIBRARY)}) }
        item { row("Categories","Organize your subscriptions.",{onNavigate(Screen.CATEGORIES)}) }
        item { row("Reminders","Configure notification timing.",{onNavigate(Screen.REMINDERS)}) }
        item { row("Trial tracker","See recorded trial end dates.",{onNavigate(Screen.TRIALS)}) }
        item { row("Monthly budget","Review monthly limits.",{onNavigate(Screen.BUDGET)}) }
        item { row("Savings insights","Compare low-usage subscriptions.",{onNavigate(Screen.SAVINGS)}) }
        item { Text("YOUR DATA",fontSize=11.sp,color=MaterialTheme.colorScheme.primary,fontWeight=FontWeight.Bold) }
        item { row("Backup & restore","Choose a local export/import destination.",{onNavigate(Screen.BACKUP)}) }
        item { row("Payment history","Payments recorded manually.",{onNavigate(Screen.HISTORY)}) }
        item { row("Archived subscriptions","Restore old subscriptions.",{onNavigate(Screen.ARCHIVE)}) }
        item { Text("INFORMATION",fontSize=11.sp,color=MaterialTheme.colorScheme.primary,fontWeight=FontWeight.Bold) }
        item { row("SubliX Pro",if(isPro)"Forecast, category budgets, reports, planner and price history." else "See Pro edition features.",{onNavigate(Screen.PRO)}) }
        item { row("Privacy","Understand exactly what stays on device.",{onNavigate(Screen.PRIVACY)}) }
        item { row("About","Version, publisher and notices.",{onNavigate(Screen.ABOUT)}) }
        item { Text("No account required in either edition. SubliX does not process payments.",color=Muted,fontSize=12.sp) }
    }
}

@Composable private fun HistoryScreen(records:List<PaidRecord>,modifier:Modifier,onOpen:(PaidRecord)->Unit) {
    LazyColumn(modifier,contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
        item { SectionTitle("Payment history","Only transactions you marked as paid.") }
        if(records.isEmpty()) item { SurfaceCard(Modifier.fillMaxWidth()){Text("No confirmed payments yet.",color=Muted)} }
        items(records,key={it.id}) { record -> SurfaceCard(Modifier.fillMaxWidth().clickable{onOpen(record)}){
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
                Column {Text(record.subscriptionName,fontWeight=FontWeight.SemiBold);Text(record.paidOn.format(dateFormatter),color=Muted,fontSize=12.sp)}
                Text(BillingMath.format(record.amountCents),fontWeight=FontWeight.Bold)
            }
        } }
    }
}

@Composable private fun ArchiveScreen(
    plans:List<Subscription>,
    modifier:Modifier,
    onRestore:(Subscription)->Unit,
    onOpen:(Subscription)->Unit
) {
    LazyColumn(modifier,contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
        item { SectionTitle("Archive","Inactive subscriptions.") }
        if(plans.isEmpty()) item {
            SurfaceCard(Modifier.fillMaxWidth()) {
                Text("Nothing archived yet.",color=Muted)
            }
        }
        items(plans,key={it.id}) { plan ->
            // One card only. Nested cards previously doubled padding, distorted
            // the reference layout, and confused accessibility focus order.
            SurfaceCard(Modifier.fillMaxWidth()) {
                Row(verticalAlignment=Alignment.CenterVertically,
                    horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                    Box(Modifier.size(46.dp).clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha=.16f)),
                        contentAlignment=Alignment.Center) {
                        Text(plan.name.take(1).uppercase(Locale.ENGLISH),
                            fontSize=20.sp,fontWeight=FontWeight.Bold,
                            color=MaterialTheme.colorScheme.primary)
                    }
                    Column(Modifier.weight(1f)) {
                        Text(plan.name,fontWeight=FontWeight.Bold,maxLines=2,
                            overflow=TextOverflow.Ellipsis)
                        Text("${plan.category} · ${plan.cycle.label}",color=Muted,fontSize=12.sp,
                            maxLines=2)
                        Text(BillingMath.format(plan.amountCents),fontWeight=FontWeight.SemiBold,
                            maxLines=1,overflow=TextOverflow.Ellipsis)
                    }
                }
                Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick={onOpen(plan)},modifier=Modifier.weight(1f)
                        .heightIn(min=48.dp)
                        .semantics { contentDescription = "View ${plan.name} details" }) {
                        Text("Details")
                    }
                    Button(onClick={onRestore(plan)},modifier=Modifier.weight(1f)
                        .heightIn(min=48.dp)
                        .semantics { contentDescription = "Restore ${plan.name}" }) {
                        Text("Restore")
                    }
                }
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
