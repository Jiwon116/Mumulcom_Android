package com.example.mumulcom

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

// 검색 결과를 서버에서 받아옴
interface SearchRetrofitInterface {
    @GET("/questions/search/coding") // 코딩 질문 검색
    fun getCodingQuestions(
        @Query("keyword") type: String?,   // 검색어
    ): Call<CodingQuestionResponse>


    @GET("/questions/search/concept") // 개념 질문 검색
    fun getConceptQuestions(
        @Query("keyword") type: String?,   // 검색어
    ): Call<ConceptQuestionResponse>
}