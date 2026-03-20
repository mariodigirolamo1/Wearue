package com.clothesmatcher

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.clothesmatcher.data.FavoriteStyle
import com.clothesmatcher.ui.MainMenuScreen
import com.clothesmatcher.ui.theme.ClothesMatcherTheme
import org.junit.Rule
import org.junit.Test

class MainMenuScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun startMatchingButton_navigatesToCategories() {
        var navigated = false
        composeTestRule.setContent {
            ClothesMatcherTheme {
                MainMenuScreen(
                    onMakeMatchClick = { navigated = true },
                    onViewFavoritesClick = {}
                )
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("start_matching_card").performClick()
        assert(navigated)
    }

    @Test
    fun noFavorites_displaysEmptyCard_andNavigatesToCategories() {
        var navigated = false
        composeTestRule.setContent {
            ClothesMatcherTheme {
                MainMenuScreen(
                    onMakeMatchClick = { navigated = true },
                    onViewFavoritesClick = {},
                    favoritesOverride = emptyList()
                )
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("empty_favorites_card").assertIsDisplayed()
        composeTestRule.onNodeWithTag("empty_favorites_card").performClick()
        assert(navigated)
    }

    @Test
    fun favoritesAvailable_1to3_displaysCorrectCountAndIcons() {
        val favs = listOf(
            FavoriteStyle("id1", "timeless_ivy", listOf("shirts", "trousers")),
            FavoriteStyle("id2", "cyber_street", listOf("shoes", "accessories")),
            FavoriteStyle("id3", "modern_minimal", listOf("shirts", "shoes"))
        )
        
        var navigatedToFavorites = false
        
        composeTestRule.setContent {
            ClothesMatcherTheme {
                MainMenuScreen(
                    onMakeMatchClick = {},
                    onViewFavoritesClick = { navigatedToFavorites = true },
                    favoritesOverride = favs
                )
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("favorites_card").assertIsDisplayed()
        
        // Use useUnmergedTree = true because favorites_card is clickable and merges its children
        composeTestRule.onNodeWithTag("favorites_count_text", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag("favorites_count_text", useUnmergedTree = true).assertTextEquals("3 saved")
        
        // Check first 3 icons are there
        composeTestRule.onNodeWithTag("fav_preview_id1", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag("fav_preview_id2", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag("fav_preview_id3", useUnmergedTree = true).assertIsDisplayed()
        
        // "More" icon should NOT be there
        composeTestRule.onNodeWithTag("fav_more_icon", useUnmergedTree = true).assertDoesNotExist()

        composeTestRule.onNodeWithTag("favorites_card").performClick()
        assert(navigatedToFavorites)
    }

    @Test
    fun favoritesAvailable_moreThan3_displaysEllipses() {
        // Use valid palette IDs from Palettes.all to ensure items are composed
        val favs = listOf(
            FavoriteStyle("id1", "timeless_ivy", listOf("shirts")),
            FavoriteStyle("id2", "monochrome_office", listOf("trousers")),
            FavoriteStyle("id3", "executive_blue", listOf("shoes")),
            FavoriteStyle("id4", "urban_rugged", listOf("accessories"))
        )
        
        composeTestRule.setContent {
            ClothesMatcherTheme {
                MainMenuScreen(
                    onMakeMatchClick = {},
                    onViewFavoritesClick = {},
                    favoritesOverride = favs
                )
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("favorites_card").assertIsDisplayed()
        
        // Use useUnmergedTree = true because favorites_card is clickable and merges its children
        composeTestRule.onNodeWithTag("favorites_count_text", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag("favorites_count_text", useUnmergedTree = true).assertTextEquals("4 saved")
        
        // Check only first 3 icons are there
        composeTestRule.onNodeWithTag("fav_preview_id1", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag("fav_preview_id2", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag("fav_preview_id3", useUnmergedTree = true).assertIsDisplayed()
        
        // The 4th item ID icon should NOT be displayed directly (limit is 3 + ellipses)
        composeTestRule.onNodeWithTag("fav_preview_id4", useUnmergedTree = true).assertDoesNotExist()
        
        // "More" icon (ellipses) SHOULD be there
        composeTestRule.onNodeWithTag("fav_more_icon", useUnmergedTree = true).assertIsDisplayed()
    }
}
