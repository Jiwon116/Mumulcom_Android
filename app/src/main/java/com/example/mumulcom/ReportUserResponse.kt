package com.example.mumulcom

import com.google.gson.annotations.SerializedName

data class ReportUserResponse(
    @SerializedName("isSuccess") var isSuccess: Boolean,
    @SerializedName("code") var code:Int,
    @SerializedName("message") var message:String,
    @SerializedName("result") var result:ReportUserId
)
