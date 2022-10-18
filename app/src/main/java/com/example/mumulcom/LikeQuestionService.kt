package com.example.mumulcom

import android.util.Log
import retrofit2.Call
import retrofit2.Response

class LikeQuestionService {
    private lateinit var likeQuestionView: LikeQuestionView

    fun setLikeQuestionView(likeQuestionView: LikeQuestionView){
        this.likeQuestionView = likeQuestionView
    }


    fun getLikeQuestion(X_ACCESS_TOKEN:String,likeSend: LikeSend){

        val likeQuestionService = getRetrofit().create(QuestionRetrofitInterface::class.java)

        likeQuestionView.onGetLikeQuestionLoading()
        Log.d("qqq","like 로딩 호출")


        likeQuestionService.getLikeQuestion(X_ACCESS_TOKEN,likeSend)
            .enqueue(object : retrofit2.Callback<LikeQuestionResponse>{
                override fun onResponse(call: Call<LikeQuestionResponse>, response: Response<LikeQuestionResponse>) {

                    Log.d("qqq","getLikeQuestion 호출")

                    val resp = response.body()!!
                    Log.d("qqq",resp.result.toString())

                    when(resp.code){

                        1000-> likeQuestionView.onGetLikeQuestionSuccess(resp.result)
                        else-> likeQuestionView.onGetLikeQuestionFailure(resp.code,resp.message)

                    }


                }

                override fun onFailure(call: Call<LikeQuestionResponse>, t: Throwable) {
                    likeQuestionView.onGetLikeQuestionFailure(400 ,"네트워크 오류가 발생했습니다.")
                }
            })

    }
}