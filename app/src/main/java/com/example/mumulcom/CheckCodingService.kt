package com.example.mumulcom

import android.util.Log
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CheckCodingService() {
    private lateinit var checkCodingQuestionView: CheckCodingQuestionView

    fun setcheckcodingquestionView(checkCodingQuestionView: CheckCodingQuestionView) {
        this.checkCodingQuestionView = checkCodingQuestionView

    }

    fun checkCodingQuestion(jwt: String, images: MultipartBody.Part, codeQuestionReq: MultipartBody.Part) {
        val checkCodingQuestionService =
            getRetrofit().create(CheckCodingQuestionRetrofitInterface::class.java)

        checkCodingQuestionView.onCheckCodingQuestionLoading()


        checkCodingQuestionService.checkCodingQuestion(jwt, images, codeQuestionReq).enqueue(object :
            Callback<CheckCodingQuestionResponse> {
            override fun onResponse(
                call: Call<CheckCodingQuestionResponse>,
                response: Response<CheckCodingQuestionResponse>
            ) {
                Log.d("CHECKCODING/API-RESPONSE", response.toString())
                Log.d("CHECKCODING/API-RESPONSE", response.errorBody().toString())

                if (response.isSuccessful && response.code() == 200) {
                    val resp = response.body()!!

                    Log.d("CHECKCODING/API2", resp.toString())

                    when (resp.code) {
                        1000 -> checkCodingQuestionView.onCheckCodingQuestionSuccess(resp.result)
                        else -> checkCodingQuestionView.onCheckCodingQuestionFailure(
                            resp.code,
                            resp.message
                        )
                    }
                }
            }

            override fun onFailure(call: Call<CheckCodingQuestionResponse>, t: Throwable) {
                Log.d("CHECKCODING/API-ERROR", t.message.toString())

                checkCodingQuestionView.onCheckCodingQuestionFailure(400, "네트워크 오류가 발생했습니다.")
            }
        })
        Log.d("CHECKCODING/API", "Hello")
    }
}