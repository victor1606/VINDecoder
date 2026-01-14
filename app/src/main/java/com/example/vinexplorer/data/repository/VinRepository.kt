package com.example.vinexplorer.data.repository

import android.util.Log
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
    companion object {
        private const val TAG = "VinRepository"
    }

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
            Log.d(TAG, "=== DECODE VIN API CALL ===")
            Log.d(TAG, "Requesting VIN: ${vin.uppercase().trim()}")

            // Try to fetch from network
            val response = apiService.decodeVin(vin.uppercase().trim())

            Log.d(TAG, "API Response received:")
            Log.d(TAG, "  Count: ${response.count}")
            Log.d(TAG, "  Message: ${response.message}")
            Log.d(TAG, "  Search Criteria: ${response.searchCriteria}")
            Log.d(TAG, "  Results count: ${response.results.size}")

            // Log ALL results from API response (only non-null values)
            Log.d(TAG, "  === ALL RESPONSE DATA ===")
            response.results.filter { !it.value.isNullOrBlank() }.forEach { result ->
                Log.d(TAG, "  [${result.variableId}] ${result.variable}: ${result.value}")
            }
            Log.d(TAG, "  === END RESPONSE DATA ===")

            val vehicleInfo = VehicleInfo.fromResults(vin.uppercase().trim(), response.results)

            Log.d(TAG, "Parsed VehicleInfo:")
            Log.d(TAG, "  Make: ${vehicleInfo.make}")
            Log.d(TAG, "  Model: ${vehicleInfo.model}")
            Log.d(TAG, "  Year: ${vehicleInfo.year}")
            Log.d(TAG, "  Body: ${vehicleInfo.bodyClass}")
            Log.d(TAG, "  Engine: ${vehicleInfo.engineCylinders} cyl, ${vehicleInfo.engineDisplacement}L")
            Log.d(TAG, "=== END DECODE VIN ===")

            // Cache to local database
            val entity = DecodedVinEntity.fromVehicleInfo(vehicleInfo)
            vinDao.insertVin(entity)

            Result.success(vehicleInfo)
        } catch (e: Exception) {
            Log.e(TAG, "API call failed: ${e.message}", e)
            // Network failed, try to get from cache
            val cached = vinDao.getVinByValue(vin.uppercase().trim())
            if (cached != null) {
                Log.d(TAG, "Returning cached data for VIN: $vin")
                Result.success(cached.toVehicleInfo())
            } else {
                Log.e(TAG, "No cached data available for VIN: $vin")
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
            Log.d(TAG, "=== GET MODELS API CALL ===")
            Log.d(TAG, "Requesting models for Make: $make, Year: $year")

            val response = apiService.getModelsForMakeYear(make, year)

            Log.d(TAG, "API Response received:")
            Log.d(TAG, "  Count: ${response.count}")
            Log.d(TAG, "  Message: ${response.message}")
            Log.d(TAG, "  Search Criteria: ${response.searchCriteria}")
            Log.d(TAG, "  Models found: ${response.results.size}")

            // Log ALL models from API response
            Log.d(TAG, "  === ALL MODELS ===")
            response.results.forEach { model ->
                Log.d(TAG, "  [MakeID: ${model.makeId}] ${model.makeName} - ${model.modelName} (ModelID: ${model.modelId})")
            }
            Log.d(TAG, "  === END ALL MODELS ===")
            Log.d(TAG, "=== END GET MODELS ===")

            Result.success(response.results)
        } catch (e: Exception) {
            Log.e(TAG, "Get models API call failed: ${e.message}", e)
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

