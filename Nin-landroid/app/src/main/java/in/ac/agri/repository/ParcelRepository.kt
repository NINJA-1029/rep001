package in.ac.agri.repository

import in.ac.agri.network.ApiService
import in.ac.agri.network.Parcel
import in.ac.agri.network.ParcelDetails

class ParcelRepository(private val apiService: ApiService) {
    suspend fun getParcels(): List<Parcel> = apiService.getParcels()
    suspend fun getParcelDetails(id: String): ParcelDetails = apiService.getParcelDetails(id)
}