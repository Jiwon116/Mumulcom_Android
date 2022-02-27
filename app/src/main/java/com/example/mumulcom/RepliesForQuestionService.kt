package com.example.mumulcom

import android.util.Log
import com.example.mumulcom.getRetrofit
import com.example.mumulcom.QuestionRetrofitInterface
import retrofit2.Call
import retrofit2.Response

// 질문에 대한 답변들을 가져오는 api
class RepliesForQuestionService {

    private lateinit var repliesForQuestionView: RepliesForQuestionView

    // 외부 접근
    fun setRepliesForQuestionView(repliesForQuestionView: RepliesForQuestionView){
        this.repliesForQuestionView = repliesForQuestionView
    }

    // 서버 연동 함수
    fun getRepliesForQuestion(questionIdx:Long,userIdx:Long){
        val repliesForQuestionService = getRetrofit().create(QuestionRetrofitInterface::class.java)

        repliesForQuestionView.onGetRepliesLoading() // api 호출전 로딩 처리

        repliesForQuestionService.getRepliesForQuestion(questionIdx,userIdx)
            .enqueue(object : retrofit2.Callback<RepliesForQuestionResponse>{
                override fun onResponse(call: Call<RepliesForQuestionResponse>, response: Response<RepliesForQuestionResponse>) {
                    // 호출 성공
                    val resp = response.body()!!
//                    Log.d("RepliesForQuestionService/API",response.body().toString())
                    when(resp.code){
                        1000->{
                            repliesForQuestionView.onGetRepliesSuccess(resp.result)
                            Log.d("RepliesForQuestionService/API","성공")
                        }else->
                        repliesForQuestionView.onGetRepliesFailure(resp.code,resp.message)
                    }
                }

                override fun onFailure(call: Call<RepliesForQuestionResponse>, t: Throwable) {
                    repliesForQuestionView.onGetRepliesFailure(400,"네트워크 오류가 발생했습니다.")
                }

            })
    }


}