package com.example.vinexplorer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vinexplorer.data.model.DecodedVinEntity
import com.example.vinexplorer.data.repository.VinRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for the Favorites screen
 */
class FavoritesViewModel(
    private val repository: VinRepository
) : ViewModel() {

    /**
     * Favorite VINs list - reactive from Room
     */
    val favoriteVins: StateFlow<List<DecodedVinEntity>> = repository.getFavoriteVins()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * Remove a VIN from favorites
     */
    fun removeFromFavorites(vin: String) {
        viewModelScope.launch {
            repository.toggleFavorite(vin, false)
        }
    }

    /**
     * Delete a VIN from history entirely
     */
    fun deleteVin(entity: DecodedVinEntity) {
        viewModelScope.launch {
            repository.deleteVin(entity)
        }
    }
}

