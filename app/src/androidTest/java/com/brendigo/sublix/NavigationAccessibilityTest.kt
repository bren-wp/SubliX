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

    @Test fun archiveSearchAndClearSurviveActivityRecreation() {
        rule.onNodeWithContentDescription("Settings").performClick()
        rule.onNodeWithTag("settings-list")
            .performScrollToNode(hasTestTag("settings-archived-subscriptions"))
        rule.onNodeWithTag("settings-archived-subscriptions").performClick()
        rule.onNodeWithTag("archive-search").performTextInput("family")
        // Confirm input reached the parent saveable state before Activity teardown.
        rule.onNodeWithTag("archive-search").assertTextContains("family")
        rule.waitForIdle()
        rule.activityRule.scenario.recreate()
        rule.onNodeWithTag("archive-search").assertTextContains("family")
        rule.onNodeWithTag("archive-list")
            .performScrollToNode(hasTestTag("archive-clear"))
        rule.onNodeWithTag("archive-clear").performClick()
        rule.onNodeWithTag("archive-clear").assertDoesNotExist()
    }

    @Test fun archiveSearchPersistsWhenLeavingAndReenteringTheScreen() {
        rule.onNodeWithContentDescription("Settings").performClick()
        rule.onNodeWithTag("settings-list")
            .performScrollToNode(hasTestTag("settings-archived-subscriptions"))
        rule.onNodeWithTag("settings-archived-subscriptions").performClick()
        rule.onNodeWithTag("archive-search").performTextInput("preserve")
        rule.onNodeWithTag("archive-search").assertTextContains("preserve")
        rule.onNodeWithContentDescription("Back").performClick()
        rule.onNodeWithTag("settings-list")
            .performScrollToNode(hasTestTag("settings-archived-subscriptions"))
        rule.onNodeWithTag("settings-archived-subscriptions").performClick()
        rule.onNodeWithTag("archive-search").assertTextContains("preserve")
        rule.onNodeWithTag("archive-list")
            .performScrollToNode(hasTestTag("archive-clear"))
        rule.onNodeWithTag("archive-clear").performClick()
        rule.onNodeWithTag("archive-clear").assertDoesNotExist()
    }

    @Test fun calendarSelectionSurvivesActivityRecreation() {
        rule.onNodeWithTag("tab-calendar").performClick()
        rule.onNodeWithTag("calendar-this").performClick()
        rule.onNodeWithTag("calendar-selected-period").assertTextContains("Showing: This month")
        rule.activityRule.scenario.recreate()
        rule.onNodeWithTag("calendar-selected-period").assertTextContains("Showing: This month")
        rule.onNodeWithTag("calendar-all").performClick()
        rule.onNodeWithTag("calendar-selected-period").assertDoesNotExist()
    }

    @Test fun serviceLibrarySearchAndCustomFallbackSurviveRecreation() {
        rule.onNodeWithTag("tab-plans").performClick()
        rule.onNodeWithText("Browse service library").performClick()
        rule.onNodeWithTag("library-search").performTextInput("provider-no-match")
        rule.activityRule.scenario.recreate()
        rule.onNodeWithTag("library-search").assertTextContains("provider-no-match")
        rule.onNodeWithTag("library-list").performScrollToNode(hasTestTag("library-use-custom"))
        rule.onNodeWithTag("library-use-custom").performClick()
        rule.onNodeWithTag("edit-name").assertExists()
    }

    @Test fun libraryBackReturnsToPlansInsteadOfUnsavedEditor() {
        rule.onNodeWithTag("tab-plans").performClick()
        rule.onNodeWithText("Browse service library").performClick()
        rule.onNodeWithText("Service library").assertIsDisplayed()
        rule.onNodeWithContentDescription("Back").performClick()
        rule.onNodeWithText("Subscriptions").assertIsDisplayed()
        rule.onNodeWithTag("tab-plans").assertExists()
    }

    @Test fun nestedRouteHistorySurvivesActivityRecreation() {
        rule.onNodeWithContentDescription("Settings").performClick()
        rule.onNodeWithTag("settings-list")
            .performScrollToNode(hasTestTag("settings-archived-subscriptions"))
        rule.onNodeWithTag("settings-archived-subscriptions").performClick()
        rule.activityRule.scenario.recreate()
        rule.onNodeWithText("Archive").assertIsDisplayed()
        rule.onNodeWithContentDescription("Back").performClick()
        rule.onNodeWithTag("settings-list").assertExists()
        rule.onNodeWithContentDescription("Back").performClick()
        rule.onNodeWithTag("tab-home").assertExists()
    }

    @Test fun androidHardwareBackUsesTheSameNavigationHistory() {
        rule.onNodeWithTag("tab-plans").performClick()
        rule.onNodeWithText("Browse service library").performClick()
        // The BackHandler callback is installed after Compose has committed
        // the destination. Observe the new route before dispatching system Back.
        rule.onNodeWithText("Service library").assertIsDisplayed()
        rule.waitForIdle()
        rule.activityRule.scenario.onActivity {
            check(it.onBackPressedDispatcher.hasEnabledCallbacks()) {
                "Secondary screen has no active Android Back callback"
            }
            it.onBackPressedDispatcher.onBackPressed()
        }
        rule.onNodeWithText("Subscriptions").assertIsDisplayed()
        rule.onNodeWithTag("tab-plans").assertExists()
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

    @Test fun freeEditionHasNoProPurchaseOrUpgradeNavigation() {
        if (BuildConfig.FLAVOR != "free") return
        rule.onNodeWithContentDescription("Settings").performClick()
        rule.onNodeWithTag("settings-sublix-pro").assertDoesNotExist()
        rule.onNodeWithTag("settings-advanced-tools").assertDoesNotExist()
        rule.onNodeWithTag("settings-list")
            .performScrollToNode(hasTestTag("settings-getting-started"))
        rule.onNodeWithTag("settings-getting-started").performClick()
        rule.onNodeWithText("Getting started").assertIsDisplayed()
        rule.onNodeWithText("Add a subscription").assertExists()
        rule.onNodeWithContentDescription("Back").performClick()
        rule.onNodeWithTag("settings-list")
            .performScrollToNode(hasTestTag("settings-monthly-budget"))
        rule.onNodeWithTag("settings-monthly-budget").performClick()
        rule.onNodeWithText("Spending overview").assertIsDisplayed()
    }

    @Test fun proCategoryBudgetDraftSurvivesActivityRecreation() {
        if (BuildConfig.FLAVOR != "pro") return
        rule.onNodeWithContentDescription("Settings").performClick()
        rule.onNodeWithTag("settings-list")
            .performScrollToNode(hasTestTag("settings-advanced-tools"))
        rule.onNodeWithTag("settings-advanced-tools").performClick()
        rule.onNodeWithText("Category spending limits").performClick()
        rule.onNodeWithTag("category-limit-cloud").performTextInput("42.50")
        rule.onNodeWithTag("category-limit-cloud").assertTextContains("42.50")
        rule.waitForIdle()
        rule.activityRule.scenario.recreate()
        rule.onNodeWithTag("category-limit-cloud").assertTextContains("42.50")
    }

    @Test fun proForecastIsAccessibleInProEdition() {
        if (BuildConfig.FLAVOR != "pro") return
        rule.onNodeWithContentDescription("Settings").performClick()
        rule.onNodeWithTag("settings-list")
            .performScrollToNode(hasTestTag("settings-advanced-tools"))
        rule.onNodeWithTag("settings-advanced-tools").performClick()
        rule.onNodeWithText("12-month payment forecast").performClick()
        rule.onNodeWithText("Payment forecast").assertIsDisplayed()
    }
}
