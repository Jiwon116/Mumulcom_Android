package com.example.mumulcom

import retrofit2.Call
import retrofit2.http.*

interface ProfileRetrofitInterface {
    // 회원 정보 조회
    @GET("/questions/my/coding/{userIdx}")  // 코딩 질문
    fun getProfiles(
        @Header("X-ACCESS-TOKEN") jwt : String, // jwt
        @Query("userIdx") userIdx: Long,    //유저 식별자
    ):Call<ProfileResponse>
}