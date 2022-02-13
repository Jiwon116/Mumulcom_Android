package com.example.mumulcom

import com.google.gson.annotations.SerializedName

data class ConceptQuestion(
    @SerializedName("questionIdx") var questionIdx: Long,   // 질문 인덱스
    @SerializedName("profileImgUrl") var profileImgUrl: String, // 유저 프로필 이미지 url
    @SerializedName("nickname") var nickname: String,   // 유저 닉네임
    @SerializedName("bigCategoryName") var bigCategoryName: String?, // 상위 카테고리, null 가능
    @SerializedName("smallCategoryName") var smallCategoryName: String?, // 하위 카테고리, null 가능
    @SerializedName("title") var title: String, // 질문 제목
    @SerializedName("createdAt") var createdAt: String, // 질문 작성 일자
    @SerializedName("content") var content: String, // 질문 내용
    @SerializedName("likeCount") var likeCount: Int,    // 질문 좋아요 개수
    @SerializedName("replyCount") var replyCount: Int,  // 질문 답변 개수
)