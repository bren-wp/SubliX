package com.brendigo.sublix.data

import java.util.Base64
import java.util.Locale

/**
 * Preference keys for Pro category budgets.
 *
 * The v1 32-bit String.hashCode scheme can alias different categories.
 * V2 is a one-to-one URL-safe Base64 encoding of the normalized UTF-8 name.
 */
object CategoryBudgetKeys {
    fun current(category: String): String {
        val normalized = category.lowercase(Locale.ROOT).toByteArray(Charsets.UTF_8)
        return "limit_v2_" + Base64.getUrlEncoder().withoutPadding().encodeToString(normalized)
    }

    // Historical implementation used the device's default locale. Retain its
    // exact format when reading existing preferences; never use it for new writes.
    fun legacy(category: String): String =
        "limit_" + category.lowercase().hashCode().toUInt().toString(16)

    fun ambiguousLegacyKeys(categories: Collection<String>): Set<String> =
        categories.groupBy(::legacy).filterValues { it.size > 1 }.keys
}
