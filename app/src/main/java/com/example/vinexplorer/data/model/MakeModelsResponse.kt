package com.example.vinexplorer.data.model

import com.google.gson.annotations.SerializedName

/**
 * Response from NHTSA GetModelsForMakeYear API
 * API: https://vpic.nhtsa.dot.gov/api/vehicles/GetModelsForMakeYear/make/{make}/modelyear/{year}?format=json
 */
data class MakeModelsResponse(
    @SerializedName("Count")
    val count: Int,
    @SerializedName("Message")
    val message: String,
    @SerializedName("SearchCriteria")
    val searchCriteria: String?,
    @SerializedName("Results")
    val results: List<ModelResult>
)

data class ModelResult(
    @SerializedName("Make_ID")
    val makeId: Int?,
    @SerializedName("Make_Name")
    val makeName: String?,
    @SerializedName("Model_ID")
    val modelId: Int?,
    @SerializedName("Model_Name")
    val modelName: String?
)

