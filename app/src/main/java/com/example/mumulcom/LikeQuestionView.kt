package com.example.mumulcom

interface LikeQuestionView {
    fun onGetLikeQuestionLoading()
    fun onGetLikeQuestionSuccess(result: Like)
    fun onGetLikeQuestionFailure(code:Int, message:String)
}