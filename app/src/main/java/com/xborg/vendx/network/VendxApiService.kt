package com.xborg.vendx.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.xborg.vendx.database.*
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.*
import java.util.concurrent.TimeUnit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://us-central1-vendx-1f40e.cloudfunctions.net/webApi/v1/"

private val okHttpClient = OkHttpClient().newBuilder()
    .connectTimeout(10, TimeUnit.SECONDS)
    .readTimeout(10, TimeUnit.SECONDS)
    .writeTimeout(10, TimeUnit.SECONDS)
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .client(okHttpClient)
    .build()

interface VendxAPIService {

    @GET("app")
    fun getMinimumApplicationVersionAsync(
    ): Call<Application>

    @GET("machine/{id}/items")
    fun getMachineItemsAsync(
        @Path("id") id: String
    ): Call<List<Item>>

    @GET("user/{id}/inventory")
    fun getInventoryItemsAsync(
        @Path("id") id: String
    ): Call<List<Item>>

    @GET("user/{id}/transactions")
    fun getTransactionsAsync(
        @Path("id") id: String
    ): Call<List<Transaction>>

    @FormUrlEncoded
    @POST("machines/nearby/{id}")
    fun requestNearbyMachinesAsync(
        @Path("id") uid: String,
        @Field("location") location: String
    ): Call<List<Machine>>

    @FormUrlEncoded
    @POST("orders/create")
    fun createOrderAsync(
        @Field("order") order: String
    ): Call<Payment>

    @FormUrlEncoded
    @POST("payments/check/payment_data")
    fun sendPaymentDataAsync(
        @Field("paymentData") paymentData: String
    ): Call<Payment>

    @FormUrlEncoded
    @POST("vends/create")
    fun sendEncryptedOTPAsync(
        @Field("bag") bag: String
    ): Call<Vend>

    @FormUrlEncoded
    @POST("vends/complete/{id}")
    fun sendOnVendCompleteLogAsync(
        @Path("id") id: String,
        @Field("bag") bag: String
    ): Call<Vend>

    @FormUrlEncoded
    @POST("vends/cancel/{id}")
    fun sendOnVendCancelAsync(
        @Path("id") id: String,
        @Field("bag") bag: String
    ): Deferred<String>

    @FormUrlEncoded
    @POST("feedback/add/{id}")
    fun postFeedbackAsync(
        @Path("id") id: String,
        @Field("feedback") feedback: String
    ): Call<Feedback>
}

object VendxApi {
    val retrofitServices: VendxAPIService by lazy {
        retrofit.create(VendxAPIService::class.java)
    }
}