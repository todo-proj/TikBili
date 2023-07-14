package com.benyq.tikbili.api

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 *
 * @author benyq
 * @date 7/13/2023
 *
 */
class RetrofitManager {

    fun createRetrofit() {
        val gson = Gson()
        // log interceptor
        val loggingInterceptor = HttpLoggingInterceptor { message ->

        }
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val okhttpClient = OkHttpClient.Builder().apply {
            addInterceptor(loggingInterceptor)
            readTimeout(30, TimeUnit.SECONDS)
            writeTimeout(30, TimeUnit.SECONDS)
            connectTimeout(30, TimeUnit.SECONDS)
            retryOnConnectionFailure(true)
            build()
        }.build()

        val retrofitBuilder = Retrofit.Builder()
            .baseUrl("")
            .client(okhttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))

        val retrofit = retrofitBuilder.build()
    }
}