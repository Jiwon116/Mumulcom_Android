package com.example.mumulcom

interface ScrapConceptView {
    fun onGetScrapConceptLoading()
    fun onGetScrapConceptSuccess(result: ArrayList<Question>?)
    fun onGetScrapConceptFailure(code:Int, message:String)
}