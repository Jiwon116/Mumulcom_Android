package com.example.mumulcom

import retrofit2.Call
import retrofit2.http.*

interface AnnounceRetrofitInterface {
    // 공지사항
    @GET("/announce")
    fun getAnnounces(
    ):Call<AnnounceResponse>
}