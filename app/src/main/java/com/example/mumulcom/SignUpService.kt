package com.example.mumulcom

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpService {
    private lateinit var signUpView: SignUpView

    fun setSignUpView(signUpView: SignUpCategoryActivity) {
        this.signUpView = signUpView
    }

    fun signUp(signup: SignUp){
        val authService = getRetrofit().create(SignUpRetrofitInterface::class.java)

        signUpView.onSignUpLoading()

        authService.signUp(signup)
            .enqueue(object : Callback<SignUpResponse> {
                override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {

                    if(response.isSuccessful) {
                        val resp = response.body()!!

                        when(resp.code){
                            1000-> resp.result?.let { signUpView.onSignUpSuccess(it) }
                            else-> signUpView.onSignUpFailure(resp.code,resp.message)
                        }
                    }
                }

                override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                    signUpView.onSignUpFailure(400,"네트워크 오류가 발생했습니다.")
                }

            })
    }
}