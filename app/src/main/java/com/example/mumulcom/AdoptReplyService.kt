package com.example.mumulcom

import com.example.mumulcom.getRetrofit
import com.example.mumulcom.QuestionRetrofitInterface
import retrofit2.Call
import retrofit2.Response


class AdoptReplyService {
    private lateinit var adoptReplyView: AdoptReplyView

    fun setAdoptReplyView(adoptReplyView: AdoptReplyView){
        this.adoptReplyView = adoptReplyView
    }

    fun getAdoptReply(jwt:String,userIdx:Long,replyIdx:Long){
        val adoptReplyService = getRetrofit().create(QuestionRetrofitInterface::class.java)

        adoptReplyView.onGetAdoptReplyLoading()

        adoptReplyService.getAdoptReply(jwt,userIdx,replyIdx)
            .enqueue(object :retrofit2.Callback<AdoptReplyResponse>{
                override fun onResponse(
                    call: Call<AdoptReplyResponse>,
                    response: Response<AdoptReplyResponse>
                ) {
                    val resp = response.body()!!
                    when(resp.code){
                        1000->adoptReplyView.onGetAdoptReplySuccess(resp.result)
                        else->adoptReplyView.onGetAdoptReplyFailure(resp.code,resp.message)
                    }
                }

                override fun onFailure(call: Call<AdoptReplyResponse>, t: Throwable) {
                    adoptReplyView.onGetAdoptReplyFailure(400,"네트워크 오류")
                }

            })
    }


}