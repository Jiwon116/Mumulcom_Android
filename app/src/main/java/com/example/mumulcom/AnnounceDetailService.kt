package com.example.mumulcom

import retrofit2.Call
import retrofit2.Response

class AnnounceDetailService {
    private lateinit var announceDetailView: AnnounceDetailView

    fun setAnnounceDetailService(announceDetailView: AnnounceDetailActivity){
        this.announceDetailView = announceDetailView
    }

    fun getAnnouncesDetail(announceIdx:Long){
        val announceDetailService = getRetrofit().create(AnnounceDetailRetrofitInterface::class.java)

        announceDetailView.onGetAnnounceDetailLoading()

        announceDetailService.getAnnouncesDetail(announceIdx)
            .enqueue(object :retrofit2.Callback<AnnounceDetailResponse>{
                override fun onResponse(call: Call<AnnounceDetailResponse>, response: Response<AnnounceDetailResponse>) {

                    val resp = response.body()!!

                    when(resp.code){
                        1000-> announceDetailView.onGetAnnounceDetailSuccess(resp.result)
                        else-> announceDetailView.onGetAnnounceDetailFailure(resp.code,resp.message)
                    }

                }

                override fun onFailure(call: Call<AnnounceDetailResponse>, t: Throwable) {
                    announceDetailView.onGetAnnounceDetailFailure(400,"네트워크 오류가 발생했습니다.")
                }

            })
    }

}
