package com.dc.todomvvmretrofit.data.network

import com.dc.todomvvmretrofit.data.model.*
import com.dc.todomvvmretrofit.utils.EndPoints
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

//    @FormUrlEncoded
//    @POST(EndPoints.userLogin)
//    fun userLogin(
//        @Field("email") email: String,
//        @Field("password") password: String
//    ): Call<LoginResponse>

    @POST(EndPoints.userLogin)
    fun userLogin(@Body request: LoginRequestModel): Call<LoginResponse>


    @POST(EndPoints.userRegister)
    fun userRegister(
        @Body request: RegisterRequestModel
    ): Call<LoginResponse>

    @GET(EndPoints.refreshToken)
    fun refreshToken(@Header("Authorization") token: String?): Call<TokenResponse>

    @GET(EndPoints.todoList)
    fun todoList(): Call<TodoListResponse>

    @FormUrlEncoded
    @POST(EndPoints.addTodo)
    fun addTodo(
        @Field("title") title: String?,
        @Field("description") description: String?,
        @Field("dateTime") dateTime: String?,
        @Field("priority") priority: String?,
    ): Call<TodoResponse>

    @FormUrlEncoded
    @POST(EndPoints.updateTodo)
    fun updateTodo(
        @Field("todoId") todoId: String?,
        @Field("title") title: String?,
        @Field("description") description: String?,
        @Field("dateTime") dateTime: String?,
        @Field("priority") priority: String?,
    ): Call<TodoResponse>

    @FormUrlEncoded
    @POST(EndPoints.deleteTodo)
    fun deleteTodo(@Field("todoId") todoId: String): Call<TodoResponse>
}