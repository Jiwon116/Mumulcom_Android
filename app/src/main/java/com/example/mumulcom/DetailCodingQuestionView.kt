package com.example.mumulcom


interface DetailCodingQuestionView {
    fun onGetDetailCodingQuestionsLoading()
    fun onGetDetailCodingQuestionsSuccess(result: ArrayList<DetailCodingQuestion>)
    fun onGetLikeCountCodingQuestion(result:ArrayList<DetailCodingQuestion>)
    fun onGetDetailCodingQuestionsFailure(code:Int, message:String)
}