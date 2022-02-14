package com.example.mumulcom

import com.google.gson.annotations.SerializedName

class DetailCodingQuestionResponse (
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code:Int,
    @SerializedName("message") val message:String,
    @SerializedName("result") val result: ArrayList<DetailCodingQuestion>
)