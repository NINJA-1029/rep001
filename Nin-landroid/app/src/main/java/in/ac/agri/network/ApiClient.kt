package in.ac.agri.network

// import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient {
    private val okHttp = OkHttpClient.Builder()
        .addInterceptor { chain ->
            /*
            val user = FirebaseAuth.getInstance().currentUser
            val token = if (user != null) {
                try {
                    val task = user.getIdToken(false)
                    com.google.android.gms.tasks.Tasks.await(task).token
                } catch (e: Exception) {
                    null
                }
            } else {
                null
            }

            val req = chain.request().newBuilder()
            if (token != null) {
                req.addHeader("Authorization", "Bearer $token")
            }
            chain.proceed(req.build())
            */
            chain.proceed(chain.request())
        }.build()

    val api: ApiService = Retrofit.Builder()
        .baseUrl("https://api.landroid.app/") // Placeholder for actual backend URL
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttp)
        .build()
        .create(ApiService::class.java)
}