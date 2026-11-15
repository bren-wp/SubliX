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
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.assertTextContains
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

    @Test fun planQuickFiltersAndSearchSurviveActivityRecreation() {
        rule.onNodeWithTag("tab-plans").performClick()
        rule.onNodeWithTag("quick-due-soon").performClick()
        rule.onNodeWithTag("plan-search").performTextInput("cloud")
        rule.activityRule.scenario.recreate()
        rule.onNodeWithTag("plan-search").assertTextContains("cloud")
        rule.onNodeWithTag("quick-due-soon").assertExists()
        rule.onNodeWithTag("plan-list")
            .performScrollToNode(hasTestTag("clear-plan-filters"))
        rule.onNodeWithTag("clear-plan-filters").performClick()
        rule.onNodeWithTag("quick-all").assertExists()
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

    @Test fun unsavedSubscriptionDraftSurvivesActivityRecreation() {
        rule.onNodeWithContentDescription("Add subscription").performClick()
        rule.onNodeWithTag("edit-name").performTextInput("Draft before rotation")
        rule.onNodeWithTag("edit-amount").performTextInput("12.50")
        rule.activityRule.scenario.recreate()
        rule.onNodeWithTag("edit-name").assertTextContains("Draft before rotation")
        rule.onNodeWithTag("edit-amount").assertTextContains("12.50")
        rule.onNodeWithTag("edit-form")
            .performScrollToNode(hasTestTag("save-subscription"))
        rule.onNodeWithTag("save-subscription").assertExists()
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
