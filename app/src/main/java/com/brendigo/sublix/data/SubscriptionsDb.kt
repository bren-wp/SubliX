package com.brendigo.sublix.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.time.LocalDate

/** Device-local records. Never auto-backup financial data to an unknown account. */
class SubscriptionsDb(context: Context): SQLiteOpenHelper(context.applicationContext, "sublix.db", null, 3) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE subscriptions (id TEXT PRIMARY KEY NOT NULL, name TEXT NOT NULL, amount_cents INTEGER NOT NULL CHECK(amount_cents >= 0), next_due TEXT NOT NULL, cycle TEXT NOT NULL, category TEXT NOT NULL, note TEXT NOT NULL, archived INTEGER NOT NULL DEFAULT 0, anchor_day INTEGER NOT NULL CHECK(anchor_day BETWEEN 1 AND 31), trial_end TEXT, cancellation_url TEXT NOT NULL DEFAULT '', usage TEXT NOT NULL DEFAULT 'UNKNOWN')")
        db.execSQL("CREATE TABLE payments (id INTEGER PRIMARY KEY AUTOINCREMENT, subscription_name TEXT NOT NULL, amount_cents INTEGER NOT NULL, paid_on TEXT NOT NULL)")
        createPriceHistory(db)
    }
    private fun createPriceHistory(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE price_events (id INTEGER PRIMARY KEY AUTOINCREMENT, subscription_id TEXT NOT NULL, service_name TEXT NOT NULL, old_cents INTEGER NOT NULL, new_cents INTEGER NOT NULL, changed_on TEXT NOT NULL)")
        db.execSQL("CREATE INDEX idx_price_events_subscription ON price_events(subscription_id)")
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2 && newVersion >= 2) {
            db.execSQL("ALTER TABLE subscriptions ADD COLUMN trial_end TEXT")
            db.execSQL("ALTER TABLE subscriptions ADD COLUMN cancellation_url TEXT NOT NULL DEFAULT ''")
            db.execSQL("ALTER TABLE subscriptions ADD COLUMN usage TEXT NOT NULL DEFAULT 'UNKNOWN'")
        }
        if (oldVersion < 3 && newVersion >= 3) createPriceHistory(db)
        if (newVersion > 3 || oldVersion < 1) error("Missing database migration $oldVersion → $newVersion")
    }
    fun subscriptions(): List<Subscription> = readableDatabase.rawQuery("SELECT id,name,amount_cents,next_due,cycle,category,note,archived,anchor_day,trial_end,cancellation_url,usage FROM subscriptions ORDER BY archived, next_due, name", null).use { cur ->
        buildList {
            while (cur.moveToNext()) add(Subscription(
                id=cur.getString(0), name=cur.getString(1), amountCents=cur.getLong(2),
                nextDue=LocalDate.parse(cur.getString(3)), cycle=BillingCycle.valueOf(cur.getString(4)),
                category=cur.getString(5), note=cur.getString(6), archived=cur.getInt(7)!=0, anchorDay=cur.getInt(8),
                trialEnd=cur.getString(9)?.let(LocalDate::parse), cancellationUrl=cur.getString(10) ?: "",
                usage=runCatching { Usage.valueOf(cur.getString(11)) }.getOrDefault(Usage.UNKNOWN)))
        }
    }
    fun payments(): List<PaidRecord> = readableDatabase.rawQuery("SELECT id,subscription_name,amount_cents,paid_on FROM payments ORDER BY paid_on DESC,id DESC LIMIT 1000", null).use { cur ->
        buildList { while (cur.moveToNext()) add(PaidRecord(cur.getLong(0),cur.getString(1),cur.getLong(2),LocalDate.parse(cur.getString(3)))) }
    }
    fun priceEvents(): List<PriceEvent> = readableDatabase.rawQuery(
        "SELECT id,subscription_id,service_name,old_cents,new_cents,changed_on FROM price_events ORDER BY changed_on DESC,id DESC LIMIT 1000",null
    ).use { c ->
        buildList {
            while(c.moveToNext()) add(PriceEvent(c.getLong(0),c.getString(1),c.getString(2),
                c.getLong(3),c.getLong(4),LocalDate.parse(c.getString(5))))
        }
    }

    fun save(item: Subscription) {
        val db=writableDatabase
        val values=ContentValues().apply {
            put("id",item.id);put("name",item.name);put("amount_cents",item.amountCents)
            put("next_due",item.nextDue.toString());put("cycle",item.cycle.name)
            put("category",item.category);put("note",item.note);put("archived",if(item.archived)1 else 0)
            put("anchor_day",item.anchorDay);put("trial_end",item.trialEnd?.toString())
            put("cancellation_url",item.cancellationUrl);put("usage",item.usage.name)
        }
        db.beginTransaction()
        try {
            val previous=db.rawQuery("SELECT amount_cents FROM subscriptions WHERE id=?", arrayOf(item.id)).use { cursor ->
                if(cursor.moveToFirst()) cursor.getLong(0) else null
            }
            val result=db.insertWithOnConflict("subscriptions",null,values,SQLiteDatabase.CONFLICT_REPLACE)
            check(result != -1L) { "Unable to store subscription" }
            if(previous!=null && previous!=item.amountCents) {
                db.insertOrThrow("price_events",null,ContentValues().apply {
                    put("subscription_id",item.id);put("service_name",item.name)
                    put("old_cents",previous);put("new_cents",item.amountCents);put("changed_on",LocalDate.now().toString())
                })
            }
            db.setTransactionSuccessful()
        } finally { db.endTransaction() }
    }
    fun delete(id: String) { writableDatabase.delete("subscriptions","id=?", arrayOf(id)) }
    fun markPaid(item: Subscription, paidOn: LocalDate) {
        val db=writableDatabase
        db.beginTransaction()
        try {
            db.insertOrThrow("payments",null,ContentValues().apply { put("subscription_name",item.name);put("amount_cents",item.amountCents);put("paid_on",paidOn.toString()) })
            val updated=item.copy(nextDue=BillingMath.nextOccurrence(item,1))
            val values=ContentValues().apply { put("next_due",updated.nextDue.toString()) }
            db.update("subscriptions",values,"id=?",arrayOf(item.id))
            db.setTransactionSuccessful()
        } finally { db.endTransaction() }
    }
}
