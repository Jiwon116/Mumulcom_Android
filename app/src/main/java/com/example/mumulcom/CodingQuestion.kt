package com.example.mumulcom

import com.google.gson.annotations.SerializedName

data class CodingQuestion(
    @SerializedName("questionIdx") var questionIdx: Long,   // 질문 식별번호
    @SerializedName("profileImgUrl") var profileImgUrl: String, // 질문 작성자 프로필 사진
    @SerializedName("nickname") var nickname: String,   // 유저 닉네임
    @SerializedName("bigCategoryName") var bigCategoryName: String?, // 상위 카테고리, null 가능
    @SerializedName("smallCategoryName") var smallCategoryName: String?, // 하위 카테고리, null 가능
    @SerializedName("title") var title: String, // 질문 제목
    @SerializedName("createdAt") var createdAt: String, // 질문 작성 일자
    @SerializedName("currentError") var currentError: String,   // 현재 막힌 부분
    @SerializedName("myCodingSkill") var myCodingSkill: String?,    // 현재 코딩 실력, null 가능
    @SerializedName("likeCount") var likeCount: Int,    // 질문 좋아요 개수
    @SerializedName("replyCount") var replyCount: Int,  // 질문 답변 개수
)
