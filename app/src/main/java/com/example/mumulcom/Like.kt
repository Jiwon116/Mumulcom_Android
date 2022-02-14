package com.example.mumulcom

import com.google.gson.annotations.SerializedName

// 좋아요 했을때 받아오는 정보
data class Like(
    @SerializedName("noticeTargetUserIdx") val noticeTargetUserIdx: Long, //
    @SerializedName("noticeContent") val userIdx: Long, // 작

)