package com.example.mumulcom

import retrofit2.Call
import retrofit2.http.*

// 질문정보를 서버에서 받아올때 이용
interface QuestionRetrofitInterface {
    @GET("/questions/latest/{userIdx}") // 최근 질문 조회 (HomeFragment)
    fun getQuestions(
        @Path("userIdx") userIdx : Long,
        @Header("X-ACCESS-TOKEN") X_ACCESS_TOKEN : String
    ): Call<QuestionResponse>

    @GET("/questions?")  // 카테고리별 질문 조회 (QuestionBoardActivity)
    fun getCategoryQuestions(
        @Query("type") type:Int,   // 질문 유형 (코딩 질문: 1 / 개념 질문 : 2)
        @Query("sort") sort:Int, // 정렬 기준 (최신순 : 1/ 핫한순 : 2)
        @Query("bigCategoryIdx") bigCategoryIdx:Int, // (ex.1) -> 필수
        @Query("smallCategoryIdx") smallCategoryIdx:Int?, // (ex.1) -> 필수 아님
        @Query("isReplied") isReplied:Boolean, // (답변 달린 질문만 보기 (true) / 전체 질문 보기 (false)
        @Query("lastQuestionIdx") lastQuestionIdx:Int, // (ex. 1)  마지막으로 조회한 질문글 순서 번호
        @Query("perPage") perPage:Int, // 하나의 페이지당 조회할 질문 글 갯수
    ):Call<CategoryQuestionResponse>

    @GET("/questions/coding/{questionIdx}/{userIdx}") // 코딩 질문 상세 조회 (api 10)
    fun getDetailCodingQuestion(
        @Path("questionIdx") questionIdx : Long,
        @Path("userIdx") userIdx : Long

    ):Call<DetailCodingQuestionResponse>

    @GET("/questions/concept/{questionIdx}/{userIdx}") // 개념 질문 상세 조회 (api 11)
    fun getDetailConceptQuestion(
        @Path("questionIdx") questionIdx : Long,
        @Path("userIdx") userIdx : Long

    ):Call<DetailConceptQuestionResponse>

    @GET("/replies/{questionIdx}/{userIdx}") // 질문에 대한 답변들 조회 (api 22)
    fun getRepliesForQuestion(
        @Path("questionIdx") questionIdx : Long,
        @Path("userIdx") userIdx : Long
    ):Call<RepliesForQuestionResponse>

    @POST("/likes/questions/creation") // 해당 질문을 좋아요 했을때 호출 (api 27)
    fun getLikeQuestion(
        @Header("X-ACCESS-TOKEN") X_ACCESS_TOKEN : String,
        @Body questionIdx : Long,  // 질문 고유 번호 (좋아요를 누른 질문 번호)
        @Body userIdx : Long   // 유저 고유 번호 (좋아요를 한 유저 번호)
    ):Call<LikeQuestionResponse>

}