package com.example.mumulcom

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface LoginRetrofitInterface {
    @POST("/users/login")  // 로그인
    fun login(@Body email: Login): Call<LoginResponse>
}