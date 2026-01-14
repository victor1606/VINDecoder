package com.example.vinexplorer.viewmodel

import com.example.vinexplorer.ui.viewmodel.VinListViewModel
import com.example.vinexplorer.data.repository.VinRepository
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
 * Unit tests for VinListViewModel
 */
@OptIn(ExperimentalCoroutinesApi::class)
class VinListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: VinRepository
    private lateinit var viewModel: VinListViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock(VinRepository::class.java)
        `when`(repository.getAllVins()).thenReturn(flowOf(emptyList()))
        `when`(repository.searchVins(anyString())).thenReturn(flowOf(emptyList()))
        viewModel = VinListViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial VIN input is empty`() {
        assertEquals("", viewModel.vinInput.value)
    }

    @Test
    fun `onVinInputChange updates VIN input`() {
        // When: Entering a VIN
        viewModel.onVinInputChange("1HGBH41JXMN109186")

        // Then: The VIN input should be updated
        assertEquals("1HGBH41JXMN109186", viewModel.vinInput.value)
    }

    @Test
    fun `onVinInputChange converts to uppercase`() {
        // When: Entering lowercase VIN
        viewModel.onVinInputChange("1hgbh41jxmn109186")

        // Then: The VIN should be uppercase
        assertEquals("1HGBH41JXMN109186", viewModel.vinInput.value)
    }

    @Test
    fun `onVinInputChange limits to 17 characters`() {
        // When: Entering a VIN longer than 17 characters
        viewModel.onVinInputChange("1HGBH41JXMN109186EXTRA")

        // Then: Only 17 characters should be kept
        assertEquals("1HGBH41JXMN109186", viewModel.vinInput.value)
        assertEquals(17, viewModel.vinInput.value.length)
    }

    @Test
    fun `onVinInputChange filters out I, O, Q characters`() {
        // When: Entering VIN with invalid characters I, O, Q
        viewModel.onVinInputChange("1IOQBH41JXMN1091")

        // Then: I, O, Q should be removed
        assertFalse(viewModel.vinInput.value.contains("I"))
        assertFalse(viewModel.vinInput.value.contains("O"))
        assertFalse(viewModel.vinInput.value.contains("Q"))
    }

    @Test
    fun `isValidVin returns true for valid 17-character VIN`() {
        assertTrue(viewModel.isValidVin("1HGBH41JXMN109186"))
    }

    @Test
    fun `isValidVin returns false for VIN shorter than 17 characters`() {
        assertFalse(viewModel.isValidVin("1HGBH41JXM"))
    }

    @Test
    fun `isValidVin returns false for VIN with invalid characters`() {
        assertFalse(viewModel.isValidVin("1HGBH41IXMN10918")) // Contains I
        assertFalse(viewModel.isValidVin("1HGBH41OXMN10918")) // Contains O
        assertFalse(viewModel.isValidVin("1HGBH41QXMN10918")) // Contains Q
    }

    @Test
    fun `onSearchQueryChange updates search query`() {
        // When: Entering a search query
        viewModel.onSearchQueryChange("Honda")

        // Then: The search query should be updated
        assertEquals("Honda", viewModel.searchQuery.value)
    }
}

