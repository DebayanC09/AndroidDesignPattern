package com.dc.todomvvmretrofit.ui.viewmodel.signup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dc.todomvvmretrofit.data.model.LoginResponse
import com.dc.todomvvmretrofit.data.repository.signup.SignUpRepository
import com.dc.todomvvmretrofit.utils.setUserdata
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpViewModel(
    private val repository: SignUpRepository,
    application: Application
) : AndroidViewModel(application) {

    fun register(name: String, email: String, password: String): MutableLiveData<State> {
        val observer: MutableLiveData<State> = MutableLiveData()

        var hasError = false
        var nameError: String? = null
        var emailError: String? = null
        var passwordError: String? = null

        if (name.isEmpty()) {
            hasError = true
            nameError = "Please enter name"
        }
        if (email.isEmpty()) {
            hasError = true
            emailError = "Please enter email"
        }
        if (password.isEmpty()) {
            hasError = true
            passwordError = "Please enter password"
        }

        if (hasError) {
            observer.postValue(State.ValidationError(nameError, emailError, passwordError))
        } else {
            observer.postValue(State.Loading)

            repository.userRegister(name, email, password)
                .enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let { body ->
                                if (body.status.equals("1", false) && body.statusCode.equals(
                                        "201",
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
        private val repository: SignUpRepository,
        private val application: Application
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
                return SignUpViewModel(repository, application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    sealed class State {
        object Loading : State()
        data class Success(val message: String) : State()
        data class ValidationError(
            val nameError: String?,
            val emailError: String?,
            val passwordError: String?
        ) :
            State()

        data class Error(val message: String) : State()
    }

}