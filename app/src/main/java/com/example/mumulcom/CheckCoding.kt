package com.example.mumulcom

import com.google.gson.annotations.SerializedName

data class CheckCoding(
    @SerializedName("userIdx") val userIdx:Long, // 댓글 작성자 고유 번호
    @SerializedName("currentError") val currentError:String, // 댓글 작성자 닉네임
    @SerializedName("myCodingSkill") val myCodingSkill:String?, // 댓글 작성자 닉네임
    @SerializedName("bigCategoryIdx") val bigCategoryIdx:Long, // 댓글 고유 번호
    @SerializedName("smallCategoryIdx") val smallCategoryIdx:Long?, // 댓글 고유 번호
    @SerializedName("title") val title:String, // 댓글 내용
    @SerializedName("codeQuestionUrl") val codeQuestionUrl:String?  // 댓글 첨부 이미지 (null 가능)
)