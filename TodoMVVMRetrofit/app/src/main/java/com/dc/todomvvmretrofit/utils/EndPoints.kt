package com.dc.todomvvmretrofit.utils

object EndPoints {
    private const val isLive = true

    fun baseUrl(): String {
        return if (isLive) {
            "https://odd-erin-mackerel-tux.cyclic.app/"
        } else {
            "http://192.168.0.12:5000/"
        }
    }

    const val userLogin = "users/login"
    const val userRegister = "users/register"
    const val refreshToken = "auth/refreshToken"
    const val addTodo = "todo/addTodo"
    const val updateTodo = "todo/updateTodo"
    const val deleteTodo = "todo/deleteTodo"
    const val todoList = "todo/todoList"
}