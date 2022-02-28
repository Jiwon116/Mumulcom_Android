package com.example.mumulcom

import com.google.gson.annotations.SerializedName

// 로그인 시 서버에 보내는 정보 (유저 이메일)
data class Login(
    @SerializedName("email") var email: String,  // 유저 이메일
)
