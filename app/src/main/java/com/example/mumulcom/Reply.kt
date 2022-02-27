package com.example.mumulcom

import com.google.gson.annotations.SerializedName

data class Reply(
    @SerializedName("replyIdx") val replyIdx : Long, // 답변 고유 번호
    @SerializedName("questionIdx") val questionIdx:Long, // 질문 고유 번호
    @SerializedName("userIdx") val userIdx:Long, // 유저 고유 번호
    @SerializedName("nickname") val nickname:String, // 유저 닉네임
    @SerializedName("profileImgUrl") val profileImgUrl:String, // 유저 프로필
    @SerializedName("createdAt") val createdAt:String, // 답변 작성 일자
    @SerializedName("replyUrl") val replyUrl:String, // 답변 참고 url --> ??
    @SerializedName("content") val content:String, // 답변 내용
    @SerializedName("replyImgUrl") val replyImgUrl:ArrayList<String>, // 답변 이미지
    @SerializedName("likeCount") val likeCount:Int, // 좋아요 수
    @SerializedName("reReplyCount") val reReplyCount:Int, // 대댓글
    @SerializedName("status") val status:String, // 채택 여부 (Y/N)
    @SerializedName("isLiked") val isLiked:String, // 답변에 대한 좋아요 여부
)