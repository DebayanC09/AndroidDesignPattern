package com.dc.todomvvmretrofit.data.repository.signup

import com.dc.todomvvmretrofit.data.model.LoginResponse
import com.dc.todomvvmretrofit.data.model.RegisterRequestModel
import com.dc.todomvvmretrofit.data.network.ApiService
import retrofit2.Call

class SignUpRepository(private val apiService: ApiService) {
    companion object {
        private lateinit var loginRepository: SignUpRepository
        fun instance(apiService: ApiService): SignUpRepository {
            if (!Companion::loginRepository.isInitialized) {
                loginRepository = SignUpRepository(apiService)
            }
            return loginRepository
        }
    }

    fun userRegister(name: String, email: String, password: String): Call<LoginResponse> {
        val request = RegisterRequestModel(name = name, email = email, password = password)
        return apiService.userRegister(request)
    }
}