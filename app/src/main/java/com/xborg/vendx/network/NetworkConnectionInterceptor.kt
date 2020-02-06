package com.xborg.vendx.network

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

abstract class NetworkConnectionInterceptor : Interceptor {
    abstract val isInternetAvailable: Boolean
    abstract fun onInternetUnavailable()
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        if (!isInternetAvailable) {
            onInternetUnavailable()
        }
        return chain.proceed(request)
    }
}