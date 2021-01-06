package arsh.dazibao.api

import okhttp3.Interceptor
import okhttp3.Response

class LogOutInterceptor(private val logOut: () -> Unit) : Interceptor {
    private companion object {
        const val UNAUTHORIZED = 401
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response: Response
        response = chain.proceed(request)

        return if (response.code() == UNAUTHORIZED) {
            logOut()
            response
        } else
            response
    }
}
