package com.example.mumulcom

interface ReportUserView {
    fun onReportUserLoading()
    fun onReportUserSuccess(result : ReportUserId)
    fun onReportUserFailure(code:Int , message:String)
}