package com.brendigo.sublix.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.time.LocalDate

/** Device-local records. Never auto-backup financial data to an unknown account. */
class SubscriptionsDb(context: Context): SQLiteOpenHelper(context.applicationContext, "sublix.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE subscriptions (id TEXT PRIMARY KEY NOT NULL, name TEXT NOT NULL, amount_cents INTEGER NOT NULL CHECK(amount_cents >= 0), next_due TEXT NOT NULL, cycle TEXT NOT NULL, category TEXT NOT NULL, note TEXT NOT NULL, archived INTEGER NOT NULL DEFAULT 0, anchor_day INTEGER NOT NULL CHECK(anchor_day BETWEEN 1 AND 31))")
        db.execSQL("CREATE TABLE payments (id INTEGER PRIMARY KEY AUTOINCREMENT, subscription_name TEXT NOT NULL, amount_cents INTEGER NOT NULL, paid_on TEXT NOT NULL)")
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Add explicit non-destructive SQL migrations when incrementing database version.
        if (oldVersion != newVersion) error("Missing migration from $oldVersion to $newVersion")
    }
    fun subscriptions(): List<Subscription> = readableDatabase.rawQuery("SELECT id,name,amount_cents,next_due,cycle,category,note,archived,anchor_day FROM subscriptions ORDER BY archived, next_due, name", null).use { cur ->
        buildList {
            while (cur.moveToNext()) add(Subscription(
                id=cur.getString(0), name=cur.getString(1), amountCents=cur.getLong(2),
                nextDue=LocalDate.parse(cur.getString(3)), cycle=BillingCycle.valueOf(cur.getString(4)),
                category=cur.getString(5), note=cur.getString(6), archived=cur.getInt(7)!=0, anchorDay=cur.getInt(8)))
        }
    }
    fun payments(): List<PaidRecord> = readableDatabase.rawQuery("SELECT id,subscription_name,amount_cents,paid_on FROM payments ORDER BY paid_on DESC,id DESC LIMIT 1000", null).use { cur ->
        buildList { while (cur.moveToNext()) add(PaidRecord(cur.getLong(0),cur.getString(1),cur.getLong(2),LocalDate.parse(cur.getString(3)))) }
    }
    fun save(item: Subscription) {
        val v=ContentValues().apply {
            put("id",item.id); put("name",item.name); put("amount_cents",item.amountCents)
            put("next_due",item.nextDue.toString()); put("cycle",item.cycle.name)
            put("category",item.category); put("note",item.note); put("archived",if (item.archived) 1 else 0); put("anchor_day",item.anchorDay)
        }
        writableDatabase.insertWithOnConflict("subscriptions", null, v, SQLiteDatabase.CONFLICT_REPLACE)
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
