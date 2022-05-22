package com.example.mumulcom


import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CheckCodingQuestionService{
    private lateinit var checkCodingQuestionView: CheckCodingQuestionView

    fun setcheckcodingquestionView(checkCodingQuestionView: CheckCodingQuestionView){
        this.checkCodingQuestionView=checkCodingQuestionView
    }

    fun checkCodingQuestion(
        jwt: String, images: ArrayList<MultipartBody.Part?>?, checkCoding: CheckCoding) {
//        jwt: String, images: ArrayList<MultipartBody.Part?>?, userIdx: Long, currentError:String, myCodingSkill: String?, bigCategoryIdx: Long, smallCategoryIdx: Long?, title: String, codeQuestionUrl: String?
//        ) {

        val checkCodingQuestionService= getRetrofit().create(CheckCodingQuestionRetrofitInterface::class.java)

//        val jsonObject = JSONObject("{\"userIdx\":${userIdx},\"currentError\":\"${currentError}\",\"myCodingSkill\":\"${myCodingSkill}\",\"bigCategoryIdx\":${bigCategoryIdx}, \"smallCategoryIdx\":${smallCategoryIdx},\"title\":\"${title}\",\"codeQuestionUrl\":\"${codeQuestionUrl}\"}").toString()
//        val codeQuestionReq = jsonObject.toRequestBody("application/json".toMediaTypeOrNull())
//        Log.d("json/jsonObject", jsonObject)
//        Log.d("json/jsonBody", codeQuestionReq.toString())

        checkCodingQuestionView.onCheckCodingQuestionLoading()

        checkCodingQuestionService.checkCodingQuestion(
            jwt, if (images==null){
                null
            }else{
                images
            }, checkCoding).enqueue(object : Callback<CheckCodingQuestionResponse> {
            override fun onResponse(call: Call<CheckCodingQuestionResponse>, response: Response<CheckCodingQuestionResponse>) {
                Log.d("CHECKCODING/API-RESPONSE", response.toString())
                Log.d("co/API-RESPONSE", checkCoding.toString())
                if (response.isSuccessful&&response.code()==200){
                    val resp=response.body()!!
                    Log.d("CHECKCODING/API-SUCCESS-mumulcom", resp.toString())

                    when(resp.code){
                        1000-> checkCodingQuestionView.onCheckCodingQuestionSuccess(resp.result)

                        else->checkCodingQuestionView.onCheckCodingQuestionFailure(resp.code, resp.message)
                    }
                }
            }

            override fun onFailure(call: Call<CheckCodingQuestionResponse>, t: Throwable) {
                Log.d("CHECKCODING/API-ERROR",t.message.toString())

                checkCodingQuestionView.onCheckCodingQuestionFailure(400, "네트워크 오류가 발생했습니다.")
                Log.d("CHECKCODING/API-ERROR",t.message.toString())
            }
        })
    }

}