package in.ac.agri

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.Modifier
import androidx.compose.runtime.*
import com.google.firebase.auth.FirebaseAuth
import in.ac.agri.network.ApiClient
import in.ac.agri.repository.ParcelRepository
import in.ac.agri.ui.auth.AuthScreen
import in.ac.agri.ui.map.MapScreen
import in.ac.agri.ui.map.MapViewModel
import in.ac.agri.ui.theme.AgriTheme

class MainActivity : ComponentActivity() {
    private val apiService = ApiClient().api
    private val repository = ParcelRepository(apiService)
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AgriTheme {
                var isAuthenticated by remember { 
                    mutableStateOf(FirebaseAuth.getInstance().currentUser != null) 
                }

                if (!isAuthenticated) {
                    AuthScreen(onAuthSuccess = {
                        isAuthenticated = true
                    })
                } else {
                    val viewModel = remember { MapViewModel(repository) }
                    LaunchedEffect(Unit) {
                        viewModel.loadParcels()
                    }
                    MapScreen(viewModel = viewModel)
                }
            }
        }
    }
}
