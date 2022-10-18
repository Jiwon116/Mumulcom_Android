package com.example.mumulcom

import android.util.Log
import retrofit2.Call
import retrofit2.Response

class CategoryQuestionService {
    private lateinit var categoryQuestionView: CategoryQuestionView

    // 외부 접근
    fun setCategoryQuestionService(categoryQuestionView: CategoryQuestionView){
        this.categoryQuestionView = categoryQuestionView
    }

    // 서버에서 카테고리별 question 가져오는 부분
    fun getCategoryQuestions(type:Int=1,sort:Int=1,bigCategoryIdx:Int,smallCategoryIdx:Int?,isReplied:Boolean=false,lastQuestionIdx:Int,perPage:Int){
        val categoryQuestionService = getRetrofit().create(QuestionRetrofitInterface::class.java)

        categoryQuestionView.onGetQuestionsLoading()

        categoryQuestionService.getCategoryQuestions(type,sort,bigCategoryIdx ,smallCategoryIdx ,isReplied ,lastQuestionIdx ,perPage )
            .enqueue(object :retrofit2.Callback<CategoryQuestionResponse>{
                override fun onResponse(call: Call<CategoryQuestionResponse>, response: Response<CategoryQuestionResponse>) {


                    Log.d("apiTest",response.toString())

                    val resp = response.body()!!

                    when(resp.code){
                        1000-> {categoryQuestionView.onGetQuestionsSuccess(resp.result)
                            Log.d("CategoryQuestionService/API","성공")

                        }
                        else-> categoryQuestionView.onGetQuestionsFailure(resp.code,resp.message)
                    }

                }

                override fun onFailure(call: Call<CategoryQuestionResponse>, t: Throwable) {
                    Log.d("apiTest","실패")
                    categoryQuestionView.onGetQuestionsFailure(400,"네트워크 오류가 발생했습니다.")
                }

            })

    }

}// end of CategoryQuestionService