package com.example.mumulcom

import android.util.Log
import retrofit2.Call
import retrofit2.Response



// 이 클래스를 사용해서 질문 리스트를 받아올수 있음.
class QuestionService {
    private lateinit var recentQuestionView: RecentQuestionView

    // 외부 접근
    fun setRecentQuestionView(recentQuestionView: RecentQuestionView){
        this.recentQuestionView = recentQuestionView
    }

    fun getQuestions(userIdx:Long, X_ACCESS_TOKEN:String) {
        val questionService = getRetrofit().create(QuestionRetrofitInterface::class.java)
        // val callResponse: Call<QuestionResponse> = questionService.getQuestions(userIdx)

        recentQuestionView.onGetQuestionsLoading()

        questionService.getQuestions(userIdx,X_ACCESS_TOKEN ).enqueue(object : retrofit2.Callback<QuestionResponse>{
            override fun onResponse(call: Call<QuestionResponse>, response: Response<QuestionResponse>) {

                val resp = response.body()!!

                when(resp.code){
                    1000->{ recentQuestionView.onGetQuestionsSuccess(resp.result)
                        Log.d("HomeFragment/API","성공")}
//                    1000-> recentQuestionView.onGetQuestionsSuccess(null)
                    else -> recentQuestionView.onGetQuestionsFailure(resp.code,resp.message)

                }

            }

            override fun onFailure(call: Call<QuestionResponse>, t: Throwable) { // 네트워크 응답 실패시
                recentQuestionView.onGetQuestionsFailure(400,"네트워크 오류가 발생했습니다.")

            }

        })

    }


}// end of Class