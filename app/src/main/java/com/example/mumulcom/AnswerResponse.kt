package com.example.mumulcom

import com.google.gson.annotations.SerializedName
//val jwt:String

data class AnswerResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code:Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: Replies?
)

