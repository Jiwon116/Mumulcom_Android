package com.example.mumulcom

interface CheckCodingQuestionView {
    fun onCheckCodingQuestionLoading()
    fun onCheckCodingQuestionSuccess(result: String)
    fun onCheckCodingQuestionFailure(code:Int, message: String)
}