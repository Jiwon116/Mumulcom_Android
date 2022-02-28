package com.example.mumulcom

interface SignUpView {
    fun onSignUpLoading()
    fun onSignUpSuccess(user: User)
    fun onSignUpFailure(code: Int, message: String)
}