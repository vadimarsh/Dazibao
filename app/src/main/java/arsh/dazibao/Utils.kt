package arsh.dazibao

import android.content.Context
import androidx.core.content.edit
import java.util.regex.Pattern

fun isValid(password: String) =
    Pattern.compile("(?!.*[^a-zA-Z0-9])(.{6,})\$").matcher(password).matches()

fun setUserAuth(token: String, context: Context ) =
    context.getSharedPreferences(API_SHARED_FILE, Context.MODE_PRIVATE).edit{
        putString(AUTHENTICATED_SHARED_KEY, token)
    }
fun getUserAuth(context: Context): String? =
    context
        .getSharedPreferences(API_SHARED_FILE, Context.MODE_PRIVATE)
        .getString(AUTHENTICATED_SHARED_KEY, null)

fun isAuthenticated(context: Context) =
    context.getSharedPreferences(API_SHARED_FILE, Context.MODE_PRIVATE).getString(
        AUTHENTICATED_SHARED_KEY, ""
    )?.isNotEmpty() ?: false

fun clearUserAuth(context:Context){
    val sharedPrefs = context.getSharedPreferences(API_SHARED_FILE,Context.MODE_PRIVATE)
    if (sharedPrefs.contains(AUTHENTICATED_SHARED_KEY).not()) {
        return
    }
    sharedPrefs.edit {
        remove(AUTHENTICATED_SHARED_KEY)
    }
}
