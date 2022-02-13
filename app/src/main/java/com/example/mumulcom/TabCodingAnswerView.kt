package com.example.mumulcom

interface TabCodingAnswerView {
    fun onGetCodingAnswersLoading()
    fun onGetCodingAnswersSuccess(result: ArrayList<Question>?)
    fun onGetCodingAnswersFailure(code:Int, message:String)
}