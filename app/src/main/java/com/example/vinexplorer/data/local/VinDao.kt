package com.example.vinexplorer.data.local

import androidx.room.*
import com.example.vinexplorer.data.model.DecodedVinEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for VIN database operations
 */
@Dao
interface VinDao {

    @Query("SELECT * FROM decoded_vins ORDER BY timestamp DESC")
    fun getAllVins(): Flow<List<DecodedVinEntity>>

    @Query("SELECT * FROM decoded_vins WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavoriteVins(): Flow<List<DecodedVinEntity>>

    @Query("SELECT * FROM decoded_vins WHERE vin = :vin LIMIT 1")
    suspend fun getVinByValue(vin: String): DecodedVinEntity?

    @Query("SELECT * FROM decoded_vins WHERE vin = :vin LIMIT 1")
    fun getVinByValueFlow(vin: String): Flow<DecodedVinEntity?>

    @Query("""
        SELECT * FROM decoded_vins 
        WHERE vin LIKE '%' || :query || '%' 
        OR make LIKE '%' || :query || '%' 
        OR model LIKE '%' || :query || '%'
        OR year LIKE '%' || :query || '%'
        ORDER BY timestamp DESC
    """)
    fun searchVins(query: String): Flow<List<DecodedVinEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVin(vin: DecodedVinEntity)

    @Update
    suspend fun updateVin(vin: DecodedVinEntity)

    @Delete
    suspend fun deleteVin(vin: DecodedVinEntity)

    @Query("DELETE FROM decoded_vins WHERE vin = :vin")
    suspend fun deleteVinByValue(vin: String)

    @Query("UPDATE decoded_vins SET isFavorite = :isFavorite WHERE vin = :vin")
    suspend fun updateFavorite(vin: String, isFavorite: Boolean)

    @Query("SELECT COUNT(*) FROM decoded_vins")
    suspend fun getVinCount(): Int
}


