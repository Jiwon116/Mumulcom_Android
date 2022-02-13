package com.example.mumulcom

import com.google.gson.annotations.SerializedName

data class Question(
    @SerializedName("questionIdx") val questionIdx: Long, // 질문 고유 번호
    @SerializedName("userIdx") val userIdx: Long, // 작성자 고유 번호
    @SerializedName("type") val type: Int, // 질문 유형 (코딩질문 :1 , 개념질문 2)
    @SerializedName("nickname") val nickname: String, // 작성자 이름
    @SerializedName("profileImgUrl") val profileImgUrl: String, // 작성자 이름
    @SerializedName("createdAt") val createdAt: String, // 작성한 날짜
    @SerializedName("title") val title: String, // 질문 제목
    @SerializedName("bigCategoryName") val bigCategoryName: String, // 상위 카테고리 (앱,웹,서버,기타)
    @SerializedName("smallCategoryName") val smallCategoryName: String?, // (안드로이드,ios,html,css,.....)
    @SerializedName("likeCount") val likeCount: Int, // 좋아요 한 갯수
    @SerializedName("replyCount") val replyCount: Int, // 답변 갯수
)