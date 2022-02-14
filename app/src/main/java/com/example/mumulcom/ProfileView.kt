package com.example.mumulcom

interface ProfileView {
    fun onGetProfileLoading()
    fun onGetProfileSuccess(profile: Profile)
    fun onGetProfileFailure(code:Int, message:String)
}