package `in`.ac.agri.network

import retrofit2.http.*

interface ApiService {
    @GET("parcels")
    suspend fun getParcels(): List<Parcel>

    @GET("parcels/{id}")
    suspend fun getParcelDetails(@Path("id") id: String): ParcelDetails

    @POST("parcels/upload")
    suspend fun uploadParcel(@Body data: ParcelUploadRequest): ParcelResponse
}

data class Parcel(
    val id: String,
    val name: String,
    val centroid: List<Double>
)

data class ParcelDetails(
    val id: String,
    val boundary: String, // GeoJSON
    val orthomosaicUrl: String,
    val ndviUrl: String,
    val healthScore: HealthScore,
    val canopyData: CanopyData,
    val valuation: Valuation
)

data class HealthScore(
    val score: Double,
    val status: String,
    val confidence: Double
)

data class CanopyData(
    val count: Int,
    val densityPerAcre: Double,
    val stressedTrees: Int,
    val confidence: Double
)

data class Valuation(
    val low: Double,
    val mid: Double,
    val high: Double,
    val confidence: Double
)

data class ParcelUploadRequest(
    val name: String,
    val boundary: String,
    val orthoUrl: String,
    val demUrl: String,
    val ndviUrl: String
)

data class ParcelResponse(
    val id: String,
    val status: String
)