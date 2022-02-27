package com.example.mumulcom

import com.google.gson.annotations.SerializedName

data class Comment(
    @SerializedName("userIdx") val userIdx:Long, // 댓글 작성자 고유 번호
    @SerializedName("nickname") val nickname:String, // 댓글 작성자 닉네임
    @SerializedName("profileImgUrl") val profileUrl:String, // 댓글 작성자 닉네임
    @SerializedName("reReplyIdx") val reReplyIdx:Long, // 댓글 고유 번호
    @SerializedName("createdAt") val createdAt:String, // 댓글 고유 번호
    @SerializedName("content") val content:String, // 댓글 내용
    @SerializedName("imageUrl") val imageUrl:String?  // 댓글 첨부 이미지 (null 가능)
)