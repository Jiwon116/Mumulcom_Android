package com.example.mumulcom

interface ScrapCodingView {
    fun onGetScrapCodingLoading()
    fun onGetScrapCodingSuccess(result: ArrayList<Question>?)
    fun onGetScrapCodingFailure(code:Int, message:String)
}