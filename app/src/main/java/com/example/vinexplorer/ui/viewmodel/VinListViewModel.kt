package com.example.vinexplorer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vinexplorer.data.model.DecodedVinEntity
import com.example.vinexplorer.data.model.VehicleInfo
import com.example.vinexplorer.data.repository.VinRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for the VIN list/history screen
 */
class VinListViewModel(
    private val repository: VinRepository
) : ViewModel() {

    // Search/input state
    private val _vinInput = MutableStateFlow("")
    val vinInput: StateFlow<String> = _vinInput.asStateFlow()

    // Search query for filtering history
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Decode operation state
    private val _decodeState = MutableStateFlow<UiState<VehicleInfo>>(UiState.Idle)
    val decodeState: StateFlow<UiState<VehicleInfo>> = _decodeState.asStateFlow()

    // VIN history list - reactive from Room
    val vinHistory: StateFlow<List<DecodedVinEntity>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.getAllVins()
            } else {
                repository.searchVins(query)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Show favorites only
    private val _showFavoritesOnly = MutableStateFlow(false)
    val showFavoritesOnly: StateFlow<Boolean> = _showFavoritesOnly.asStateFlow()

    /**
     * Update VIN input field
     */
    fun onVinInputChange(input: String) {
        // VINs are alphanumeric, max 17 characters, no I, O, Q
        val filtered = input.uppercase()
            .filter { it.isLetterOrDigit() && it !in "IOQ" }
            .take(17)
        _vinInput.value = filtered
    }

    /**
     * Update search query for filtering history
     */
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    /**
     * Toggle favorites filter
     */
    fun toggleFavoritesFilter() {
        _showFavoritesOnly.value = !_showFavoritesOnly.value
    }

    /**
     * Decode a VIN
     */
    fun decodeVin() {
        val vin = _vinInput.value.trim()

        if (vin.length != 17) {
            _decodeState.value = UiState.Error("VIN must be exactly 17 characters")
            return
        }

        viewModelScope.launch {
            _decodeState.value = UiState.Loading

            repository.decodeVin(vin).fold(
                onSuccess = { vehicleInfo ->
                    _decodeState.value = UiState.Success(vehicleInfo)
                    _vinInput.value = "" // Clear input after successful decode
                },
                onFailure = { error ->
                    _decodeState.value = UiState.Error(
                        error.message ?: "Failed to decode VIN"
                    )
                }
            )
        }
    }

    /**
     * Delete a VIN from history
     */
    fun deleteVin(entity: DecodedVinEntity) {
        viewModelScope.launch {
            repository.deleteVin(entity)
        }
    }

    /**
     * Toggle favorite status
     */
    fun toggleFavorite(vin: String, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(vin, isFavorite)
        }
    }

    /**
     * Reset decode state to idle
     */
    fun resetDecodeState() {
        _decodeState.value = UiState.Idle
    }

    /**
     * Validate VIN format
     */
    fun isValidVin(vin: String): Boolean {
        return vin.length == 17 && vin.all { it.isLetterOrDigit() && it !in "IOQ" }
    }
}

