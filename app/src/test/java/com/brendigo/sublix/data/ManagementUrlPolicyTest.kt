package com.brendigo.sublix.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ManagementUrlPolicyTest {
    @Test fun acceptsOrdinaryHttpsManagementLinks() {
        assertTrue(ManagementUrlPolicy.isValid("https://provider.example/account/billing"))
        assertTrue(ManagementUrlPolicy.isValid("https://provider.example:8443/manage?q=one%20two#details"))
        assertTrue(ManagementUrlPolicy.isOptionalValid(""))
    }

    @Test fun rejectsMalformedOrUnsafeLinks() {
        listOf(
            "https://",
            "https:///account",
            "http://provider.example/account",
            "javascript:alert(1)",
            "https://user:password@provider.example/account",
            "https://provider.example\\@evil.example/",
            "https://provider.example/a b",
            "https://provider.example/\nHeader: injected",
            " https://provider.example/",
            "https://provider.example/ ",
            "https://provider.example:wrong/account",
            "https://provider.example/" + "a".repeat(520)
        ).forEach { link ->
            assertFalse("Unexpectedly accepted: " + link, ManagementUrlPolicy.isValid(link))
            assertFalse("Unexpectedly accepted as optional: " + link,
                ManagementUrlPolicy.isOptionalValid(link))
        }
    }

    @Test fun optionalBlankIsAllowedForPlansWithoutManagementLinks() {
        assertTrue(ManagementUrlPolicy.isOptionalValid(""))
        assertTrue(ManagementUrlPolicy.isOptionalValid("   "))
        assertFalse(ManagementUrlPolicy.isValid(""))
    }
}
