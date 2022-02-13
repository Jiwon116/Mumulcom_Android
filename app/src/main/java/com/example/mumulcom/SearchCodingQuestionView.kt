package com.example.mumulcom

interface SearchCodingQuestionView {
    fun onGetCodingQuestionsLoading()
    fun onGetCodingQuestionsSuccess(result: ArrayList<CodingQuestion>?)
    fun onGetCodingQuestionsFailure(code:Int, message:String)
}