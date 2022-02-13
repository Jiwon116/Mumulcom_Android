package com.example.mumulcom

interface TabConceptQuestionView {
    fun onGetConceptQuestionsLoading()
    fun onGetConceptQuestionsSuccess(result: ArrayList<Question>?)
    fun onGetConceptQuestionsFailure(code:Int, message:String)
}