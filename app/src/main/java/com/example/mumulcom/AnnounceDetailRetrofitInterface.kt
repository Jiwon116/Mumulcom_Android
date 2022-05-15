package com.example.mumulcom

import retrofit2.Call
import retrofit2.http.*

interface AnnounceDetailRetrofitInterface {
    // 공지사항 세부내용
    @GET("/announce/{announceIdx}")
    fun getAnnouncesDetail(
        @Path("announceIdx") announceIdx : Long,
    ): Call<AnnounceDetailResponse>
}