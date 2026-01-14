package com.example.vinexplorer.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.vinexplorer.data.model.DecodedVinEntity
import com.example.vinexplorer.ui.components.VinHistoryCard
import com.example.vinexplorer.ui.theme.VinExplorerTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI tests for VIN History Card component
 */
@RunWith(AndroidJUnit4::class)
class VinHistoryCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun vinHistoryCard_displaysVehicleInfo() {
        // Given: A VIN entity
        val entity = createTestEntity(
            vin = "1HGBH41JXMN109186",
            make = "Honda",
            model = "Accord",
            year = "2021"
        )

        // When: Displaying the card
        composeTestRule.setContent {
            VinExplorerTheme {
                VinHistoryCard(
                    entity = entity,
                    onClick = {},
                    onFavoriteClick = {}
                )
            }
        }

        // Then: The vehicle name should be displayed
        composeTestRule.onNodeWithText("2021 Honda Accord").assertIsDisplayed()
    }

    @Test
    fun vinHistoryCard_displaysVinCode() {
        // Given: A VIN entity
        val entity = createTestEntity(vin = "1HGBH41JXMN109186")

        // When: Displaying the card
        composeTestRule.setContent {
            VinExplorerTheme {
                VinHistoryCard(
                    entity = entity,
                    onClick = {},
                    onFavoriteClick = {}
                )
            }
        }

        // Then: The VIN should be displayed
        composeTestRule.onNodeWithText("1HGBH41JXMN109186").assertIsDisplayed()
    }

    @Test
    fun vinHistoryCard_clickTriggersCallback() {
        var clicked = false
        val entity = createTestEntity(vin = "1HGBH41JXMN109186")

        composeTestRule.setContent {
            VinExplorerTheme {
                VinHistoryCard(
                    entity = entity,
                    onClick = { clicked = true },
                    onFavoriteClick = {}
                )
            }
        }

        // When: Clicking the card
        composeTestRule.onNodeWithText("2021 Honda Accord").performClick()

        // Then: The callback should be triggered
        assert(clicked)
    }

    @Test
    fun vinHistoryCard_favoriteButtonExists() {
        val entity = createTestEntity(vin = "1HGBH41JXMN109186")

        composeTestRule.setContent {
            VinExplorerTheme {
                VinHistoryCard(
                    entity = entity,
                    onClick = {},
                    onFavoriteClick = {}
                )
            }
        }

        // Then: The favorite button should exist
        composeTestRule.onNodeWithContentDescription("Add to favorites").assertExists()
    }

    @Test
    fun vinHistoryCard_showsRemoveFromFavorites_whenIsFavorite() {
        val entity = createTestEntity(vin = "1HGBH41JXMN109186", isFavorite = true)

        composeTestRule.setContent {
            VinExplorerTheme {
                VinHistoryCard(
                    entity = entity,
                    onClick = {},
                    onFavoriteClick = {}
                )
            }
        }

        // Then: Should show "Remove from favorites"
        composeTestRule.onNodeWithContentDescription("Remove from favorites").assertExists()
    }

    private fun createTestEntity(
        vin: String,
        make: String = "Honda",
        model: String = "Accord",
        year: String = "2021",
        isFavorite: Boolean = false
    ): DecodedVinEntity {
        return DecodedVinEntity(
            vin = vin,
            make = make,
            model = model,
            year = year,
            bodyClass = "Sedan",
            vehicleType = "Passenger Car",
            driveType = "FWD",
            doors = "4",
            trim = "EX",
            engineCylinders = "4",
            engineDisplacement = "1.5",
            engineHP = "192",
            fuelType = "Gasoline",
            transmissionStyle = "Automatic",
            manufacturer = "Honda",
            plantCity = "Marysville",
            plantCountry = "USA",
            plantState = "Ohio",
            abs = "Yes",
            tpms = "Yes",
            airBagLocFront = "1st Row",
            airBagLocSide = "1st Row",
            airBagLocCurtain = "All Rows",
            series = "Accord",
            gvwr = "Class 1C",
            errorCode = null,
            errorText = null,
            timestamp = System.currentTimeMillis(),
            isFavorite = isFavorite
        )
    }
}

