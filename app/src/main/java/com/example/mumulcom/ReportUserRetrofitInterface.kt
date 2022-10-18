package com.example.mumulcom

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ReportUserRetrofitInterface {
    @POST("/reports")// 신고하기
    fun postReportUser(
        @Header("X-ACCESS-TOKEN") token : String,
        @Body body : PostUserReq
    ): Call<ReportUserResponse>
}