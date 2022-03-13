package com.example.mumulcom

import android.util.Log
import retrofit2.Call
import retrofit2.Response

class DetailConceptQuestionService {
    private lateinit var detailConceptQuestionView: DetailConceptQuestionView // 선언

    // 외부 접근
    fun setDetailConceptQuestionService(detailConceptQuestionView: DetailConceptQuestionView){
        this.detailConceptQuestionView = detailConceptQuestionView
    }

    // 서버에서 해당 (개념) 질문에 대한 상세 내용 받아오는 함수
    fun getDetailConceptQuestion(questionIdx:Long,userIdx:Long){
        val detailConceptQuestionService = getRetrofit().create(QuestionRetrofitInterface::class.java)

        detailConceptQuestionView.onGetDetailConceptQuestionsLoading() // 호출전

        detailConceptQuestionService.getDetailConceptQuestion(questionIdx,userIdx)
            .enqueue(object : retrofit2.Callback<DetailConceptQuestionResponse>{
                override fun onResponse(call: Call<DetailConceptQuestionResponse>, response: Response<DetailConceptQuestionResponse>) {
                    // 호출 성공
                    Log.d("DetailConceptQuestionService/API","호출")
                    val resp = response.body()!!
                    when(resp.code){
                        1000->{
                            detailConceptQuestionView.onGetDetailConceptQuestionsSuccess(resp.result!!)
                            Log.d("DetailConceptQuestionService/API","성공")
                        }else->
                        detailConceptQuestionView.onGetDetailConceptQuestionsFailure(resp.code,resp.message)
                    }

                }

                override fun onFailure(call: Call<DetailConceptQuestionResponse>, t: Throwable) {
                    // 호출 실패
                    detailConceptQuestionView.onGetDetailConceptQuestionsFailure(400,"네트워크 오류가 발생했습니다.")

                }

            })
    }


    fun getLikeCountConceptQuestion(questionIdx:Long,userIdx:Long){
        val detailConceptQuestionService = getRetrofit().create(QuestionRetrofitInterface::class.java)

        detailConceptQuestionView.onGetDetailConceptQuestionsLoading() // 호출전

        detailConceptQuestionService.getDetailConceptQuestion(questionIdx,userIdx)
            .enqueue(object : retrofit2.Callback<DetailConceptQuestionResponse>{
                override fun onResponse(call: Call<DetailConceptQuestionResponse>, response: Response<DetailConceptQuestionResponse>) {
                    // 호출 성공
                    Log.d("DetailConceptQuestionService/API","호출")
                    val resp = response.body()!!
                    when(resp.code){
                        1000->{
                            detailConceptQuestionView.onGetLikeCountConceptQuestion(resp.result!!)
                            Log.d("DetailConceptQuestionService/API","성공")
                        }else->
                        detailConceptQuestionView.onGetDetailConceptQuestionsFailure(resp.code,resp.message)
                    }

                }

                override fun onFailure(call: Call<DetailConceptQuestionResponse>, t: Throwable) {
                    // 호출 실패
                    detailConceptQuestionView.onGetDetailConceptQuestionsFailure(400,"네트워크 오류가 발생했습니다.")

                }

            })
    }

}