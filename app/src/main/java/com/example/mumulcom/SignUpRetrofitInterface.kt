package com.example.mumulcom

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SignUpRetrofitInterface {
    @POST("/users")  // 회원 가입
    fun signUp(@Body user: User): Call<SignUpResponse>
}