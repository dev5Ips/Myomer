package com.myomer.myomer.network

import android.content.Context
import com.myomer.myomer.data.remote.model.response.PostResponseItem
import com.myomer.myomer.network.interceptor.HeaderInterceptor
import com.myomer.myomer.network.interceptor.NetworkInterceptor
import com.myomer.myomer.util.Constant.baseURL
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import java.io.File
import java.util.concurrent.TimeUnit


/**
 * Created by Jeetesh Surana.
 */

interface ApiRestService {

    @GET("posts")
    suspend fun getWeather(): Response<List<PostResponseItem>>


    companion object {
        operator fun invoke(
            context: Context,
            networkInterceptor: NetworkInterceptor,
            headerInterceptor: HeaderInterceptor
        ): ApiRestService {

            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY

            val cacheSize = 20 * 1024 * 1024L // 20 MB

            fun provideCache(): Cache? {
                var cache: Cache? = null
                try {
                    cache = Cache(File(context.cacheDir, "Cache_directory"), cacheSize)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return cache
            }

            val okHttpBuilder = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .cache(provideCache())
                .addInterceptor(networkInterceptor)
                .addInterceptor(logging)
                .addInterceptor(headerInterceptor)

            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            return Retrofit.Builder()
                .baseUrl(baseURL)
                .client(okHttpBuilder.build())
                .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()
                .create(ApiRestService::class.java)
        }
    }
}
