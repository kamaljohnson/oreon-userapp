package com.xborg.vendx.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

private const val BASE_URL = "http://us-central1-vendx-1f40e.cloudfunctions.net/webApi/v1/"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

interface VendxAPIService {
    @GET("machine/{id}/items")
    fun getMachineItemsAsync(@Path("id") id: String):
            Deferred<String>

    @GET("user/{id}/shelf")
    fun getShelfItemsAsync(@Path("id") id: String):
            Deferred<String>


}

object VendxApi {
    val retrofitServices: VendxAPIService by lazy {
        retrofit.create(VendxAPIService::class.java)
    }
}