package com.example.mumulcom

import retrofit2.Call
import retrofit2.Response


// 질문을 스크랩할때 호출
class ScrapQuestionService {
    private lateinit var scrapQuestionView : ScrapQuestionView

    // 외부 접근
    fun setScrapQuestionView(scrapQuestionView : ScrapQuestionView){
        this.scrapQuestionView = scrapQuestionView
    }

    fun getScrapQuestion(X_ACCESS_TOKEN:String,scrapSend: LikeSend){
        val scrapForQuestionService = getRetrofit().create(QuestionRetrofitInterface::class.java)

        scrapQuestionView.onGetScrapLoading() // 로딩중

        scrapForQuestionService.getScrapQuestion(X_ACCESS_TOKEN ,scrapSend)
            .enqueue(object : retrofit2.Callback<ScrapQuestionResponse>{
                override fun onResponse(call: Call<ScrapQuestionResponse>, response: Response<ScrapQuestionResponse>) {

                    val resp = response.body()!!
                    when(resp.code){
                        1000-> scrapQuestionView.onGetScrapSuccess(resp.message,resp.result)
                        else-> scrapQuestionView.onGetScrapFailure(resp.code,resp.message)
                    }
                }

                override fun onFailure(call: Call<ScrapQuestionResponse>, t: Throwable) {
                    scrapQuestionView.onGetScrapFailure(400,"네트워크 오류가 발생했습니다.")
                }

            })
    }
}