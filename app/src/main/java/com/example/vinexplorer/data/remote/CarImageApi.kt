package com.example.vinexplorer.data.remote

/**
 * Utility object for generating car image URLs
 * Uses multiple fallback sources for car images
 */
object CarImageApi {

    private const val TAG = "CarImageApi"
    
    /**
     * Get the manufacturer logo URL as a fallback
     */
    fun getManufacturerLogoUrl(make: String?): String? {
        if (make.isNullOrBlank()) return null

        val cleanMake = make.trim().lowercase().replace(" ", "-")
        return "https://www.carlogos.org/car-logos/$cleanMake-logo.png"
    }
}
