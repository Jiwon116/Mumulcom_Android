package com.example.mumulcom

interface LoginView {
    fun onLoginLoading()
    fun onLoginSuccess(user: LoginUser)
    fun onLoginFailure(code:Int, message:String)
}