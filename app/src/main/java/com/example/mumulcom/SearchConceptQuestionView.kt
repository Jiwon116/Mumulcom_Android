package com.example.mumulcom

interface SearchConceptQuestionView {
    fun onGetConceptQuestionsLoading()
    fun onGetConceptQuestionsSuccess(result: ArrayList<ConceptQuestion>?)
    fun onGetConceptQuestionsFailure(code:Int, message:String)
}