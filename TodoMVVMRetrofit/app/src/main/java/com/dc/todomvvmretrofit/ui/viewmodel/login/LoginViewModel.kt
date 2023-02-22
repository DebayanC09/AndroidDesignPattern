package com.dc.todomvvmretrofit.ui.viewmodel.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dc.todomvvmretrofit.data.model.LoginResponse
import com.dc.todomvvmretrofit.data.repository.login.LoginRepository
import com.dc.todomvvmretrofit.utils.setUserdata
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(
    private val loginRepository: LoginRepository,
    application: Application
) : AndroidViewModel(application) {

    fun login(email: String, password: String): MutableLiveData<State> {
        val observer: MutableLiveData<State> = MutableLiveData()

        var hasError = false
        var emailError: String? = null
        var passwordError: String? = null

        if (email.isEmpty()) {
            hasError = true
            emailError = "Please enter email"
        }
        if (password.isEmpty()) {
            hasError = true
            passwordError = "Please enter password"
        }

        if (hasError) {
            observer.postValue(State.ValidationError(emailError, passwordError))
        } else {
            observer.postValue(State.Loading)

            loginRepository.userLogin(email, password).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { body ->
                            if (body.status.equals("1", false) && body.statusCode.equals(
                                    "200",
                                    false
                                )
                            ) {
                                body.user?.let {
                                    getApplication<Application>().setUserdata(it)

                                }
                                body.message?.let {
                                    observer.postValue(State.Success(it))
                                } ?: kotlin.run {
                                    observer.postValue(State.Success(""))
                                }
                            } else {
                                body.message?.let {
                                    observer.postValue(State.Error(it))
                                } ?: kotlin.run {
                                    observer.postValue(State.Error("Something went wrong"))
                                }
                            }
                        } ?: kotlin.run {
                            observer.postValue(State.Error("Something went wrong"))
                        }
                    } else {
                        observer.postValue(State.Error("Something went wrong"))
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    observer.postValue(State.Error(t.message.toString()))
                }
            })
        }

        return observer
    }

    @Suppress("UNCHECKED_CAST")
    class ViewModelFactory(
        private val repository: LoginRepository,
        private val application: Application
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                return LoginViewModel(repository, application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    sealed class State {
        object Loading : State()
        data class Success(val message: String) : State()
        data class ValidationError(val emailError: String?, val passwordError: String?) :
            State()

        data class Error(val message: String) : State()
    }

}