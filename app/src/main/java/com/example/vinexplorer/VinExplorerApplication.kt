package com.example.vinexplorer

import android.app.Application
import com.example.vinexplorer.data.local.VinDatabase
import com.example.vinexplorer.data.remote.RetrofitClient
import com.example.vinexplorer.data.repository.VinRepository

/**
 * Application class for initializing singletons (manual DI)
 */
class VinExplorerApplication : Application() {

    // Lazy initialization of database
    val database by lazy { VinDatabase.getDatabase(this) }

    // Lazy initialization of repository
    val repository by lazy {
        VinRepository(
            apiService = RetrofitClient.apiService,
            vinDao = database.vinDao()
        )
    }

    companion object {
        private lateinit var instance: VinExplorerApplication

        fun getInstance(): VinExplorerApplication = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}

