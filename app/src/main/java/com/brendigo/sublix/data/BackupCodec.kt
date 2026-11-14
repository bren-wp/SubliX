package com.brendigo.sublix.data

import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate

/** Explicit, versioned offline backup; never includes accounts or access credentials. */
object BackupCodec {
    const val MAX_BYTES = 2_000_000
    fun encode(items: List<Subscription>): String = JSONObject().apply {
        put("format","sublix-backup")
        put("version",1)
        put("subscriptions",JSONArray().apply { items.forEach { s -> put(JSONObject().apply {
            put("id",s.id);put("name",s.name);put("amountCents",s.amountCents);put("nextDue",s.nextDue.toString());
            put("cycle",s.cycle.name);put("category",s.category);put("note",s.note);put("archived",s.archived)
        }) } })
    }.toString(2)
    fun decode(content: String): List<Subscription> {
        require(content.toByteArray(Charsets.UTF_8).size <= MAX_BYTES) { "File is too large" }
        val data=JSONObject(content)
        require(data.getString("format")=="sublix-backup" && data.getInt("version")==1) { "Unsupported backup format" }
        val items=data.getJSONArray("subscriptions")
        require(items.length()<=5000) { "Too many subscriptions" }
        val ids=mutableSetOf<String>()
        return (0 until items.length()).map { i ->
            val x=items.getJSONObject(i)
            val id=x.getString("id"); val name=x.getString("name").trim();val cents=x.getLong("amountCents")
            require(id.length in 1..100 && ids.add(id) && name.length in 1..120 && cents in 0..99_999_999_999) { "Invalid subscription entry" }
            val category=x.optString("category","Other").take(80);val note=x.optString("note","").take(500)
            Subscription(id,name,cents,LocalDate.parse(x.getString("nextDue")),BillingCycle.valueOf(x.getString("cycle")),category,note,x.optBoolean("archived",false))
        }
    }
}
