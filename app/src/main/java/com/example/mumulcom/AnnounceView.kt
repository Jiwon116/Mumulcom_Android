package com.example.mumulcom

interface AnnounceView {
    fun onGetAnnounceLoading()
    fun onGetAnnounceSuccess(result: ArrayList<Announce>?)
    fun onGetAnnounceFailure(code:Int, message:String)
}