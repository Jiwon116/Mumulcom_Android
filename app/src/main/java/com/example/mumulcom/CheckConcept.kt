package com.example.mumulcom

import com.google.gson.annotations.SerializedName

data class CheckConcept(
    @SerializedName("images") val images: List<String>?,//이미지 우선 List<multiplefile>
    @SerializedName("userIdx") var userIdx: Long, //유저
    @SerializedName("bigCategoryIdx") val bigCategoryIdx: Long, // 상위 카테고리 (앱,웹,서버,기타)
    @SerializedName("smallCategoryIdx") val smallCategoryIdx: Long?, // 하위 카테고리 (안드로이드,ios,html,css,.....)
    @SerializedName("title") val title: String,// 질문 제목
    @SerializedName("content") val content: String//내용
)
