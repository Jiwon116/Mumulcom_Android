package com.example.mumulcom

import com.google.gson.annotations.SerializedName

// 로그인
data class Login(
    @SerializedName("email") var email: String,  // 유저 이메일
)
