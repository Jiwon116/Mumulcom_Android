package com.example.mumulcom

import android.util.Log
import retrofit2.Call
import retrofit2.Response

class SearchService {
    private lateinit var searchCodingQuestionView: SearchCodingQuestionView
    private lateinit var searchConceptQuestionView: SearchConceptQuestionView

    fun setSearchCodingQuestionView(searchCodingQuestionView: SearchCodingQuestionView) {
        this.searchCodingQuestionView = searchCodingQuestionView
    }

    fun setSearchConceptQuestionView(searchConceptQuestionView: SearchConceptQuestionView) {
        this.searchConceptQuestionView = searchConceptQuestionView
    }

    // 서버에서 코딩 질문 중 keyword와 맞는 데이터를 가져오는 부분
    fun getCodingQuestions(keyword:String?){
        val searchCodingQuestionService = getRetrofit().create(SearchRetrofitInterface::class.java)

        searchCodingQuestionView.onGetCodingQuestionsLoading()

        searchCodingQuestionService.getCodingQuestions(keyword)
            .enqueue(object :retrofit2.Callback<CodingQuestionResponse>{
                override fun onResponse(call: Call<CodingQuestionResponse>, response: Response<CodingQuestionResponse>) {

                    val resp = response.body()!!

                    when(resp.code){
                        1000-> {searchCodingQuestionView.onGetCodingQuestionsSuccess(resp.result)
                            Log.d("SearchCodingQuestionService/API","성공")

                        }
                        else-> searchCodingQuestionView.onGetCodingQuestionsFailure(resp.code,resp.message)
                    }

                }

                override fun onFailure(call: Call<CodingQuestionResponse>, t: Throwable) {
                    searchCodingQuestionView.onGetCodingQuestionsFailure(400,"네트워크 오류가 발생했습니다.")
                }
            })
    }

    // 서버에서 개념 질문 중 keyword와 맞는 데이터를 가져오는 부분
    fun getConceptQuestions(keyword:String?){
        val searchConceptQuestionService = getRetrofit().create(SearchRetrofitInterface::class.java)

        searchConceptQuestionView.onGetConceptQuestionsLoading()

        searchConceptQuestionService.getConceptQuestions(keyword)
            .enqueue(object :retrofit2.Callback<ConceptQuestionResponse>{
                override fun onResponse(call: Call<ConceptQuestionResponse>, response: Response<ConceptQuestionResponse>) {

                    val resp = response.body()!!

                    when(resp.code){
                        1000-> {searchConceptQuestionView.onGetConceptQuestionsSuccess(resp.result)
                            Log.d("SearchConceptQuestionService/API","성공")

                        }
                        else-> searchConceptQuestionView.onGetConceptQuestionsFailure(resp.code,resp.message)
                    }

                }

                override fun onFailure(call: Call<ConceptQuestionResponse>, t: Throwable) {
                    searchConceptQuestionView.onGetConceptQuestionsFailure(400,"네트워크 오류가 발생했습니다.")
                }
            })
    }
}