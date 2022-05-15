package com.example.mumulcom

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

//인터페이스 생성
interface AnswerRetrofitInterface {
    @Multipart
    @POST("/replies")
    fun answer(
        @Header("X-ACCESS-TOKEN") jwt: String,
        @Part images: List<MultipartBody.Part?>?,
        @Part ("postReplyReq") postReplyReq: Answer
    ): Call<AnswerResponse>
}