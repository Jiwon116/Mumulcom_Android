package com.example.mumulcom

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


//인터페이스 생성
interface CheckConceptQuestionRetrofitInterface {
    @Multipart
    @POST("/questions/concept")
    fun checkConceptQuestion(
        @Header("X-ACCESS-TOKEN") jwt: String,
        @Part images: List<MultipartBody.Part?>?,
        @Part ("conceptQueReq") conceptQueReq: CheckConcept,
    ): Call<CheckConceptQuestionResponse>
}




//@Header("X-ACCESS-TOKEN") jwt : String, // jwt
//@Path("userIdx") userIdx : Long,