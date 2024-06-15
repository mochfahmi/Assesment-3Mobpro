package org.d3if3134.asessment3.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.d3if3134.asessment3.model.banMobil
import org.d3if3134.asessment3.model.OpStatus
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.DELETE

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

private const val BASE_URL = "https://unspoken.my.id/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface BanMobilApiServices {
    @GET("ban.php")
    suspend fun getBanMobil(
        @Header("Authorization") userId: String
    ): List<banMobil>

    @Multipart
    @POST("ban.php")
    suspend fun postBanMobil(
        @Header("Authorization") userId: String,
        @Part("merkBan") merkBan: RequestBody,
        @Part("jenisBan") jenisBan: RequestBody,
        @Part("ukuranBan") ukuranBan: RequestBody,
        @Part image: MultipartBody.Part
    ): OpStatus

    @DELETE("ban.php")
    suspend fun deleteBanMobil(
        @Header("Authorization") userId: String,
        @Query("id") id: Long
    ): OpStatus
}

object BanMobilApi {
    val service: BanMobilApiServices by lazy {
        retrofit.create(BanMobilApiServices::class.java)
    }

    fun getBanMobilUrl(imageId: String): String {
        return "${BASE_URL}image.php?id=$imageId"
    }
enum class ApiStatus { LOADING, SUCCESS, FAILED }

}