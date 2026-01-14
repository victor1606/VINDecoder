package com.example.vinexplorer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.vinexplorer.data.repository.VinRepository

/**
 * Factory for creating ViewModels with repository dependency
 */
class ViewModelFactory(
    private val repository: VinRepository,
    private val vin: String? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(VinListViewModel::class.java) -> {
                VinListViewModel(repository) as T
            }
            modelClass.isAssignableFrom(VinDetailViewModel::class.java) -> {
                VinDetailViewModel(repository, vin ?: "") as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

