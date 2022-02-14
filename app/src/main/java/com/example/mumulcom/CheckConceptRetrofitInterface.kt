package com.example.mumulcom

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface CheckConceptRetrofitInterface {
    @POST("/questions/concept")
    fun checkConceptQuestion(
        @Body checkConcept: CheckConcept): Call<CheckConceptQuestionResponse>
}