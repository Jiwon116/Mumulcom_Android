package com.example.mumulcom


interface DetailConceptQuestionView {
    fun onGetDetailConceptQuestionsLoading()
    fun onGetDetailConceptQuestionsSuccess(result: ArrayList<DetailConceptQuestion>)
    fun onGetLikeCountConceptQuestion(result:ArrayList<DetailConceptQuestion>)
    fun onGetDetailConceptQuestionsFailure(code: Int, message: String)
}