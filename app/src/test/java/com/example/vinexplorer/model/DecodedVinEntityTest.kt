package com.example.vinexplorer.model

import com.example.vinexplorer.data.model.DecodedVinEntity
import com.example.vinexplorer.data.model.VehicleInfo
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for DecodedVinEntity
 */
class DecodedVinEntityTest {

    @Test
    fun `displayName shows year make model when available`() {
        val entity = createTestEntity(
            vin = "1HGBH41JXMN109186",
            year = "2021",
            make = "Honda",
            model = "Accord"
        )

        assertEquals("2021 Honda Accord", entity.displayName)
    }

    @Test
    fun `displayName shows VIN when year make model are null`() {
        val entity = createTestEntity(
            vin = "1HGBH41JXMN109186",
            year = null,
            make = null,
            model = null
        )

        assertEquals("1HGBH41JXMN109186", entity.displayName)
    }

    @Test
    fun `displayName shows partial info when some fields are null`() {
        val entity = createTestEntity(
            vin = "1HGBH41JXMN109186",
            year = "2021",
            make = "Honda",
            model = null
        )

        assertEquals("2021 Honda", entity.displayName)
    }

    @Test
    fun `toVehicleInfo converts entity correctly`() {
        val entity = createTestEntity(
            vin = "1HGBH41JXMN109186",
            year = "2021",
            make = "Honda",
            model = "Accord"
        )

        val vehicleInfo = entity.toVehicleInfo()

        assertEquals("1HGBH41JXMN109186", vehicleInfo.vin)
        assertEquals("2021", vehicleInfo.year)
        assertEquals("Honda", vehicleInfo.make)
        assertEquals("Accord", vehicleInfo.model)
    }

    @Test
    fun `fromVehicleInfo creates entity correctly`() {
        val vehicleInfo = VehicleInfo(
            vin = "1HGBH41JXMN109186",
            make = "Honda",
            model = "Accord",
            year = "2021",
            bodyClass = "Sedan",
            vehicleType = "Passenger Car",
            driveType = "FWD",
            doors = "4",
            trim = "EX",
            engineCylinders = "4",
            engineDisplacement = "1.5",
            engineHP = "192",
            fuelType = "Gasoline",
            transmissionStyle = "Automatic",
            manufacturer = "Honda",
            plantCity = "Marysville",
            plantCountry = "USA",
            plantState = "Ohio",
            abs = "Yes",
            tpms = "Yes",
            airBagLocFront = "1st Row",
            airBagLocSide = "1st Row",
            airBagLocCurtain = "All Rows",
            series = "Accord",
            gvwr = "Class 1C",
            errorCode = null,
            errorText = null
        )

        val entity = DecodedVinEntity.fromVehicleInfo(vehicleInfo, isFavorite = true)

        assertEquals("1HGBH41JXMN109186", entity.vin)
        assertEquals("Honda", entity.make)
        assertEquals("Accord", entity.model)
        assertEquals("2021", entity.year)
        assertTrue(entity.isFavorite)
    }

    @Test
    fun `isFavorite defaults to false`() {
        val entity = createTestEntity(vin = "1HGBH41JXMN109186")
        assertFalse(entity.isFavorite)
    }

    private fun createTestEntity(
        vin: String,
        make: String? = "Honda",
        model: String? = "Accord",
        year: String? = "2021",
        isFavorite: Boolean = false
    ): DecodedVinEntity {
        return DecodedVinEntity(
            vin = vin,
            make = make,
            model = model,
            year = year,
            bodyClass = null,
            vehicleType = null,
            driveType = null,
            doors = null,
            trim = null,
            engineCylinders = null,
            engineDisplacement = null,
            engineHP = null,
            fuelType = null,
            transmissionStyle = null,
            manufacturer = null,
            plantCity = null,
            plantCountry = null,
            plantState = null,
            abs = null,
            tpms = null,
            airBagLocFront = null,
            airBagLocSide = null,
            airBagLocCurtain = null,
            series = null,
            gvwr = null,
            errorCode = null,
            errorText = null,
            timestamp = System.currentTimeMillis(),
            isFavorite = isFavorite
        )
    }
}

