package com.example.vinexplorer.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for storing decoded VIN information locally
 */
@Entity(tableName = "decoded_vins")
data class DecodedVinEntity(
    @PrimaryKey
    val vin: String,
    val make: String?,
    val model: String?,
    val year: String?,
    val bodyClass: String?,
    val vehicleType: String?,
    val driveType: String?,
    val doors: String?,
    val trim: String?,

    // Engine info
    val engineCylinders: String?,
    val engineDisplacement: String?,
    val engineHP: String?,
    val fuelType: String?,
    val transmissionStyle: String?,

    // Manufacturing info
    val manufacturer: String?,
    val plantCity: String?,
    val plantCountry: String?,
    val plantState: String?,

    // Safety info
    val abs: String?,
    val tpms: String?,
    val airBagLocFront: String?,
    val airBagLocSide: String?,
    val airBagLocCurtain: String?,

    // Additional info
    val series: String?,
    val gvwr: String?,
    val errorCode: String?,
    val errorText: String?,

    // Metadata
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
) {
    fun toVehicleInfo(): VehicleInfo = VehicleInfo(
        vin = vin,
        make = make,
        model = model,
        year = year,
        bodyClass = bodyClass,
        vehicleType = vehicleType,
        driveType = driveType,
        doors = doors,
        trim = trim,
        engineCylinders = engineCylinders,
        engineDisplacement = engineDisplacement,
        engineHP = engineHP,
        fuelType = fuelType,
        transmissionStyle = transmissionStyle,
        manufacturer = manufacturer,
        plantCity = plantCity,
        plantCountry = plantCountry,
        plantState = plantState,
        abs = abs,
        tpms = tpms,
        airBagLocFront = airBagLocFront,
        airBagLocSide = airBagLocSide,
        airBagLocCurtain = airBagLocCurtain,
        series = series,
        gvwr = gvwr,
        errorCode = errorCode,
        errorText = errorText
    )

    val displayName: String
        get() = listOfNotNull(year, make, model).joinToString(" ").ifBlank { vin }

    companion object {
        fun fromVehicleInfo(info: VehicleInfo, isFavorite: Boolean = false): DecodedVinEntity =
            DecodedVinEntity(
                vin = info.vin,
                make = info.make,
                model = info.model,
                year = info.year,
                bodyClass = info.bodyClass,
                vehicleType = info.vehicleType,
                driveType = info.driveType,
                doors = info.doors,
                trim = info.trim,
                engineCylinders = info.engineCylinders,
                engineDisplacement = info.engineDisplacement,
                engineHP = info.engineHP,
                fuelType = info.fuelType,
                transmissionStyle = info.transmissionStyle,
                manufacturer = info.manufacturer,
                plantCity = info.plantCity,
                plantCountry = info.plantCountry,
                plantState = info.plantState,
                abs = info.abs,
                tpms = info.tpms,
                airBagLocFront = info.airBagLocFront,
                airBagLocSide = info.airBagLocSide,
                airBagLocCurtain = info.airBagLocCurtain,
                series = info.series,
                gvwr = info.gvwr,
                errorCode = info.errorCode,
                errorText = info.errorText,
                isFavorite = isFavorite
            )
    }
}

