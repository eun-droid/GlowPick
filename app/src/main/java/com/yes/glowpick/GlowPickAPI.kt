package com.yes.glowpick

import com.yes.glowpick.model.product.ProductResponse
import com.yes.glowpick.model.recommend.RecommendProduct
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

interface GlowPickAPI {
    @GET("product.{pageNumber}.json")
    suspend fun getProducts(
        @Path("pageNumber") pageNumber: Int
    ): ProductResponse

    @GET("recommend_product.json")
    suspend fun getRecommends() : Map<String, ArrayList<RecommendProduct>>

    companion object {
        private const val BASE_URL = "https://s3.ap-northeast-2.amazonaws.com/public.glowday.com/test/app/"

        fun create(): GlowPickAPI {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(httpLoggingInterceptor)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GlowPickAPI::class.java)
        }
    }
}