package com.xborg.vendx.network

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "https://us-central1-vendx-1f40e.cloudfunctions.net/"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface VendxAPIService {
    @GET("items")
    fun getItems():
            Call<String>
}

object VendxApi {
    val retrofitServices: VendxAPIService by lazy {
        retrofit.create(VendxAPIService::class.java)
    }
}