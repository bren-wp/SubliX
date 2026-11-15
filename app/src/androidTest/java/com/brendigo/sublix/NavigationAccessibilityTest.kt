package com.brendigo.sublix

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Real Compose interactions on an Android emulator, never a physical-device claim. */
@RunWith(AndroidJUnit4::class)
class NavigationAccessibilityTest {
    @get:Rule val rule = createAndroidComposeRule<MainActivity>()

    @Test fun homeAndPlansAreAccessibleAndNavigable() {
        rule.onNodeWithContentDescription("Settings").assertHasClickAction()
        rule.onNodeWithContentDescription("Add subscription").assertHasClickAction()
        rule.onNodeWithContentDescription("Plans").performClick()
        rule.onNodeWithText("Subscriptions").assertIsDisplayed()
        rule.onNodeWithText("Search subscriptions").assertExists()
        rule.onNodeWithText("Archived").performClick()
    }

    @Test fun archiveIsAccessibleFromSettings() {
        rule.onNodeWithContentDescription("Settings").performClick()
        rule.onNodeWithText("Archived subscriptions").performScrollTo().performClick()
        rule.onNodeWithText("Archive").assertIsDisplayed()
    }

    @Test fun addSubscriptionButtonRequiresValidInput() {
        rule.onNodeWithContentDescription("Add subscription").performClick()
        rule.onNodeWithText("Add subscription").assertIsDisplayed()
        rule.onNodeWithText("Save subscription").performScrollTo().assertIsNotEnabled()
    }

    @Test fun proForecastIsAccessibleInProEdition() {
        if (BuildConfig.FLAVOR != "pro") return
        rule.onNodeWithContentDescription("Settings").performClick()
        rule.onNodeWithText("SubliX Pro").performScrollTo().performClick()
        rule.onNodeWithText("12-month payment forecast").performClick()
        rule.onNodeWithText("Payment forecast").assertIsDisplayed()
    }
}
