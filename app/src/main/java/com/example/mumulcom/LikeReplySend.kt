package com.example.mumulcom

import com.google.gson.annotations.SerializedName

data class LikeReplySend(
    @SerializedName("replyIdx") val replyIdx: Long,  // 답변 고유 번호 (좋아요를 누른 답변 번호)
    @SerializedName("userIdx") val userIdx: Long  // 유저 고유 번호 (좋아요를 한 유저 번호)

)