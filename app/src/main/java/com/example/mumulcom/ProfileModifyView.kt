package com.example.mumulcom

interface ProfileModifyView {
    fun onSetProfileLoading()
    fun onSetProfileSuccess(profile: Profile)
    fun onSetProfileFailure(code:Int, message:String)
}