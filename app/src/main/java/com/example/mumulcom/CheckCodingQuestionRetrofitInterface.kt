package com.example.mumulcom

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


//인터페이스 생성
interface CheckCodingQuestionRetrofitInterface {


    @POST("/questions/coding")
    fun checkCodingQuestion(
        @Header("X-ACCESS-TOKEN") jwt: String,
        @Body checkCoding: CheckCoding
    ): Call<CheckCodingQuestionResponse>
}




//@Header("X-ACCESS-TOKEN") jwt : String, // jwt
//@Path("userIdx") userIdx : Long,