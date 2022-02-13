package com.example.mumulcom

interface LoginView {
    fun onLoginLoading()
    fun onLoginSuccess(profile: Profile)
    fun onLoginFailure(code:Int, message:String)
}