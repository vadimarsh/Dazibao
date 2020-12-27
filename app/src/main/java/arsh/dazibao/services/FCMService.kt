package arsh.dazibao.services


import android.util.Log
import arsh.dazibao.App
import arsh.dazibao.R
import arsh.dazibao.getUserAuth
import com.auth0.android.jwt.JWT
import com.example.arshkotlin9.api.Token
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.IOException


class FCMService : FirebaseMessagingService(), CoroutineScope by MainScope() {
    override fun onMessageReceived(message: RemoteMessage) {
        val recipientId = message.data["recipientId"] ?: ""
        val title = message.data["title"] ?: ""
        val token = getUserAuth(this) ?: ""


        if (token.isNotEmpty() && recipientId.isNotEmpty()) {
            val jwt = JWT(token)

            if (recipientId != jwt.getClaim("id").asString()) {
                FirebaseInstanceId.getInstance().deleteInstanceId()
               // FirebaseInstallations.getInstance().delete()
            } else {
                NotificationHelper.notifyFromFCM(this, title)

            }
        }

    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
      /* launch {
            try {
                Log.d("zzzz","mytoken:"+ getUserAuth(this@FCMService))
                Log.d("zzzz","token from serv:"+token)
                val response = App.repository.firebasePushToken(Token(token))
                Log.d("zzzz","sended token ")
                if (!response.isSuccessful) {
                    Log.d("zzzz","nonsuccesful ")
                    NotificationHelper.notifyFromFCM(
                        this@FCMService,
                        getString(R.string.push_token_failed)
                    )
                }
            } catch (e: IOException) {
                Log.d("zzzz","exception ")
                NotificationHelper.notifyFromFCM(
                    this@FCMService,
                    getString(R.string.push_token_failed)
                )
            }
        }*/

    }
}