package com.example.vinexplorer.data.remote

import com.example.vinexplorer.data.model.MakeModelsResponse
import com.example.vinexplorer.data.model.VinDecodeResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit interface for NHTSA vPIC API
 * Base URL: https://vpic.nhtsa.dot.gov/api/
 */
interface NhtsaApiService {

    /**
     * Decode a VIN to get vehicle information
     * @param vin The 17-character Vehicle Identification Number
     * @param format Response format (json)
     */
    @GET("vehicles/DecodeVin/{vin}")
    suspend fun decodeVin(
        @Path("vin") vin: String,
        @Query("format") format: String = "json"
    ): VinDecodeResponse

    /**
     * Get all models for a specific make and year
     * @param make The vehicle manufacturer/make name
     * @param year The model year
     * @param format Response format (json)
     */
    @GET("vehicles/GetModelsForMakeYear/make/{make}/modelyear/{year}")
    suspend fun getModelsForMakeYear(
        @Path("make") make: String,
        @Path("year") year: String,
        @Query("format") format: String = "json"
    ): MakeModelsResponse

    /**
     * Decode VIN with extended info (includes additional details)
     */
    @GET("vehicles/DecodeVinExtended/{vin}")
    suspend fun decodeVinExtended(
        @Path("vin") vin: String,
        @Query("format") format: String = "json"
    ): VinDecodeResponse
}


