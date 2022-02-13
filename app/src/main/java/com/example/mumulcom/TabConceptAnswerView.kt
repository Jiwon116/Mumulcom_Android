package com.example.mumulcom

interface TabConceptAnswerView {
    fun onGetConceptAnswersLoading()
    fun onGetConceptAnswersSuccess(result: ArrayList<Question>?)
    fun onGetConceptAnswersFailure(code:Int, message:String)
}