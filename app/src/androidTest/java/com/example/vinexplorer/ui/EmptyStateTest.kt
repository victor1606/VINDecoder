package com.example.vinexplorer.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.vinexplorer.ui.components.EmptyState
import com.example.vinexplorer.ui.components.NoResultsState
import com.example.vinexplorer.ui.theme.VinExplorerTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI tests for Empty State components
 */
@RunWith(AndroidJUnit4::class)
class EmptyStateTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun emptyState_displaysTitleAndSubtitle() {
        composeTestRule.setContent {
            VinExplorerTheme {
                EmptyState(
                    icon = Icons.Default.Favorite,
                    title = "No favorites yet",
                    subtitle = "Tap the heart icon on any VIN to save it here"
                )
            }
        }

        // Then: Title and subtitle should be displayed
        composeTestRule.onNodeWithText("No favorites yet").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tap the heart icon on any VIN to save it here").assertIsDisplayed()
    }

    @Test
    fun emptyState_displaysTitleOnly_whenSubtitleIsNull() {
        composeTestRule.setContent {
            VinExplorerTheme {
                EmptyState(
                    icon = Icons.Default.Favorite,
                    title = "No favorites yet",
                    subtitle = null
                )
            }
        }

        // Then: Only title should be displayed
        composeTestRule.onNodeWithText("No favorites yet").assertIsDisplayed()
    }

    @Test
    fun noResultsState_displaysSearchQuery() {
        composeTestRule.setContent {
            VinExplorerTheme {
                NoResultsState(query = "Honda")
            }
        }

        // Then: Should show the search query in message
        composeTestRule.onNodeWithText("No results found").assertIsDisplayed()
        composeTestRule.onNodeWithText("No VINs match \"Honda\"").assertIsDisplayed()
    }
}

