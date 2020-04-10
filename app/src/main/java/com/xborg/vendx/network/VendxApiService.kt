package com.xborg.vendx.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.xborg.vendx.database.*
import com.xborg.vendx.database.machine.Machine
import com.xborg.vendx.database.AccessToken
import com.xborg.vendx.database.user.User
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.*
import java.util.concurrent.TimeUnit
import retrofit2.converter.gson.GsonConverterFactory

//private const val BASE_URL = "https://us-central1-vendx-1f40e.cloudfunctions.net/webApi/v1/"
private const val BASE_URL = "https://safe-badlands-08276.herokuapp.com/"

val interceptor : HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
    this.level = HttpLoggingInterceptor.Level.BODY
}

private val okHttpClient = OkHttpClient().newBuilder()
    .apply {
        this.addInterceptor(interceptor)
    }
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

    companion object {
        var accessToken: String = ""
    }

    @FormUrlEncoded
    @POST("oauth/convert-token/")
    fun sendLoginIdToken(
        @Field("token") token: String,
        @Field("backend") backend: String,
        @Field("grant_type") grant_type: String = "convert_token",
        @Field("client_id") client_id: String = "HRmOHx29EHd2Mx1RNgnwAEClAd4J2GcdWnLACvaF",
        @Field("client_secret") client_secret: String = "B8btMbUOM1LJWGStbyfgcg5fMnUPgmBEa1T8ysiAuEVicZOOsDXz6vjqtliSgGOgmRlpw3bgjKLGMmHbir4wekRgFNAGRZfjyoTK8zBCASlNDpmGeBxnBQDPcItbkRrR"
    ): Call<AccessToken>

    @GET("app")
    fun getMinimumApplicationVersionAsync(
    ): Call<AppInfo>

    @GET("item_details")
    fun getItemDetailsAsync(
        @Header("Authorization") token: String? = accessToken
    ): Call<List<ItemDetail>>

    @FormUrlEncoded
    @POST("auth/email/")
    fun sendLoginNameAndEmail(
        @Field("email") email: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("auth/token/")
    fun sendEmailToken(
        @Field("email") email: String,
        @Field("token") token: String
    ): Call<AccessToken>

    @FormUrlEncoded
    @POST("oauth/token/")
    fun refreshAccessToken(
        @Field("refresh_token") refresh_token: String,
        @Field("grant_type") grant_type: String = "refresh_token",
        @Field("client_id") client_id: String = "HRmOHx29EHd2Mx1RNgnwAEClAd4J2GcdWnLACvaF",
        @Field("client_secret") client_secret: String = "B8btMbUOM1LJWGStbyfgcg5fMnUPgmBEa1T8ysiAuEVicZOOsDXz6vjqtliSgGOgmRlpw3bgjKLGMmHbir4wekRgFNAGRZfjyoTK8zBCASlNDpmGeBxnBQDPcItbkRrR"
    ): Call<AccessToken>

    @GET("machines")
    fun getMachinesNearbyAsync(
        @Header("Authorization") token:String? = accessToken
    ): Call<List<Machine>>

    @POST("orders")
    fun createOrder(
        @Header("Authorization") token: String? = accessToken,
        @Body cart: List<CartItem>
        ): Call<ResponseBody>

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

    @GET("users/current/")
    fun getUserInfoAsync(
        @Header("Authorization") token: String? = accessToken
    ): Call<User>

    @GET("machines_nearby/{user_id}")
    fun getMachinesNearbyAsync(
        @Path("user_id") user_id: String,
        @Body user_location: Location
    ): Call<List<Machine>>

    @GET("machines/{machine_id}")
    fun getMachineAsync(
        @Path("machine_id") machine_id: String
    ): Call<Machine>
}

object VendxApi {
    val retrofitServices: VendxAPIService by lazy {
        retrofit.create(VendxAPIService::class.java)
    }
}