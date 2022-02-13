package com.example.mumulcom

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CheckConceptQuestionService{
    private lateinit var checkConceptQuestionView: CheckConceptQuestionView

    fun setcheckconceptquestionView(checkConceptQuestionView: CheckConceptQuestionView){
        this.checkConceptQuestionView=checkConceptQuestionView
    }

    fun checkConceptQuestion(checkConcept: CheckConcept){
        val checkConceptQuestionService= getRetrofit().create(CheckConceptRetrofitInterface::class.java)

        checkConceptQuestionView.onCheckConceptQuestionLoading()

        checkConceptQuestionService.checkConceptQuestion(checkConcept).enqueue(object : Callback<CheckConceptQuestionResponse>{
            override fun onResponse(
                call: Call<CheckConceptQuestionResponse>,
                response: Response<CheckConceptQuestionResponse>
            ) {
                Log.d("CHECKCONCEPT/API-RESPONSE", response.toString())

                if (response.isSuccessful&&response.code()==200){
                    val resp=response.body()!!
                    Log.d("CHECKCONCEPT/API", resp.toString())

                    when(resp.code){
                        1000->checkConceptQuestionView.onCheckConceptQuestionSuccess(resp.result)
                        else->checkConceptQuestionView.onCheckConceptQuestionFailure(resp.code, resp.message)
                    }
                }
            }

            override fun onFailure(call: Call<CheckConceptQuestionResponse>, t: Throwable) {
                Log.d("CHECKCONCEPT/API-ERROR",t.message.toString())

                checkConceptQuestionView.onCheckConceptQuestionFailure(400, "네트워크 오류가 발생했습니다.")
            }
        })
        Log.d("CHECKCONCEPT/API","Hello")
    }
}