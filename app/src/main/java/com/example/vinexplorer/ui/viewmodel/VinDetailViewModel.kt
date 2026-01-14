package com.example.vinexplorer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vinexplorer.data.model.DecodedVinEntity
import com.example.vinexplorer.data.model.ModelResult
import com.example.vinexplorer.data.repository.VinRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for the VIN detail screen
 */
class VinDetailViewModel(
    private val repository: VinRepository,
    private val vin: String
) : ViewModel() {

    // VIN details from local database (reactive)
    val vinDetails: StateFlow<DecodedVinEntity?> = repository.getVinByValueFlow(vin)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Models for the same manufacturer/year
    private val _modelsState = MutableStateFlow<UiState<List<ModelResult>>>(UiState.Idle)
    val modelsState: StateFlow<UiState<List<ModelResult>>> = _modelsState.asStateFlow()

    // Expanded sections state
    private val _expandedSections = MutableStateFlow(
        setOf("basic") // Basic info expanded by default
    )
    val expandedSections: StateFlow<Set<String>> = _expandedSections.asStateFlow()

    init {
        // Load models when VIN details are available
        viewModelScope.launch {
            vinDetails.collect { entity ->
                entity?.let {
                    if (it.make != null && it.year != null && _modelsState.value is UiState.Idle) {
                        loadModelsForMakeYear(it.make, it.year)
                    }
                }
            }
        }
    }

    /**
     * Load models for the same manufacturer and year
     */
    fun loadModelsForMakeYear(make: String, year: String) {
        viewModelScope.launch {
            _modelsState.value = UiState.Loading

            repository.getModelsForMakeYear(make, year).fold(
                onSuccess = { models ->
                    _modelsState.value = UiState.Success(models)
                },
                onFailure = { error ->
                    _modelsState.value = UiState.Error(
                        error.message ?: "Failed to load models"
                    )
                }
            )
        }
    }

    /**
     * Retry loading models
     */
    fun retryLoadModels() {
        vinDetails.value?.let { entity ->
            if (entity.make != null && entity.year != null) {
                loadModelsForMakeYear(entity.make, entity.year)
            }
        }
    }

    /**
     * Toggle a section's expanded state
     */
    fun toggleSection(sectionId: String) {
        _expandedSections.value = if (sectionId in _expandedSections.value) {
            _expandedSections.value - sectionId
        } else {
            _expandedSections.value + sectionId
        }
    }

    /**
     * Toggle favorite status
     */
    fun toggleFavorite() {
        viewModelScope.launch {
            vinDetails.value?.let { entity ->
                repository.toggleFavorite(entity.vin, !entity.isFavorite)
            }
        }
    }

    /**
     * Refresh VIN data from network
     */
    fun refreshVin() {
        viewModelScope.launch {
            repository.decodeVin(vin)
            // Also refresh models
            vinDetails.value?.let { entity ->
                if (entity.make != null && entity.year != null) {
                    loadModelsForMakeYear(entity.make, entity.year)
                }
            }
        }
    }
}

