package com.example.mumulcom

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

//인터페이스 생성
interface AnswerRetrofitInterface {


    @POST("/replies")
    fun answer(
        @Header("X-ACCESS-TOKEN") jwt: String,
        @Body answer: Answer
    ): Call<AnswerResponse>
}