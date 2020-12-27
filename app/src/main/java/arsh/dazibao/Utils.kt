package arsh.dazibao

import android.content.Context
import androidx.core.content.edit
import java.util.*
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

fun cleanUserAuth(context: Context) =
    context.getSharedPreferences(API_SHARED_FILE, Context.MODE_PRIVATE).edit{

        clear()

    }


fun dateToStr(time: Int): String {
    val timel : Long = time.toLong()*1000;
    val dateStr = Date(timel).toString()

    return dateStr
}


fun verboseTime(time: Int): String {
    val secs = System.currentTimeMillis() / 1000 - time + 1
    val mins: Long = 60
    val hours = mins * 60
    val days = hours * 24
    val weeks = days * 7
    val months = weeks * 4
    val years = months * 12

    val result = when (secs) {
        in 0..59 -> "менее минуты назад"
        in mins..hours -> {
            "${secs / mins} минут назад"
        }
        in hours..days -> {
            "${secs / hours} часов назад"
        }
        in days..weeks -> {
            "${secs / (days)} дней назад"
        }
        in weeks..months -> {
            "${secs / (weeks)} недель назад"
        }
        in months..years -> {
            "${secs / (months)} месяцев назад"
        }
        else -> "больше года назад"
    }
    return result
}

/*
fun isFirstTime(context: Context) =
    context.getSharedPreferences(API_SHARED_FILE, Context.MODE_PRIVATE).getBoolean(
        FIRST_TIME_SHARED_KEY, true
    )

fun setNotFirstTime(context: Context) =
    context.getSharedPreferences(API_SHARED_FILE, Context.MODE_PRIVATE)
        .edit()
        .putBoolean(API_SHARED_FILE, false)*/
