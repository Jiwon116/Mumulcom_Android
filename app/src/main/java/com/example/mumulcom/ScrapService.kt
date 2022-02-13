package com.example.mumulcom

import android.util.Log
import retrofit2.Call
import retrofit2.Response

class ScrapService {
    private lateinit var scrapCodingView: ScrapCodingView
    private lateinit var scrapConceptView: ScrapConceptView

    // 외부 접근
    fun setScrapCodingService(scrapCodingView: ScrapCodingView){
        this.scrapCodingView = scrapCodingView
    }

    // 서버에서 탭별 coding question 가져오는 부분
    fun getScrapCodingQuestions(jwt: String, userIdx: Long, isReplied: Boolean, bigCategory: String?, smallCategory: String?){
        val scrapService = getRetrofit().create(ScrapRetrofitInterface::class.java)

        scrapCodingView.onGetScrapCodingLoading()

        scrapService.getScrapCodingQuestions(jwt, userIdx, isReplied, bigCategory, smallCategory)
            .enqueue(object :retrofit2.Callback<ScrapResponse>{
                override fun onResponse(call: Call<ScrapResponse>, response: Response<ScrapResponse>) {

                    val resp = response.body()!!

                    when(resp.code){
                        1000-> scrapCodingView.onGetScrapCodingSuccess(resp.result)
                        else-> scrapCodingView.onGetScrapCodingFailure(resp.code,resp.message)
                    }

                }

                override fun onFailure(call: Call<ScrapResponse>, t: Throwable) {
                    scrapCodingView.onGetScrapCodingFailure(400,"네트워크 오류가 발생했습니다.")
                }

            })
    }

    // 외부 접근
    fun setScrapConceptService(scrapConceptView: ScrapConceptView){
        this.scrapConceptView = scrapConceptView
    }

    // 서버에서 탭별 concept question 가져오는 부분
    fun getScrapConceptQuestions(jwt: String, userIdx: Long, isReplied: Boolean, bigCategory: String?, smallCategory: String?){
        val scrapService = getRetrofit().create(ScrapRetrofitInterface::class.java)

        scrapConceptView.onGetScrapConceptLoading()

        scrapService.getScrapConceptQuestions(jwt, userIdx, isReplied, bigCategory, smallCategory)
            .enqueue(object :retrofit2.Callback<ScrapResponse>{
                override fun onResponse(call: Call<ScrapResponse>, response: Response<ScrapResponse>) {

                    val resp = response.body()!!

                    when(resp.code){
                        1000-> scrapConceptView.onGetScrapConceptSuccess(resp.result)
                        else-> scrapConceptView.onGetScrapConceptFailure(resp.code,resp.message)
                    }

                }

                override fun onFailure(call: Call<ScrapResponse>, t: Throwable) {
                    scrapConceptView.onGetScrapConceptFailure(400,"네트워크 오류가 발생했습니다.")
                }

            })
    }

}