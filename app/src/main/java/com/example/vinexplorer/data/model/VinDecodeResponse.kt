package com.example.vinexplorer.data.model

import com.google.gson.annotations.SerializedName

/**
 * Response wrapper from NHTSA DecodeVin API
 * API: https://vpic.nhtsa.dot.gov/api/vehicles/DecodeVin/{vin}?format=json
 */
data class VinDecodeResponse(
    @SerializedName("Count")
    val count: Int,
    @SerializedName("Message")
    val message: String,
    @SerializedName("SearchCriteria")
    val searchCriteria: String?,
    @SerializedName("Results")
    val results: List<VinResult>
)

data class VinResult(
    @SerializedName("Value")
    val value: String?,
    @SerializedName("ValueId")
    val valueId: String?,
    @SerializedName("Variable")
    val variable: String?,
    @SerializedName("VariableId")
    val variableId: Int?
)


