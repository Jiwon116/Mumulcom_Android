package com.example.mumulcom

import com.google.gson.annotations.SerializedName

data class LikeSend (
    @SerializedName("questionIdx") val questionIdx: Long,  // 질문 고유 번호 (좋아요를 누른 질문 번호)
    @SerializedName("userIdx") val userIdx: Long  // 유저 고유 번호 (좋아요를 한 유저 번호)

)