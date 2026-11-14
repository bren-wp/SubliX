package com.brendigo.sublix.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import androidx.work.WorkManager
import com.brendigo.sublix.data.BillingMath
import com.brendigo.sublix.data.SubscriptionsDb
import java.time.LocalDate
import java.util.concurrent.TimeUnit

object ReminderScheduler {
    const val KEY = "reminders_enabled"
    const val PREFS = "sublix_prefs"
    private const val CHANNEL = "renewals"
    fun initialize(context: Context) {
        val mgr=context.getSystemService(NotificationManager::class.java)
        mgr.createNotificationChannel(NotificationChannel(CHANNEL,"Upcoming subscription renewals",NotificationManager.IMPORTANCE_DEFAULT))
        val request=PeriodicWorkRequestBuilder<ReminderWorker>(24,TimeUnit.HOURS).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork("sublix-renewals",ExistingPeriodicWorkPolicy.KEEP,request)
    }
    fun isEnabled(context: Context)=context.getSharedPreferences(PREFS,Context.MODE_PRIVATE).getBoolean(KEY,false)
    fun setEnabled(context: Context,enabled: Boolean) { context.getSharedPreferences(PREFS,Context.MODE_PRIVATE).edit().putBoolean(KEY,enabled).apply() }
    fun hasPermission(context: Context)= Build.VERSION.SDK_INT<33 || ContextCompat.checkSelfPermission(context,Manifest.permission.POST_NOTIFICATIONS)==PackageManager.PERMISSION_GRANTED
    fun post(context: Context, id: Int, text: String) {
        if (!hasPermission(context)) return
        val message=NotificationCompat.Builder(context,CHANNEL)
            .setSmallIcon(com.brendigo.sublix.R.drawable.brand_icon)
            .setContentTitle("SubliX · Upcoming renewal")
            .setContentText(text).setAutoCancel(true).setPriority(NotificationCompat.PRIORITY_DEFAULT).build()
        try { NotificationManagerCompat.from(context).notify(id,message) }
        catch (_: SecurityException) { /* User may revoke notification permission at any time. */ }
    }
}

class ReminderWorker(context: Context, params: WorkerParameters): CoroutineWorker(context,params) {
    override suspend fun doWork(): Result {
        val context=applicationContext
        if (!ReminderScheduler.isEnabled(context) || !ReminderScheduler.hasPermission(context)) return Result.success()
        return try {
            val today=LocalDate.now()
            val prefs=context.getSharedPreferences("sublix_sent_reminders",Context.MODE_PRIVATE)
            val items=SubscriptionsDb(context).use { it.subscriptions() }
            for (s in items.filterNot { it.archived }) {
                val days=BillingMath.daysUntil(s,today) ?: continue
                if (days !in listOf(7L,3L,1L,0L)) continue
                val reminderKey="${s.id}:${s.nextDue}:${today}"
                if (prefs.getBoolean(reminderKey,false)) continue
                val label=when(days) { 0L->"today";1L->"tomorrow";else->"in $days days" }
                ReminderScheduler.post(context,s.id.hashCode(),"${s.name}: ${BillingMath.format(s.amountCents)} $label")
                prefs.edit().putBoolean(reminderKey,true).apply()
            }
            Result.success()
        } catch (_: Exception) { Result.retry() }
    }
}
