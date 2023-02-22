package com.dc.todomvvmretrofit.data.network

import android.content.Context
import com.dc.todomvvmretrofit.ui.view.login.LoginActivity
import com.dc.todomvvmretrofit.utils.EndPoints
import com.dc.todomvvmretrofit.utils.getUserdata
import com.dc.todomvvmretrofit.utils.openActivity
import com.dc.todomvvmretrofit.utils.setUserdata
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    fun invokeWithOutAuth(): ApiService {
        val logInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val okHttpClientBuilder = OkHttpClient.Builder().apply {
            addInterceptor(logInterceptor)
        }
        val retrofit = Retrofit.Builder()
            .baseUrl(EndPoints.baseUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClientBuilder.build())
            .build()
        return retrofit.create(ApiService::class.java)
    }

    fun invokeWithAuth(context: Context): ApiService {
        val logInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val okHttpClientBuilder = OkHttpClient.Builder().apply {
            addInterceptor(logInterceptor)
        }
        okHttpClientBuilder.addInterceptor(
            Interceptor { chain ->
                val newRequest = chain.request().newBuilder()
                newRequest.addHeader("Authorization", "${context.getUserdata()?.token}")
                val response = chain.proceed(newRequest.build())
                when (response.code) {
                    403 -> {
                        context.setUserdata(null)
                        context.openActivity(
                            className = LoginActivity::class.java,
                            clearTask = true
                        )
                    }
                }
                response
            }).authenticator(
            TokenAuthenticator(
                context, invokeWithOutAuth()
            )
        )
        val retrofit = Retrofit.Builder()
            .baseUrl(EndPoints.baseUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClientBuilder.build())
            .build()
        return retrofit.create(ApiService::class.java)
    }
}