package com.example.mumulcom

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ReportUserService {
    private lateinit var reportUserView:ReportUserView

    fun setReportUserView(reportUserView:ReportUserView){
        this.reportUserView=reportUserView
    }

    fun postReportUser(token:String,body:PostUserReq){
        val reportUserService = getRetrofit().create(ReportUserRetrofitInterface::class.java)

        reportUserView.onReportUserLoading()

        reportUserService.postReportUser(token,body)
            .enqueue(object : Callback<ReportUserResponse> {
                override fun onResponse(
                    call: Call<ReportUserResponse>,
                    response: Response<ReportUserResponse>
                ) {
                    if(response.isSuccessful) {
                        val resp = response.body()!!

                        when(resp.code){
                            1000-> reportUserView.onReportUserSuccess(resp.result)
                            else-> reportUserView.onReportUserFailure(resp.code,resp.message)
                        }
                    }
                }

                override fun onFailure(call: Call<ReportUserResponse>, t: Throwable) {
                    reportUserView.onReportUserFailure(400,"네트워크 오류가 발생했습니다.")
                }

            })
    }
}