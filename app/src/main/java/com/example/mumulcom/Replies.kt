package com.example.mumulcom

import com.google.gson.annotations.SerializedName

data class Replies(
    @SerializedName("replyIdx") var replyIdx: Long,
    @SerializedName("replyImgResult") var replyImgResult: String,
    @SerializedName("noticeReply") var noticeReply: String,
    @SerializedName("questionUserIdx") var questionUserIdx: Long,
    @SerializedName("noticeScrap") var noticeScrap: String,
    @SerializedName("scrapUserIdx") var scrapUserIdx: List<Long>
)
