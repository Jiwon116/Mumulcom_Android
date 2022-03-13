package com.example.mumulcom

import android.util.Log

import retrofit2.Call
import retrofit2.Response

class DetailCodingQuestionService {

    private lateinit var detailCodingQuestionView: DetailCodingQuestionView // 선언

    // 외부 접근
    fun setDetailCodingQuestionService(detailCodingQuestionView: DetailCodingQuestionView){
        this.detailCodingQuestionView = detailCodingQuestionView
    }

    // 서버에서 해당 (개념) 질문에 대한 상세 내용 받아오는 함수
    fun getDetailConceptQuestion(questionIdx:Long,userIdx:Long){
        val detailConceptQuestionService = getRetrofit().create(QuestionRetrofitInterface::class.java)

        detailCodingQuestionView.onGetDetailCodingQuestionsLoading() // 호출전

        detailConceptQuestionService.getDetailCodingQuestion(questionIdx,userIdx)
            .enqueue(object : retrofit2.Callback<DetailCodingQuestionResponse>{
                override fun onResponse(call: Call<DetailCodingQuestionResponse>, response: Response<DetailCodingQuestionResponse>) {
                    // 호출 성공
                    val resp = response.body()!!
//                    Log.d("test",resp.toString())
                    when(resp.code){
                        1000->{
                            detailCodingQuestionView.onGetDetailCodingQuestionsSuccess(resp.result)
                            Log.d("DetailConceptQuestionService/API","성공")
                        }else->
                        detailCodingQuestionView.onGetDetailCodingQuestionsFailure(resp.code,resp.message)
                    }

                }

                override fun onFailure(call: Call<DetailCodingQuestionResponse>, t: Throwable) {
                    // 호출 실패
                    detailCodingQuestionView.onGetDetailCodingQuestionsFailure(400,"네트워크 오류가 발생했습니다.")

                }

            })
    }



    fun getLikeCountCodingQuestion(questionIdx:Long,userIdx:Long){
        val detailConceptQuestionService = getRetrofit().create(QuestionRetrofitInterface::class.java)

        detailConceptQuestionService.getDetailCodingQuestion(questionIdx,userIdx)
            .enqueue(object : retrofit2.Callback<DetailCodingQuestionResponse>{
                override fun onResponse(call: Call<DetailCodingQuestionResponse>, response: Response<DetailCodingQuestionResponse>) {
                    // 호출 성공
                    val resp = response.body()!!
                    when(resp.code){
                        1000->{
                            detailCodingQuestionView.onGetLikeCountCodingQuestion(resp.result)
                        }else->
                        detailCodingQuestionView.onGetDetailCodingQuestionsFailure(resp.code,resp.message)
                    }

                }

                override fun onFailure(call: Call<DetailCodingQuestionResponse>, t: Throwable) {
                    // 호출 실패
                    detailCodingQuestionView.onGetDetailCodingQuestionsFailure(400,"네트워크 오류가 발생했습니다.")

                }

            })

    }

}