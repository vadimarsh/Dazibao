package arsh.dazibao

import android.app.Application
import android.content.Context
import com.example.arshkotlin9.api.API
import com.example.arshkotlin9.api.InjectAuthTokenInterceptor

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class App : Application() {
    companion object {
        lateinit var repository: Repository
        lateinit var authTokenInterceptor: InjectAuthTokenInterceptor
    }

    override fun onCreate() {
        super.onCreate()
        authTokenInterceptor = InjectAuthTokenInterceptor {
            getSharedPreferences(
                API_SHARED_FILE,
                Context.MODE_PRIVATE
            ).getString(AUTHENTICATED_SHARED_KEY, null)
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(authTokenInterceptor)
            .build()
        val retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl(SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        //создаем API на основе нового retrofit-клиента
        val api = retrofit.create(API::class.java)

        repository = Repository(api)

        // NotificationHelper.createNotificationChannel(this)

    }
}