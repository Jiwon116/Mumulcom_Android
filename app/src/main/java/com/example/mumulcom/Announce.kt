package com.example.mumulcom

import com.google.gson.annotations.SerializedName

// 공지사항
data class Announce(
    @SerializedName("id") var id: Long,  // 공지글 번호
    @SerializedName("title") var title: String,   // 공지 제목
    @SerializedName("content") var content: String, // 공지 내용
    @SerializedName("createdAt") var createdAt: String,   // 공지 일자
)
