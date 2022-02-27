package com.example.mumulcom


interface AdoptReplyView {
    fun onGetAdoptReplyLoading()
    fun onGetAdoptReplySuccess(result: Adopt)
    fun onGetAdoptReplyFailure(code:Int, message:String)
}