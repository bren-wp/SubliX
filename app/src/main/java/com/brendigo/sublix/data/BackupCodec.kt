package com.brendigo.sublix.data

import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate

/** Explicit, versioned offline backup; never includes accounts or access credentials. */
object BackupCodec {
    const val MAX_BYTES = 2_000_000
    fun encode(items: List<Subscription>): String = JSONObject().apply {
        put("format","sublix-backup")
        put("version",2)
        put("subscriptions",JSONArray().apply { items.forEach { s -> put(JSONObject().apply {
            put("id",s.id);put("name",s.name);put("amountCents",s.amountCents);put("nextDue",s.nextDue.toString());
            put("cycle",s.cycle.name);put("category",s.category);put("note",s.note);put("archived",s.archived);put("anchorDay",s.anchorDay);put("trialEnd",s.trialEnd?.toString());put("cancellationUrl",s.cancellationUrl);put("usage",s.usage.name)
        }) } })
    }.toString(2)
    fun decode(content: String): List<Subscription> {
        require(content.toByteArray(Charsets.UTF_8).size <= MAX_BYTES) { "File is too large" }
        val data=JSONObject(content)
        require(data.getString("format")=="sublix-backup" && data.getInt("version") in 1..2) { "Unsupported backup format" }
        val items=data.getJSONArray("subscriptions")
        require(items.length()<=5000) { "Too many subscriptions" }
        val ids=mutableSetOf<String>()
        return (0 until items.length()).map { i ->
            val x=items.getJSONObject(i)
            val id=x.getString("id"); val name=x.getString("name").trim();val cents=x.getLong("amountCents")
            require(id.length in 1..100 && !id.any(Char::isISOControl) && ids.add(id) &&
                name.length in 1..120 && !name.any(Char::isISOControl) && cents in 0L..99_999_999_999L) {
                "Invalid subscription entry"
            }
            // Never silently truncate untrusted imports: a backup must either
            // import faithfully or fail atomically without changing local data.
            val category=x.optString("category","Other")
            val note=x.optString("note","")
            require(category.isNotBlank() && category.length<=80 && !category.any(Char::isISOControl) &&
                note.length<=500 && !note.any { it == '\u0000' }) { "Invalid backup text" }
            Subscription(id,name,cents,LocalDate.parse(x.getString("nextDue")),BillingCycle.valueOf(x.getString("cycle")),category,note,x.optBoolean("archived",false),x.optInt("anchorDay",LocalDate.parse(x.getString("nextDue")).dayOfMonth).also { require(it in 1..31) },
                x.optString("trialEnd").takeIf { it.isNotBlank() && it != "null" }?.let(LocalDate::parse),
                x.optString("cancellationUrl","").also { require(ManagementUrlPolicy.isOptionalValid(it)) { "Invalid management URL" } },
                runCatching { Usage.valueOf(x.optString("usage","UNKNOWN")) }.getOrDefault(Usage.UNKNOWN))
        }
    }
}
