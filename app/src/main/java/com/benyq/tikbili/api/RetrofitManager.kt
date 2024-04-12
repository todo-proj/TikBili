package com.benyq.tikbili.api

import android.util.Log
import com.benyq.tikbili.bilibili.BilibiliCollectApi
import com.benyq.tikbili.base.ext.MMKVValue
import com.google.gson.Gson
import okhttp3.Interceptor
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
object RetrofitManager {
    private val biliCookie by MMKVValue("biliCookie", "")

    val bilibiliApi by lazy { provideBilibiliApi() }

    private fun provideBilibiliApi(): BilibiliCollectApi {
        val gson = Gson()
        // log interceptor
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d("benyq", "okhttp: $message")
        }
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val bilibiliHeader = Interceptor { chain ->
            val newBuilder = chain.request().newBuilder()
                .addHeader("Referer","https://www.bilibili.com/")
                .addHeader("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36")
                .addHeader("Origin","https://www.bilibili.com")
                .addHeader("Accept","*/*")
                .addHeader("Accept-Language","zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7")
                .addHeader("Cookie",biliCookie)
                .build()
            chain.proceed(newBuilder)
        }
        val okhttpClient = OkHttpClient.Builder().apply {
            addNetworkInterceptor(bilibiliHeader)
            addInterceptor(loggingInterceptor)
            readTimeout(30, TimeUnit.SECONDS)
            writeTimeout(30, TimeUnit.SECONDS)
            connectTimeout(30, TimeUnit.SECONDS)
            retryOnConnectionFailure(true)
            build()
        }.build()

        val retrofitBuilder = Retrofit.Builder()
            .baseUrl(BilibiliCollectApi.BASE_URL)
            .client(okhttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))

        val retrofit = retrofitBuilder.build()
        return retrofit.create(BilibiliCollectApi::class.java)
    }
}