package com.example.mumulcom

import com.google.gson.annotations.SerializedName

data class UploadCommentResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean, // 요청 성공 여부
    @SerializedName("code") val code:Int, // 응답코드
    @SerializedName("message") val message:String, // 응답 메세지
    @SerializedName("result") val result: Like // 댓글을 업로드한 결과
)
