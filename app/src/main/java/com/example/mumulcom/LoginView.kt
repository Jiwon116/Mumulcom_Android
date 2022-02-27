package com.example.mumulcom

interface LoginView {
    fun onLoginLoading()
    fun onLoginSuccess(user: User)
    fun onLoginFailure(code:Int, message:String)
}