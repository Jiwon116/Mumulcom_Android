package com.example.mumulcom

import com.google.gson.annotations.SerializedName

// 개념 질문 조회 데이터 class
class DetailConceptQuestion ( // 12개

    @SerializedName("questionIdx") var questionIdx: Long,   // 질문 인덱스,
    @SerializedName("userIdx") var userIdx: Long,   // 질문 인덱스
    @SerializedName("nickname") var nickname: String,   // 유저 닉네임
    @SerializedName("profileImgUrl") var profileImgUrl: String, // 유저 프로필 이미지 url
    @SerializedName("createdAt") var createdAt: String, // 질문 작성 일자
    @SerializedName("title") var title: String, // 질문 제목
    @SerializedName("questionImgUrl") var questionImgUrls: ArrayList<String>, // 질문 이미지들 url (null ~ 여러개 가능)
    @SerializedName("content") var content: String, // 질문 내용
    @SerializedName("bigCategoryName") var bigCategoryName: String, // 상위 카테고리
    @SerializedName("smallCategoryName") var smallCategoryName: String?, // 하위 카테고리, null 가능
    @SerializedName("likeCount") var likeCount: Int,    // 질문 좋아요 개수
    @SerializedName("replyCount") var replyCount: Int,  // 질문 답변 개수
    @SerializedName("isLiked") var isLiked: String,  // 좋아요 여부
    @SerializedName("isScraped") var isScraped: String , // 스크랩 여부
    @SerializedName("isAdopted") var isAdopted: String,  // 이 질문에 답변에 채택이 됬는지 여부
)