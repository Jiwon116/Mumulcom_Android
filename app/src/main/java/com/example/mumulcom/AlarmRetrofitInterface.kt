package com.example.mumulcom

import retrofit2.Call
import retrofit2.http.*

// 저장한 질문 보기
interface AlarmRetrofitInterface {
    @GET("/notices/{userIdx}")  // 코딩 질문
    fun getAlarms(
        @Header("X-ACCESS-TOKEN") jwt : String, // jwt
        @Path("userIdx") userIdx : Long,    // 유저 인덱스
    ):Call<AlarmResponse>
}