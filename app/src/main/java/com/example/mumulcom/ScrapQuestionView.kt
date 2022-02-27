package com.example.mumulcom

// 질문 스크랩 할때 호출
interface ScrapQuestionView {
    fun onGetScrapLoading()
    fun onGetScrapSuccess(message : String,result:String)
    fun onGetScrapFailure(code:Int , message:String)
}