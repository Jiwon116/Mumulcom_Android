package com.example.mumulcom

interface AnnounceDetailView {
    fun onGetAnnounceDetailLoading()
    fun onGetAnnounceDetailSuccess(result: AnnounceDetail?)
    fun onGetAnnounceDetailFailure(code:Int, message:String)
}