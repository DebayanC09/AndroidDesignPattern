package com.dc.todomvvmretrofit.data.network

import android.content.Context
import com.dc.todomvvmretrofit.data.model.TokenResponse
import com.dc.todomvvmretrofit.utils.getUserdata
import com.dc.todomvvmretrofit.utils.setUserdata
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(private val context: Context, private val apiService: ApiService) :
    Authenticator {
    override fun authenticate(route: Route?, response: Response): Request {
        val newToken = runBlocking {
            getNewToken(context, apiService)
        }
        return response.request.newBuilder()
            .header("Authorization", newToken)
            .build()
    }

    private fun getNewToken(context: Context, apiService: ApiService): String {
        context.getUserdata()?.let { userData ->
            val response: retrofit2.Response<TokenResponse> =
                apiService.refreshToken(userData.token).execute()
            userData.token = response.body()?.token
            context.setUserdata(userData)
            response.body()?.token?.let {
                return it
            }
        }
        return ""
    }
}