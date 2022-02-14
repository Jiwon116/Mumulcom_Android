package com.example.mumulcom

import com.google.gson.annotations.SerializedName

// 회원 조회 & 수정
data class Profile(
    @SerializedName("jwt") var jwt: String,    // jwt
    @SerializedName("userIdx") var userIdx: Long,  // 유저 식별 번호
    @SerializedName("email") var email: String,  // 유저 이메일
    @SerializedName("name") var name: String,   // 유저 이름
    @SerializedName("nickname") var nickname: String, // 유저 닉네임
    @SerializedName("group") var group: String,   // 유저 소속
    @SerializedName("myCategories") var myCategories: MutableList<String>?,   // 관심 코딩 분야, null 가능
    @SerializedName("profileImgUrl") var profileImgUrl: String,    // 유저 프로필 url
)
