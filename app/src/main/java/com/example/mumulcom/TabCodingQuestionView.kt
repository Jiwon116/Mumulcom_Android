package com.example.mumulcom

interface TabCodingQuestionView {
    fun onGetCodingQuestionsLoading()
    fun onGetCodingQuestionsSuccess(result: ArrayList<Question>?)
    fun onGetCodingQuestionsFailure(code:Int, message:String)
}