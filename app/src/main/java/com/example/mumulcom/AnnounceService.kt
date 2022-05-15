package com.example.mumulcom

import retrofit2.Call
import retrofit2.Response

class AnnounceService {
    private lateinit var announceView: AnnounceView

    fun setAnnounceService(announceView: AnnounceActivity){
        this.announceView = announceView
    }

    fun getAnnounces(){
        val announceService = getRetrofit().create(AnnounceRetrofitInterface::class.java)

        announceView.onGetAnnounceLoading()

        announceService.getAnnounces()
            .enqueue(object :retrofit2.Callback<AnnounceResponse>{
                override fun onResponse(call: Call<AnnounceResponse>, response: Response<AnnounceResponse>) {

                    val resp = response.body()!!

                    when(resp.code){
                        1000-> announceView.onGetAnnounceSuccess(resp.result)
                        else-> announceView.onGetAnnounceFailure(resp.code,resp.message)
                    }

                }

                override fun onFailure(call: Call<AnnounceResponse>, t: Throwable) {
                    announceView.onGetAnnounceFailure(400,"네트워크 오류가 발생했습니다.")
                }

            })
    }

}