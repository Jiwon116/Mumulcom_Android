package com.example.mumulcom

import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AnswerService{
    private lateinit var answerView: AnswerView

    fun setanswerView(answerView: AnswerView){
        this.answerView=answerView
    }


    fun answer(jwt: String, images: List<MultipartBody.Part?>?, answer: Answer){
        val answerService= getRetrofit().create(AnswerRetrofitInterface::class.java)

        val body = "".toRequestBody(MultipartBody.FORM)
        val emptyPart = MultipartBody.Part.createFormData("images","",body)
        val emptyList = arrayListOf<MultipartBody.Part>()
        emptyList.add(emptyPart)

        answerView.onAnswerLoading()

        answerService.answer(jwt, if (images==null){
                                                   emptyList
                                                   }else{
                                                        images
                                                        }, answer).enqueue(object : Callback<AnswerResponse>{
            override fun onResponse(
                call: Call<AnswerResponse>,
                response: Response<AnswerResponse>
            ) {
                Log.d("ANSWER/API-RESPONSE", response.toString())

                if (response.isSuccessful&&response.code()==200){
                    val resp=response.body()!!
                    Log.d("ANSWER/API-SUCCESS-mumulcom", resp.toString())

                    when(resp.code){
                        1000->answerView.onAnswerSuccess(resp.result)
                        else->answerView.onAnswerFailure(resp.code, resp.message)
                    }
                }
            }

            override fun onFailure(call: Call<AnswerResponse>, t: Throwable) {
                Log.d("ANSWER/API-ERROR",t.message.toString())

                answerView.onAnswerFailure(400, "네트워크 오류가 발생했습니다.")
            }
        })
        Log.d("ANSWER/API","Hello")
    }
}