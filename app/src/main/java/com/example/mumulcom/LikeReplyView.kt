package com.example.mumulcom

interface LikeReplyView{
    fun onGetLikeReplyLoading()
    fun onGetLikeReplySuccess(result: Like)
    fun onGetLikeReplyFailure(code:Int, message:String)
}