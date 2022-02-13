package com.example.mumulcom

import retrofit2.Call
import retrofit2.http.*

interface TabRetrofitInterface {

    // 내가 한 질문 보기
    @GET("/questions/my/coding/?")  // 코딩 질문
    fun getTabCodingQuestions(
        @Header("X-ACCESS-TOKEN") jwt : String, // jwt
        @Query("userIdx") userIdx: Long,    //유저 식별자
        @Query("isReplied") isReplied: Boolean, // 답변 달린 질문만 보기 (true) / 전체 질문 보기 (false)
    ):Call<TabResponse>

    @GET("/questions/my/concept/?")  // 검색 질문
    fun getTabConceptQuestions(
        @Header("X-ACCESS-TOKEN") jwt : String, // jwt
        @Query("userIdx") userIdx: Long,    //유저 식별자
        @Query("isReplied") isReplied: Boolean, // 답변 달린 질문만 보기 (true) / 전체 질문 보기 (false)
    ):Call<TabResponse>

    // 내가 단 답변 보기
    @GET("/replies/my/coding/?")  // 코딩 질문
    fun getTabCodingAnswers(
        @Header("X-ACCESS-TOKEN") jwt : String, // jwt
        @Query("userIdx") userIdx: Long,    //유저 식별자
        @Query("isAdopted") isAdopted: Boolean, // 채택된 질문만 보기 (true) / 전체 질문 보기 (false)
    ): Call<TabResponse>

    @GET("/replies/my/concept/?")  // 검색 질문
    fun getTabConceptAnswers(
        @Header("X-ACCESS-TOKEN") jwt : String, // jwt
        @Query("userIdx") userIdx: Long,    //유저 식별자
        @Query("isAdopted") isAdopted: Boolean, // 채택된 질문만 보기 (true) / 전체 질문 보기 (false)
    ): Call<TabResponse>
}