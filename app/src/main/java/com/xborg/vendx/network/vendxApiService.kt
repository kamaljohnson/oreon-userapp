package com.xborg.vendx.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Json
import com.xborg.vendx.database.Item
import com.xborg.vendx.database.Order
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

private const val BASE_URL = "http://us-central1-vendx-1f40e.cloudfunctions.net/webApi/v1/"

private val okHttpClient = OkHttpClient().newBuilder()
    .connectTimeout(40, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .client(okHttpClient)
    .build()

interface VendxAPIService {
    @GET("machine/{id}/items")
    fun getMachineItemsAsync(
        @Path("id") id: String
    ): Deferred<String>

    @GET("user/{id}/shelf")
    fun getShelfItemsAsync(
        @Path("id") id: String
    ): Deferred<String>

    @GET("user/{id}/transactions")
    fun getTransactionsAsync(
        @Path("id") id: String
    ): Deferred<String>

    @FormUrlEncoded
    @POST("machines/nearby/{id}")
    fun requestNearbyMachines(
        @Path("id") uid: String,
        @Field("location") location: String
    ): Deferred<String>

    @FormUrlEncoded
    @POST("orders/create")
    fun createOrderAsync(
        @Field("order") order: String
    ): Deferred<String>

    @FormUrlEncoded
    @POST("payments/check/payment_data")
    fun sendPaymentDataAsync(
        @Field("paymentData") paymentData: String
    ): Deferred<String>

    @FormUrlEncoded
    @POST("vends/check/otp")
    fun sendEncryptedOTPAsync(
        @Field("bag") bag: String
    ): Deferred<String>

    @FormUrlEncoded
    @POST("vends/complete/{id}")
    fun sendOnVendCompleteLogAsync(
        @Path("id") id: String,
        @Field("bag") bag: String
    ): Deferred<String>
}

object VendxApi {
    val retrofitServices: VendxAPIService by lazy {
        retrofit.create(VendxAPIService::class.java)
    }
}