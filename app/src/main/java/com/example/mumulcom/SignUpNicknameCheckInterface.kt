package com.example.mumulcom

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SignUpNicknameCheckInterface {
    @GET ("/users/exists")  // 닉네임 중복 검색
    fun getNicknameCheck(
        @Query("nickname") type: String?,   // 닉네임
    ): Call<SignUpNicknameResponse>
}