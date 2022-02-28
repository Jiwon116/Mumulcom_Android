package com.example.mumulcom

interface WithdrawView {
    fun onWithdrawLoading()
    fun onWithdrawSuccess(profile: Profile)
    fun onWithdrawFailure(code:Int, message:String)
}