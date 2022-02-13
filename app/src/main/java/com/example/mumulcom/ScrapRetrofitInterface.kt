package com.example.mumulcom

import retrofit2.Call
import retrofit2.http.*

// 저장한 질문 보기
interface ScrapRetrofitInterface {
    @GET("/scraps/coding/?")  // 코딩 질문
    fun getScrapCodingQuestions(
        @Header("X-ACCESS-TOKEN") jwt : String, // jwt
        @Query("userIdx") userIdx: Long,    //유저 식별자
        @Query("isReplied") isReplied: Boolean, // 답변 달린 질문만 보기 (true) / 전체 질문 보기 (false)
        @Query("bigCategory") bigCategory: String?,  // 상위 카테고리, 빈 값 가능
        @Query("smallCategory") smallCategory: String?,  // 하위 카테고리, 빈 값 가능
    ):Call<ScrapResponse>

    @GET("/scraps/concept/?")  // 검색 질문
    fun getScrapConceptQuestions(
        @Header("X-ACCESS-TOKEN") jwt : String, // jwt
        @Query("userIdx") userIdx: Long,    //유저 식별자
        @Query("isReplied") isReplied: Boolean, // 답변 달린 질문만 보기 (true) / 전체 질문 보기 (false)
        @Query("bigCategory") bigCategory: String?,  // 상위 카테고리, 빈 값 가능
        @Query("smallCategory") smallCategory: String?,  // 하위 카테고리, 빈 값 가능
    ):Call<ScrapResponse>
}