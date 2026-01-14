package com.example.vinexplorer.ui.navigation

/**
 * Sealed class representing navigation destinations
 */
sealed class Screen(val route: String) {
    object VinList : Screen("vin_list")
    object VinDetail : Screen("vin_detail/{vin}") {
        fun createRoute(vin: String) = "vin_detail/$vin"
    }
}


