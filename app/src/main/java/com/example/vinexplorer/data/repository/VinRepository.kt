package com.example.vinexplorer.data.repository

import com.example.vinexplorer.data.local.VinDao
import com.example.vinexplorer.data.model.DecodedVinEntity
import com.example.vinexplorer.data.model.ModelResult
import com.example.vinexplorer.data.model.VehicleInfo
import com.example.vinexplorer.data.remote.NhtsaApiService
import kotlinx.coroutines.flow.Flow

/**
 * Repository implementing offline-first strategy for VIN data
 */
class VinRepository(
    private val apiService: NhtsaApiService,
    private val vinDao: VinDao
) {

    /**
     * Get all decoded VINs from local database as a reactive Flow
     */
    fun getAllVins(): Flow<List<DecodedVinEntity>> = vinDao.getAllVins()

    /**
     * Get favorite VINs
     */
    fun getFavoriteVins(): Flow<List<DecodedVinEntity>> = vinDao.getFavoriteVins()

    /**
     * Search VINs by query
     */
    fun searchVins(query: String): Flow<List<DecodedVinEntity>> = vinDao.searchVins(query)

    /**
     * Get a specific VIN from local database
     */
    fun getVinByValueFlow(vin: String): Flow<DecodedVinEntity?> = vinDao.getVinByValueFlow(vin)

    /**
     * Decode a VIN: fetch from API, cache to Room, return result
     * Implements offline-first: if network fails, try to return cached data
     */
    suspend fun decodeVin(vin: String): Result<VehicleInfo> {
        return try {
            // Try to fetch from network
            val response = apiService.decodeVin(vin.uppercase().trim())
            val vehicleInfo = VehicleInfo.fromResults(vin.uppercase().trim(), response.results)

            // Cache to local database
            val entity = DecodedVinEntity.fromVehicleInfo(vehicleInfo)
            vinDao.insertVin(entity)

            Result.success(vehicleInfo)
        } catch (e: Exception) {
            // Network failed, try to get from cache
            val cached = vinDao.getVinByValue(vin.uppercase().trim())
            if (cached != null) {
                Result.success(cached.toVehicleInfo())
            } else {
                Result.failure(e)
            }
        }
    }

    /**
     * Get cached VIN without network call
     */
    suspend fun getCachedVin(vin: String): DecodedVinEntity? {
        return vinDao.getVinByValue(vin.uppercase().trim())
    }

    /**
     * Get models for a specific make and year
     */
    suspend fun getModelsForMakeYear(make: String, year: String): Result<List<ModelResult>> {
        return try {
            val response = apiService.getModelsForMakeYear(make, year)
            Result.success(response.results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Delete a VIN from local database
     */
    suspend fun deleteVin(entity: DecodedVinEntity) {
        vinDao.deleteVin(entity)
    }

    /**
     * Delete a VIN by its value
     */
    suspend fun deleteVinByValue(vin: String) {
        vinDao.deleteVinByValue(vin)
    }

    /**
     * Toggle favorite status for a VIN
     */
    suspend fun toggleFavorite(vin: String, isFavorite: Boolean) {
        vinDao.updateFavorite(vin, isFavorite)
    }

    /**
     * Get the count of stored VINs
     */
    suspend fun getVinCount(): Int = vinDao.getVinCount()
}

