package com.example.mumulcom

import com.google.gson.annotations.SerializedName


// 답변에 댓글달때 보내는 데이터 class
data class CommentSend(
    @SerializedName("replyIdx") val replyIdx: Long,  // 지금 댓글작성하는 답변 id
    @SerializedName("userIdx") val userIdx: Long, // 지금 댓글 작성하는 작성자 id
    @SerializedName("content") val content: String, // 지금 댓글 작성하 내용
    @SerializedName("imageUrl") val imageUrl: String? // 댓글에 첨부할 이미지

)