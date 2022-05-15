package com.example.mumulcom

import com.google.gson.annotations.SerializedName
//val jwt:String

data class CheckCodingQuestionResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean, // 요청 성공 여부
    @SerializedName("code") var code:Int, // 응답코드
    @SerializedName("message") val message:String, // 응답 메세지
    @SerializedName("result") val result: String // 댓글을 업로드한 결과
)