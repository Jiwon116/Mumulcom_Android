package com.example.mumulcom

import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CheckConceptQuestionService{
    private lateinit var checkConceptQuestionView: CheckConceptQuestionView

    fun setcheckconceptquestionView(checkConceptQuestionView: CheckConceptQuestionActivity){
        this.checkConceptQuestionView=checkConceptQuestionView
    }

    fun checkConceptQuestion(jwt: String, images: List<MultipartBody.Part?>?, checkConcept: CheckConcept){
//        fun checkConceptQuestion(jwt: String, images: List<MultipartBody.Part?>?, userIdx: Long, bigCategoryIdx: Long, smallCategoryIdx: Long?, title: String, content:String){
        val checkConceptQuestionService= getRetrofit().create(CheckConceptQuestionRetrofitInterface::class.java)

//        val jsonObject = JSONObject("{\"userIdx\":${userIdx},\"bigCategoryIdx\":${bigCategoryIdx}, \"smallCategoryIdx\":${smallCategoryIdx},\"title\":\"${title}\",\"content\":\"${content}\"}").toString()
//        val conceptQueReq = jsonObject.toRequestBody("application/json".toMediaTypeOrNull())
//        Log.d("json/jsonObject", jsonObject)
//        Log.d("json/jsonBody", conceptQueReq.toString())

        checkConceptQuestionView.onCheckConceptQuestionLoading()

        checkConceptQuestionService.checkConceptQuestion(jwt, if (images==null){
            null
        }else{
            images
        }, checkConcept).enqueue(object : Callback<CheckConceptQuestionResponse>{
            override fun onResponse(
                call: Call<CheckConceptQuestionResponse>,
                response: Response<CheckConceptQuestionResponse>
            ) {
                Log.d("CHECKCONCEPT/API-RESPONSE", response.toString())

                if (response.isSuccessful&&response.code()==200){
                    val resp=response.body()!!
                    Log.d("CHECKCONCEPT/API-SUCCESS-mumulcom", resp.toString())

                    when(resp.code){
                        1000->checkConceptQuestionView.onCheckConceptQuestionSuccess(resp.result)
                        else->checkConceptQuestionView.onCheckConceptQuestionFailure(resp.code, resp.message)
                    }
                }
            }

            override fun onFailure(call: Call<CheckConceptQuestionResponse>, t: Throwable) {
                Log.d("CHECKCONCEPT/API-ERROR",t.message.toString())

                checkConceptQuestionView.onCheckConceptQuestionFailure(400, "네트워크 오류가 발생했습니다.")
            }
        })
        Log.d("CHECKCONCEPT/API","Hello")
    }
}