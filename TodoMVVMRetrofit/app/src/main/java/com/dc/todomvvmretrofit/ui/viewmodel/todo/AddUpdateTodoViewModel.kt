package com.dc.todomvvmretrofit.ui.viewmodel.todo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dc.todomvvmretrofit.data.model.TodoModel
import com.dc.todomvvmretrofit.data.model.TodoResponse
import com.dc.todomvvmretrofit.data.repository.todo.TodoRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddUpdateTodoViewModel(private val repository: TodoRepository) : ViewModel() {
    private var titleError: String? = null
    private var descriptionError: String? = null
    private var dateTimeError: String? = null
    private var priorityError: String? = null

    fun addTodo(
        title: String,
        description: String,
        dateTime: String,
        priority: String
    ): LiveData<State> {
        val observer: MutableLiveData<State> = MutableLiveData()

        val hasError = validateFields(title, description, dateTime, priority)
        if (hasError) {
            observer.postValue(
                State.ValidationError(
                    titleError,
                    descriptionError,
                    dateTimeError,
                    priorityError
                )
            )
        } else {
            observer.postValue(State.Loading)
            repository.addTodo(
                TodoModel(
                    title = title,
                    description = description,
                    dateTime = dateTime,
                    priority = priority
                )
            ).enqueue(object : Callback<TodoResponse> {
                override fun onResponse(
                    call: Call<TodoResponse>,
                    response: Response<TodoResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { body ->
                            if (body.status.equals("1", false) && body.statusCode.equals(
                                    "200",
                                    false
                                )
                            ) {
                                body.data?.let {
                                    repository.addTodoItemToList(it)
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

                override fun onFailure(call: Call<TodoResponse>, t: Throwable) {
                    observer.postValue(State.Error(t.message.toString()))
                }
            })

        }
        return observer
    }

    fun updateTodo(
        todoId: String,
        title: String,
        description: String,
        dateTime: String,
        priority: String
    ): MutableLiveData<State> {

        val observer: MutableLiveData<State> = MutableLiveData()

        val hasError = validateFields(title, description, dateTime, priority)
        if (hasError) {
            observer.postValue(
                State.ValidationError(
                    titleError,
                    descriptionError,
                    dateTimeError,
                    priorityError
                )
            )
        } else {
            observer.postValue(State.Loading)
            val todoModel = TodoModel(
                id = todoId,
                title = title,
                description = description,
                dateTime = dateTime,
                priority = priority
            )
            repository.updateTodo(
                todoModel = todoModel
            ).enqueue(object : Callback<TodoResponse> {
                override fun onResponse(
                    call: Call<TodoResponse>,
                    response: Response<TodoResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { body ->
                            if (body.status.equals("1", false) && body.statusCode.equals(
                                    "200",
                                    false
                                )
                            ) {
                                repository.updateTodoItemToList(todoModel)
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

                override fun onFailure(call: Call<TodoResponse>, t: Throwable) {
                    observer.postValue(State.Error(t.message.toString()))
                }
            })

        }
        return observer
    }

    private fun validateFields(
        title: String,
        description: String,
        dateTime: String,
        priority: String
    ): Boolean {
        var hasError = false
        if (title.isEmpty()) {
            hasError = true
            titleError = "Please enter title"
        } else {
            titleError = null
        }
        if (description.isEmpty()) {
            hasError = true
            descriptionError = "Please enter description"
        } else {
            descriptionError = null
        }
        if (dateTime.isEmpty()) {
            hasError = true
            dateTimeError = "Please enter date time"
        } else {
            dateTimeError = null
        }
        if (priority.isEmpty()) {
            hasError = true
            priorityError = "Please select priority"
        } else {
            priorityError = null
        }
        return hasError
    }

    @Suppress("UNCHECKED_CAST")
    class ViewModelFactory(private val repository: TodoRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AddUpdateTodoViewModel::class.java)) {
                return AddUpdateTodoViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    sealed class State {
        object Loading : State()
        data class Success(val message: String) : State()
        data class ValidationError(
            val titleError: String?,
            val descriptionError: String?,
            val dateTimeError: String?,
            val priorityError: String?,
        ) : State()

        data class Error(val message: String) : State()
    }
}