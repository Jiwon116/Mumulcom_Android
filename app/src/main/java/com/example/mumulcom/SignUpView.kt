package com.example.mumulcom

interface SignUpView {
    fun onSignUpLoading()
    fun onSignUpSuccess()
    fun onSignUpFailure(code: Int, message: String)
}