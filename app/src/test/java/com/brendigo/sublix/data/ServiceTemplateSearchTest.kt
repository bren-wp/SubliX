package com.brendigo.sublix.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ServiceTemplateSearchTest {
    private val templates = listOf(
        "Učenje online" to "Obrazovanje",
        "Streaming" to "Entertainment",
        "Cloud storage" to "Cloud"
    )

    @Test fun blankSearchKeepsTemplateOrder() {
        assertEquals(templates, ServiceTemplateSearch.find(templates, "  "))
    }

    @Test fun nameAndCategoryTokensIgnoreCaseAndAccents() {
        assertEquals(listOf(templates[0]),
            ServiceTemplateSearch.find(templates, "UCENJE  OBRAZOVANJE"))
        assertEquals(listOf(templates[2]),
            ServiceTemplateSearch.find(templates, "storage CLOUD"))
        assertTrue(ServiceTemplateSearch.find(templates, "no-such-provider").isEmpty())
    }
}
