package com.example.mumulcom

import com.google.gson.annotations.SerializedName

data class CheckConcept(
    @SerializedName("userIdx") val userIdx:Long, // 댓글 작성자 고유 번호
    @SerializedName("bigCategoryIdx") val bigCategoryIdx:Long, // 댓글 고유 번호
    @SerializedName("smallCategoryIdx") val smallCategoryIdx:Long?, // 댓글 고유 번호
    @SerializedName("title") val title:String, // 댓글 내용
    @SerializedName("content") val content:String?  // 댓글 첨부 이미지 (null 가능)
)