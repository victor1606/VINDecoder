package com.example.vinexplorer.data.model

/**
 * Parsed vehicle information from VIN decode results
 */
data class VehicleInfo(
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
    val errorText: String?
) {
    companion object {
        /**
         * Parse VIN decode API results into VehicleInfo
         */
        fun fromResults(vin: String, results: List<VinResult>): VehicleInfo {
            val map = results.associate { it.variable to it.value }

            return VehicleInfo(
                vin = vin,
                make = map["Make"]?.takeIf { it.isNotBlank() },
                model = map["Model"]?.takeIf { it.isNotBlank() },
                year = map["Model Year"]?.takeIf { it.isNotBlank() },
                bodyClass = map["Body Class"]?.takeIf { it.isNotBlank() },
                vehicleType = map["Vehicle Type"]?.takeIf { it.isNotBlank() },
                driveType = map["Drive Type"]?.takeIf { it.isNotBlank() },
                doors = map["Doors"]?.takeIf { it.isNotBlank() },
                trim = map["Trim"]?.takeIf { it.isNotBlank() },

                engineCylinders = map["Engine Number of Cylinders"]?.takeIf { it.isNotBlank() },
                engineDisplacement = map["Displacement (L)"]?.takeIf { it.isNotBlank() },
                engineHP = map["Engine Brake (hp) From"]?.takeIf { it.isNotBlank() },
                fuelType = map["Fuel Type - Primary"]?.takeIf { it.isNotBlank() },
                transmissionStyle = map["Transmission Style"]?.takeIf { it.isNotBlank() },

                manufacturer = map["Manufacturer Name"]?.takeIf { it.isNotBlank() },
                plantCity = map["Plant City"]?.takeIf { it.isNotBlank() },
                plantCountry = map["Plant Country"]?.takeIf { it.isNotBlank() },
                plantState = map["Plant State"]?.takeIf { it.isNotBlank() },

                abs = map["Anti-lock Braking System (ABS)"]?.takeIf { it.isNotBlank() },
                tpms = map["Tire Pressure Monitoring System (TPMS) Type"]?.takeIf { it.isNotBlank() },
                airBagLocFront = map["Air Bag Loc - Front"]?.takeIf { it.isNotBlank() },
                airBagLocSide = map["Air Bag Loc - Side"]?.takeIf { it.isNotBlank() },
                airBagLocCurtain = map["Air Bag Loc - Curtain"]?.takeIf { it.isNotBlank() },

                series = map["Series"]?.takeIf { it.isNotBlank() },
                gvwr = map["Gross Vehicle Weight Rating From"]?.takeIf { it.isNotBlank() },
                errorCode = map["Error Code"]?.takeIf { it.isNotBlank() && it != "0" },
                errorText = map["Error Text"]?.takeIf { it.isNotBlank() && it != "0 - VIN decoded clean. Check Digit (9th position) is correct" }
            )
        }
    }

    val displayName: String
        get() = listOfNotNull(year, make, model).joinToString(" ").ifBlank { vin }

    val hasError: Boolean
        get() = errorCode != null && errorCode != "0"
}

