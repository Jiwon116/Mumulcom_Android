package com.example.mumulcom

interface AnswerView {
    fun onAnswerLoading()
    fun onAnswerFailure(code:Int, message: String)
    fun onAnswerSuccess(result: Replies?)
}