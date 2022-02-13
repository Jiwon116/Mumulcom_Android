package com.example.mumulcom

interface CheckCodingQuestionView {
    fun onCheckCodingQuestionLoading()
    fun onCheckCodingQuestionFailure(code:Int, message: String)
    fun onCheckCodingQuestionSuccess(result: String)
}