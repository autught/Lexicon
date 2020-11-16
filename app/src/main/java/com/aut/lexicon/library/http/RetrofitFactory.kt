package com.snow.sharker.http

import com.aut.lexicon.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @description:
 * @author:  79120
 * @date :   2020/9/2 14:52
 */
class RetrofitFactory private constructor() {
    val apiService: ApiService

    init {
        val clientBuilder = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            clientBuilder.addInterceptor(loggingInterceptor)
        }
//        clientBuilder.addNetworkInterceptor(HeaderInterceptor())
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .client(clientBuilder.build())
            .build()
        apiService = retrofit.create<ApiService>(ApiService::class.java)
    }

    private object Singleton {
        val holder = RetrofitFactory()
    }

    companion object {
        private const val baseUrl = "https://app.yuzhilai.com"
        val instance = Singleton.holder

    }
}