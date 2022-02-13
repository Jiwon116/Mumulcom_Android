package com.example.mumulcom

interface AlarmView {
    fun onGetAlarmLoading()
    fun onGetAlarmSuccess(result: ArrayList<Alarm>?)
    fun onGetAlarmFailure(code:Int, message:String)
}