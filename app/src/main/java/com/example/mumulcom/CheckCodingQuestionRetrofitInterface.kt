package com.example.mumulcom

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


//인터페이스 생성
interface CheckCodingQuestionRetrofitInterface {
    @Multipart
    @POST("/questions/coding")
    fun checkCodingQuestion(
        @Header("X-ACCESS-TOKEN") X_ACCESS_TOKEN: String,
        @Part images: List<MultipartBody.Part?>?,
        @Part ("codeQuestionReq") codeQuestionReq: CheckCoding,
        ): Call<CheckCodingQuestionResponse>
}



//@Header("X-ACCESS-TOKEN") jwt : String, // jwt
//@Path("userIdx") userIdx : Long,