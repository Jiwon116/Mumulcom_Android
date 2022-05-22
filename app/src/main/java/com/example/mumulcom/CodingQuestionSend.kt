package com.example.mumulcom

import com.google.gson.annotations.SerializedName

data class CodingQuestionSend(
    @SerializedName("userIdx") val userIdx: Long,  //
    @SerializedName("currentError") val currentError: String, //
    @SerializedName("myCodingSkill") val myCodingSkill: String?, //
    @SerializedName("bigCategoryIdx") val bigCategoryIdx: Long, //
    @SerializedName("smallCategoryIdx") val smallCategoryIdx: Long?, //
    @SerializedName("title") val title: String, //
    @SerializedName("codeQuestionUrl") val codeQuestionUrl: String?, //


)
