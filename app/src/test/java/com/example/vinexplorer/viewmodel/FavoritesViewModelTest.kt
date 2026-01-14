package com.example.vinexplorer.viewmodel

import com.example.vinexplorer.data.model.DecodedVinEntity
import com.example.vinexplorer.data.repository.VinRepository
import com.example.vinexplorer.ui.viewmodel.FavoritesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

/**
 * Unit tests for FavoritesViewModel
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `favoriteVins returns empty list when no favorites exist`() {
        // Given: A repository with no favorites
        val repository = mock(VinRepository::class.java)
        `when`(repository.getFavoriteVins()).thenReturn(flowOf(emptyList()))

        // When: Creating the ViewModel
        val viewModel = FavoritesViewModel(repository)

        // Then: The initial value should be empty
        assertTrue(viewModel.favoriteVins.value.isEmpty())
    }

    @Test
    fun `favoriteVins returns list of favorite VINs`() {
        // Given: A repository with favorite VINs
        val favoriteVin = createTestVinEntity("1HGBH41JXMN109186", isFavorite = true)
        val repository = mock(VinRepository::class.java)
        `when`(repository.getFavoriteVins()).thenReturn(flowOf(listOf(favoriteVin)))

        // When: Creating the ViewModel
        val viewModel = FavoritesViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then: The favorites list should contain the VIN
        assertEquals(1, viewModel.favoriteVins.value.size)
        assertEquals("1HGBH41JXMN109186", viewModel.favoriteVins.value[0].vin)
    }

    private fun createTestVinEntity(
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

