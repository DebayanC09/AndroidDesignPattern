package com.dc.todomvvmretrofit.ui.viewmodel.todo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dc.todomvvmretrofit.base.GeneralState
import com.dc.todomvvmretrofit.data.model.TodoListResponse
import com.dc.todomvvmretrofit.data.model.TodoModel
import com.dc.todomvvmretrofit.data.model.TodoResponse
import com.dc.todomvvmretrofit.data.repository.todo.TodoRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TodoListViewModel(private val repository: TodoRepository) : ViewModel() {

    private val _todoListState: MutableLiveData<GeneralState<TodoListResponse>> = MutableLiveData()
    val todoListState: LiveData<GeneralState<TodoListResponse>> get() = _todoListState
    val todoList: LiveData<List<TodoModel>> get() = repository.todoList

    init {
        fetchTodoList()
    }

    fun fetchTodoList() {
        _todoListState.postValue(GeneralState.Loading)
        repository.todoList().enqueue(object : Callback<TodoListResponse> {
            override fun onResponse(
                call: Call<TodoListResponse>,
                response: Response<TodoListResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        if (body.status.equals("1", false) && body.statusCode.equals(
                                "200",
                                false
                            )
                        ) {
                            body.data?.let {
                                repository.setTodoList(it)
                            }
                            _todoListState.postValue(GeneralState.Success(""))
                        } else {
                            body.message?.let {
                                _todoListState.postValue(GeneralState.Error(0, it))
                            } ?: kotlin.run {
                                _todoListState.postValue(
                                    GeneralState.Error(
                                        0,
                                        "Something went wrong"
                                    )
                                )
                            }
                        }
                    } ?: kotlin.run {
                        _todoListState.postValue(GeneralState.Error(0, "Something went wrong"))
                    }
                } else {
                    _todoListState.postValue(GeneralState.Error(0, "Something went wrong"))
                }
            }

            override fun onFailure(call: Call<TodoListResponse>, t: Throwable) {
                _todoListState.postValue(GeneralState.Error(0, t.message.toString()))
            }
        })
    }

    fun deleteTodo(todoId: String): LiveData<GeneralState<TodoModel>> {
        val observer: MutableLiveData<GeneralState<TodoModel>> = MutableLiveData()
        observer.postValue(GeneralState.Loading)
        repository.deleteTodo(todoId).enqueue(object : Callback<TodoResponse> {
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
                            repository.deleteItemFromList(todoId)
                            body.message?.let {
                                observer.postValue(GeneralState.Success(it))
                            } ?: kotlin.run {
                                observer.postValue(GeneralState.Success(""))
                            }
                        } else {
                            body.message?.let {
                                observer.postValue(GeneralState.Error(0, it))
                            } ?: kotlin.run {
                                observer.postValue(
                                    GeneralState.Error(
                                        0,
                                        "Something went wrong"
                                    )
                                )
                            }
                        }
                    } ?: kotlin.run {
                        observer.postValue(GeneralState.Error(0, "Something went wrong"))
                    }
                }
            }

            override fun onFailure(call: Call<TodoResponse>, t: Throwable) {
                observer.postValue(GeneralState.Error(0, t.message.toString()))
            }
        })
        return observer
    }


    @Suppress("UNCHECKED_CAST")
    class ViewModelFactory(private val repository: TodoRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TodoListViewModel::class.java)) {
                return TodoListViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
