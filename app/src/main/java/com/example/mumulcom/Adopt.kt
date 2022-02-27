package com.example.mumulcom

import com.google.gson.annotations.SerializedName

data class Adopt(
    @SerializedName("answerer") val answerer : Long, // 답변자, 알림을 받는 유저의 식별자
    @SerializedName("questionIdx") val questionIdx:Long, // 질문 식별자
    @SerializedName("noticeMessage") val noticeMessage:String // 채택 알림 메세지

)