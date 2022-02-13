package com.example.mumulcom

import android.webkit.ConsoleMessage

interface CheckConceptQuestionView {
    fun onCheckConceptQuestionLoading()
    fun onCheckConceptQuestionSuccess(message: String)
    fun onCheckConceptQuestionFailure(code:Int, message: String)
}