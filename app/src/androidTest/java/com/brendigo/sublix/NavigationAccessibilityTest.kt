package com.brendigo.sublix

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
        rule.onNodeWithTag("tab-plans").performClick()
        rule.onNodeWithText("Subscriptions").assertIsDisplayed()
        rule.onNodeWithText("Search subscriptions").assertExists()
        rule.onNodeWithText("Archived").performClick()
    }

    @Test fun archiveIsAccessibleFromSettings() {
        rule.onNodeWithContentDescription("Settings").performClick()
        rule.onNodeWithTag("settings-list")
            .performScrollToNode(hasTestTag("settings-archived-subscriptions"))
        rule.onNodeWithTag("settings-archived-subscriptions").performClick()
        rule.onNodeWithText("Archive").assertIsDisplayed()
    }

    @Test fun addSubscriptionButtonRequiresValidInput() {
        rule.onNodeWithContentDescription("Add subscription").performClick()
        rule.onNodeWithText("Add subscription").assertIsDisplayed()
        rule.onNodeWithTag("edit-form").performScrollToNode(hasTestTag("save-subscription"))
        rule.onNodeWithTag("save-subscription").assertIsNotEnabled()
    }

    @Test fun proForecastIsAccessibleInProEdition() {
        if (BuildConfig.FLAVOR != "pro") return
        rule.onNodeWithContentDescription("Settings").performClick()
        rule.onNodeWithTag("settings-list")
            .performScrollToNode(hasTestTag("settings-sublix-pro"))
        rule.onNodeWithTag("settings-sublix-pro").performClick()
        rule.onNodeWithText("12-month payment forecast").performClick()
        rule.onNodeWithText("Payment forecast").assertIsDisplayed()
    }
}
