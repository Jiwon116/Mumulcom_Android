package com.example.mumulcom

import com.google.gson.annotations.SerializedName

// 좋아요 했을때 받아오는 정보
data class Like(
    @SerializedName("noticeTargetUserIdx") val noticeTargetUserIdx: Long, // 푸쉬알림을 받을 유저 인덱스
    @SerializedName("noticeContent") val noticeContent: String, // 푸쉬알림 내용

)