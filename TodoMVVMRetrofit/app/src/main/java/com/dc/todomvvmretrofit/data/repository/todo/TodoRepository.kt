package com.dc.todomvvmretrofit.data.repository.todo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dc.todomvvmretrofit.data.model.TodoListResponse
import com.dc.todomvvmretrofit.data.model.TodoModel
import com.dc.todomvvmretrofit.data.model.TodoResponse
import com.dc.todomvvmretrofit.data.network.ApiService
import retrofit2.Call

class TodoRepository(private val apiService: ApiService) {
    companion object {
        private lateinit var todoRepository: TodoRepository

        fun instance(apiService: ApiService): TodoRepository {
            if (!Companion::todoRepository.isInitialized) {
                todoRepository = TodoRepository(apiService)
            }
            return todoRepository
        }
    }

    private val _todoList: MutableLiveData<List<TodoModel>> = MutableLiveData()
    val todoList: LiveData<List<TodoModel>> get() = _todoList
    fun setTodoList(list: List<TodoModel>) {
        _todoList.postValue(list)
    }

    fun addTodoItemToList(item: TodoModel) {
        val list: MutableList<TodoModel> = _todoList.value as MutableList<TodoModel>
        list.add(item)
        _todoList.postValue(list)
    }

    fun deleteItemFromList(id: String) {
        val list: MutableList<TodoModel> = _todoList.value as MutableList<TodoModel>
        list.removeIf {
            it.id == id
        }
        _todoList.postValue(list)
    }

    fun updateTodoItemToList(todoModel: TodoModel) {
        val list: MutableList<TodoModel> = _todoList.value as MutableList<TodoModel>
        val position: Int = list.indexOfFirst {
            it.id == todoModel.id
        }
        list[position] = todoModel
        _todoList.postValue(list)
    }

    fun todoList(): Call<TodoListResponse> {
        return apiService.todoList()
    }

    fun addTodo(todoModel: TodoModel): Call<TodoResponse> {
        return apiService.addTodo(
            title = todoModel.title,
            description = todoModel.description,
            dateTime = todoModel.dateTime,
            priority = todoModel.priority,
        )
    }

    fun updateTodo(todoModel: TodoModel): Call<TodoResponse> {
        return apiService.updateTodo(
            todoId = todoModel.id,
            title = todoModel.title,
            description = todoModel.description,
            dateTime = todoModel.dateTime,
            priority = todoModel.priority,
        )
    }

    fun deleteTodo(todoId: String): Call<TodoResponse> {
        return apiService.deleteTodo(todoId)
    }
}