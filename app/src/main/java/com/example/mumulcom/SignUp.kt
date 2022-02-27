package com.example.mumulcom

import com.google.gson.annotations.SerializedName

// 회원가입
data class SignUp(
    @SerializedName("email") var email: String?,  // 유저 이메일
    @SerializedName("name") var name: String?,   // 유저 이름
    @SerializedName("nickname") var nickname: String?, // 유저 닉네임
    @SerializedName("group") var group: String?,   // 유저 소속
    @SerializedName("myCategories") var myCategories: List<String>?,   // 관심 코딩 분야, null 가능
)
