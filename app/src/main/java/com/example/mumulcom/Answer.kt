package com.example.mumulcom

import com.google.gson.annotations.SerializedName

data class Answer(
    @SerializedName("questionIdx") var questionIdx: Long,//질문번호
    @SerializedName("userIdx") var userIdx: Long, //유저
    @SerializedName("replyUrl") var replyUrl: String?,
    @SerializedName("content") var content: String,
//    @SerializedName("images") val images: ArrayList<String>?,//이미지 우선 List<multiplefile>

)
