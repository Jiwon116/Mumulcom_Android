package com.example.mumulcom

import retrofit2.Call
import retrofit2.http.*

interface ProfileRetrofitInterface {
    // 회원 정보 조회
    @GET("/users/{userIdx}")
    fun getProfiles(
        @Header("X-ACCESS-TOKEN") jwt : String, // jwt
        @Path("userIdx") userIdx: Long,    //유저 식별자
    ):Call<ProfileResponse>


    // 회원 정보 수정
    @PATCH("/users/{userIdx}")
    fun setProfiles(
        @Header("X-ACCESS-TOKEN") jwt : String, // jwt
        @Path("userIdx") userIdx: Long,    //유저 식별자
        @Body profileModify: ProfileModify, // 변경한 유저 정보
    ):Call<ProfileResponse>
}