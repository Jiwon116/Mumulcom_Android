package com.example.mumulcom

import retrofit2.Call
import retrofit2.Response

class TabService {
    /************** 내가 한 질문 **************/
    private lateinit var tabCodingQuestionView: TabCodingQuestionView
    private lateinit var tabConceptQuestionView: TabConceptQuestionView

    // 외부 접근
    fun setTabCodingAnswerService(tabCodingAnswerView: TabCodingAnswerView){
        this.tabCodingAnswerView = tabCodingAnswerView
    }

    // 서버에서 탭별 coding answer 받아오기
    fun getTabCodingAnswers(jwt: String, userIdx: Long, isAdopted:Boolean){
        val tabService = getRetrofit().create(TabRetrofitInterface::class.java)

        tabCodingAnswerView.onGetCodingAnswersLoading()

        tabService.getTabCodingAnswers(jwt, userIdx, isAdopted)
            .enqueue(object :retrofit2.Callback<TabResponse>{
                override fun onResponse(call: Call<TabResponse>, response: Response<TabResponse>) {

                    val resp = response.body()!!

                    when(resp.code){
                        1000-> tabCodingAnswerView.onGetCodingAnswersSuccess(resp.result)
                        else-> tabCodingAnswerView.onGetCodingAnswersFailure(resp.code,resp.message)
                    }

                }

                override fun onFailure(call: Call<TabResponse>, t: Throwable) {
                    tabCodingAnswerView.onGetCodingAnswersFailure(400,"네트워크 오류가 발생했습니다.")
                }

            })
    }

    // 외부 접근
    fun setTabConceptAnswerService(tabConceptAnswerView: TabConceptAnswerView){
        this.tabConceptAnswerView = tabConceptAnswerView
    }

    // 서버에서 탭별 concept answer 받아오기
    fun getTabConceptAnswers(jwt: String, userIdx: Long, isAdopted:Boolean){
        val tabAnswerService = getRetrofit().create(TabRetrofitInterface::class.java)

        tabConceptAnswerView.onGetConceptAnswersLoading()

        tabAnswerService.getTabConceptAnswers(jwt, userIdx, isAdopted)
            .enqueue(object :retrofit2.Callback<TabResponse>{
                override fun onResponse(call: Call<TabResponse>, response: Response<TabResponse>) {

                    val resp = response.body()!!

                    when(resp.code){
                        1000-> tabConceptAnswerView.onGetConceptAnswersSuccess(resp.result)
                        else-> tabConceptAnswerView.onGetConceptAnswersFailure(resp.code,resp.message)
                    }

                }

                override fun onFailure(call: Call<TabResponse>, t: Throwable) {
                    tabConceptAnswerView.onGetConceptAnswersFailure(400,"네트워크 오류가 발생했습니다.")
                }

            })
    }



    /************** 내가 단 답변 ***************/
    private lateinit var tabCodingAnswerView: TabCodingAnswerView
    private lateinit var tabConceptAnswerView: TabConceptAnswerView

    // 외부 접근
    fun setTabCodingQuestionService(tabCodingQuestionView: TabCodingQuestionView){
        this.tabCodingQuestionView = tabCodingQuestionView
    }

    // 서버에서 탭별 coding question 가져오는 부분
    fun getTabCodingQuestions(jwt: String, userIdx: Long, isReplied:Boolean){
        val tabService = getRetrofit().create(TabRetrofitInterface::class.java)

        tabCodingQuestionView.onGetCodingQuestionsLoading()

        tabService.getTabCodingQuestions(jwt, userIdx, isReplied)
            .enqueue(object :retrofit2.Callback<TabResponse>{
                override fun onResponse(call: Call<TabResponse>, response: Response<TabResponse>) {

                    val resp = response.body()!!

                    when(resp.code){
                        1000-> tabCodingQuestionView.onGetCodingQuestionsSuccess(resp.result)
                        else-> tabCodingQuestionView.onGetCodingQuestionsFailure(resp.code,resp.message)
                    }

                }

                override fun onFailure(call: Call<TabResponse>, t: Throwable) {
                    tabCodingQuestionView.onGetCodingQuestionsFailure(400,"네트워크 오류가 발생했습니다.")
                }

            })
    }

    // 외부 접근
    fun setTabConceptQuestionService(tabConceptQuestionView: TabConceptQuestionView){
        this.tabConceptQuestionView = tabConceptQuestionView
    }

    // 서버에서 탭별 concept question 가져오는 부분
    fun getTabConceptQuestions(jwt: String, userIdx: Long, isReplied:Boolean){
        val tabQuestionService = getRetrofit().create(TabRetrofitInterface::class.java)

        tabConceptQuestionView.onGetConceptQuestionsLoading()

        tabQuestionService.getTabConceptQuestions(jwt, userIdx, isReplied)
            .enqueue(object :retrofit2.Callback<TabResponse>{
                override fun onResponse(call: Call<TabResponse>, response: Response<TabResponse>) {

                    val resp = response.body()!!

                    when(resp.code){
                        1000-> tabConceptQuestionView.onGetConceptQuestionsSuccess(resp.result)
                        else-> tabConceptQuestionView.onGetConceptQuestionsFailure(resp.code,resp.message)
                    }

                }

                override fun onFailure(call: Call<TabResponse>, t: Throwable) {
                    tabConceptQuestionView.onGetConceptQuestionsFailure(400,"네트워크 오류가 발생했습니다.")
                }

            })
    }

}