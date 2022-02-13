package com.example.mumulcom

import com.google.gson.annotations.SerializedName

data class Alarm(
    @SerializedName("questionIdx") val questionIdx: Long, // 질문 고유 번호
    @SerializedName("noticeContent") val noticeContent: String, // 알림 내용
    @SerializedName("diffTime") val diffTime: String, // 날짜 표현
    @SerializedName("title") val title: String, // 질문 제목
    @SerializedName("bigCategoryName") val bigCategoryName: String, // 상위 카테고리
    @SerializedName("type") val type: Int, // 질문 유형 (1 -> 코딩 질문 / 2 -> 개념 질문)
    @SerializedName("noticeCategoryIdx") val noticeCategoryIdx: Int, // 알림 유형 (1 -> 질문 / 2 -> 답변)
)
