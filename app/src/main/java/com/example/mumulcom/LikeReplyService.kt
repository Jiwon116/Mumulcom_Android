package com.example.mumulcom

import retrofit2.Call
import retrofit2.Response

// 답변을 좋아요 했을때
class LikeReplyService {

    private lateinit var likeReplyView: LikeReplyView

    fun setLikeReplyView(likeReplyView: LikeReplyView){
        this.likeReplyView = likeReplyView
    }

    fun getLikeReply(X_ACCESS_TOKEN:String,likeReplySend: LikeReplySend){
        val likeReplyService = getRetrofit().create(QuestionRetrofitInterface::class.java)

        likeReplyView.onGetLikeReplyLoading()

        likeReplyService.getLikeReply(X_ACCESS_TOKEN,likeReplySend)
            .enqueue(object :retrofit2.Callback<LikeReplyResponse>{
                override fun onResponse(call: Call<LikeReplyResponse>, response: Response<LikeReplyResponse>) {

                    val resp = response.body()!!

                    when(resp.code){
                        1000-> likeReplyView.onGetLikeReplySuccess(resp.result)
                        else -> likeReplyView.onGetLikeReplyFailure(resp.code,resp.message)
                    }
                }

                override fun onFailure(call: Call<LikeReplyResponse>, t: Throwable) {
                    likeReplyView.onGetLikeReplyFailure(400,"네트워크 오류가 발생했습니다.")
                }

            })

    }
}