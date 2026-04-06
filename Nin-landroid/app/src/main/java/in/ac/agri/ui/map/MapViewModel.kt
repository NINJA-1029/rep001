package `in`.ac.agri.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.ac.agri.network.Parcel
import `in`.ac.agri.network.ParcelDetails
import `in`.ac.agri.repository.MockData
import `in`.ac.agri.repository.ParcelRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class MapUiState {
    object Loading : MapUiState()
    data class Success(val parcels: List<Parcel>) : MapUiState()
    data class Error(val message: String) : MapUiState()
}

sealed class ParcelDetailsState {
    object Idle : ParcelDetailsState()
    object Loading : ParcelDetailsState()
    data class Success(val details: ParcelDetails) : ParcelDetailsState()
    data class Error(val message: String) : ParcelDetailsState()
}

class MapViewModel(private val repository: ParcelRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<MapUiState>(MapUiState.Loading)
    val uiState: StateFlow<MapUiState> = _uiState

    private val _parcelDetails = MutableStateFlow<ParcelDetailsState>(ParcelDetailsState.Idle)
    val parcelDetails: StateFlow<ParcelDetailsState> = _parcelDetails

    // --- PREVIEW MODE FLAG ---
    // Set to true to see the UI on your phone without a backend.
    // Set to false to return to production mode.
    private val isPreviewMode = true 

    fun loadParcels() {
        viewModelScope.launch {
            _uiState.value = MapUiState.Loading
            if (isPreviewMode) {
                delay(1000) // Simulating network
                _uiState.value = MapUiState.Success(listOf(MockData.sampleParcel))
                // Automatically select the first parcel in preview mode
                selectParcel(MockData.sampleParcel.id)
            } else {
                try {
                    val parcels = repository.getParcels()
                    _uiState.value = MapUiState.Success(parcels)
                } catch (e: Exception) {
                    _uiState.value = MapUiState.Error(e.message ?: "Unknown error")
                }
            }
        }
    }

    fun selectParcel(parcelId: String) {
        viewModelScope.launch {
            _parcelDetails.value = ParcelDetailsState.Loading
            if (isPreviewMode) {
                delay(500)
                _parcelDetails.value = ParcelDetailsState.Success(MockData.sampleDetails)
            } else {
                try {
                    val details = repository.getParcelDetails(parcelId)
                    _parcelDetails.value = ParcelDetailsState.Success(details)
                } catch (e: Exception) {
                    _parcelDetails.value = ParcelDetailsState.Error(e.message ?: "Unknown error")
                }
            }
        }
    }
}