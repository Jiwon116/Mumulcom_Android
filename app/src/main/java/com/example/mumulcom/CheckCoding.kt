package com.example.mumulcom

import com.google.gson.annotations.SerializedName

// 코딩 질문하기
data class CheckCoding(
    //@SerializedName("images") val images: List<String>?,//이미지 우선 List<multiplefile>
    @SerializedName("userIdx") var userIdx: Long, //유저
    @SerializedName("title") val title: String,// 질문 제목
    @SerializedName("currentError") var currentError: String, // 현재 막힌 부분
    @SerializedName("myCodingSkill") var myCodingSkill: String?, // 현재 나의 코딩 실력
    @SerializedName("codeQuestionUrl") var codeQuestionUrl:String?, //참고코드
    @SerializedName("bigCategoryIdx") val bigCategoryIdx: Long, // 상위 카테고리 (앱,웹,서버,기타)
    @SerializedName("smallCategoryIdx") val smallCategoryIdx: Long?, // 하위 카테고리 (안드로이드,ios,html,css,.....)
////이미지는 보류
)