package com.example.mumulcom

interface CategoryQuestionView {
    fun onGetQuestionsLoading()
    fun onGetQuestionsSuccess(result: ArrayList<Question>?)
    fun onGetQuestionsFailure(code:Int, message:String)
}