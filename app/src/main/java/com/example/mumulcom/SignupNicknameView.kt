package com.example.mumulcom

interface SignupNicknameView {
    fun getNicknameCheckLoading()
    fun getNicknameCheckSuccess(result: Boolean)
    fun getNicknameCheckFailure(code:Int, message: String)
}