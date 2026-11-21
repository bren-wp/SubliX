package com.brendigo.sublix.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Locale

class CategoryBudgetKeysTest {
    @Test fun distinctCategoriesNeverShareNewKeyWhenLegacyHashesCollide() {
        val first="ajspybqz"
        val second="olelwpsn"
        assertNotEquals(first,second)
        assertEquals(CategoryBudgetKeys.legacy(first),CategoryBudgetKeys.legacy(second))
        assertNotEquals(CategoryBudgetKeys.current(first),CategoryBudgetKeys.current(second))
        assertTrue(CategoryBudgetKeys.legacy(first) in
            CategoryBudgetKeys.ambiguousLegacyKeys(listOf(first,second)))
    }

    @Test fun categoryKeysAreCaseInsensitiveButNotLocaleDependent() {
        val original=Locale.getDefault()
        try {
            val english=CategoryBudgetKeys.current("INTERNET")
            Locale.setDefault(Locale.forLanguageTag("tr-TR"))
            assertEquals(english,CategoryBudgetKeys.current("internet"))
            assertEquals(english,CategoryBudgetKeys.current("INTERNET"))
        } finally {
            Locale.setDefault(original)
        }
    }

    @Test fun similarButDifferentUnicodeNamesKeepIndependentBudgets() {
        val categories=listOf("Media", "Media & apps", "Health", "Healthcare", "Média", "Medía")
        val keys=categories.map(CategoryBudgetKeys::current)
        assertEquals(keys.size,keys.toSet().size)
        assertFalse(CategoryBudgetKeys.current("aa").contains("/"))
        assertFalse(CategoryBudgetKeys.current("aa").contains("="))
    }
}
