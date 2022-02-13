package com.example.mumulcom

import android.util.Log
import retrofit2.Call
import retrofit2.Response

class AlarmService {
    private lateinit var alarmView: AlarmView

    // 외부 접근
    fun setAlarmService(alarmView: AlarmView){
        this.alarmView = alarmView
    }

    // 서버에서 alarm 정보 가져오기
    fun getAlarms(jwt: String, userIdx: Long){
        val alarmService = getRetrofit().create(AlarmRetrofitInterface::class.java)

        alarmView.onGetAlarmLoading()

        alarmService.getAlarms(jwt, userIdx)
            .enqueue(object :retrofit2.Callback<AlarmResponse>{
                override fun onResponse(call: Call<AlarmResponse>, response: Response<AlarmResponse>) {

                    val resp = response.body()!!

                    when(resp.code){
                        1000-> alarmView.onGetAlarmSuccess(resp.result)
                        else-> alarmView.onGetAlarmFailure(resp.code,resp.message)
                    }

                }

                override fun onFailure(call: Call<AlarmResponse>, t: Throwable) {
                    alarmView.onGetAlarmFailure(400,"네트워크 오류가 발생했습니다.")
                }

            })
    }
}