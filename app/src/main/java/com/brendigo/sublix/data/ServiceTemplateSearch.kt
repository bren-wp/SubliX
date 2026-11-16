package com.brendigo.sublix.data

import java.text.Normalizer
import java.util.Locale

/** Entirely offline multi-token template search with accent and case folding. */
object ServiceTemplateSearch {
    private fun normalized(text: String): String =
        Normalizer.normalize(text, Normalizer.Form.NFD)
            .replace(Regex("\\p{M}+"), "")
            .lowercase(Locale.ROOT)

    fun find(templates: List<Pair<String, String>>, query: String): List<Pair<String, String>> {
        val words = normalized(query).trim().split(Regex("\\s+"))
            .filter { it.isNotEmpty() }
        if (words.isEmpty()) return templates
        return templates.filter { (name, category) ->
            val haystack = normalized(name) + "\n" + normalized(category)
            words.all(haystack::contains)
        }
    }
}
