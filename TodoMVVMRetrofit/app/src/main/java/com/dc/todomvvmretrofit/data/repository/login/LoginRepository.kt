package com.dc.todomvvmretrofit.data.repository.login

import com.dc.todomvvmretrofit.data.model.LoginRequestModel
import com.dc.todomvvmretrofit.data.model.LoginResponse
import com.dc.todomvvmretrofit.data.network.ApiService
import retrofit2.Call

class LoginRepository(private val apiService: ApiService) {
    companion object {
        private lateinit var loginRepository: LoginRepository
        fun instance(apiService: ApiService): LoginRepository {
            if (!::loginRepository.isInitialized) {
                loginRepository = LoginRepository(apiService)
            }
            return loginRepository
        }
    }

    fun userLogin(email: String, password: String): Call<LoginResponse> {
        val request = LoginRequestModel(email = email, password = password)
        return apiService.userLogin(request)
    }
}