package com.example.mumulcom

import android.annotation.SuppressLint
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpService {
    private lateinit var signUpView: SignUpView

    fun setSignUpView(signUpView: SignUpCategoryActivity) {
        this.signUpView = signUpView
    }

    fun signUp(user: User) {
        val authService = getRetrofit().create(SignUpRetrofitInterface::class.java)

        signUpView.onSignUpLoading()

        authService.signUp(user).enqueue(object : Callback<SignUpResponse> {
            @SuppressLint("LongLogTag")
            override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                Log.d("SIGNUP/API-RESPONSE", response.toString())

                if(response.isSuccessful && response.code() == 200) {
                    val resp = response.body()!!
                    Log.d("SIGNUP/API-RESPONSE", resp.toString())

                    when(resp.code) {
                        1000 -> {
                            signUpView.onSignUpSuccess()
                            Log.d("SIGNUP","성공")
                        }
                        else -> signUpView.onSignUpFailure(resp.code, resp.message)
                    }
                }
            }

            override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                Log.d("SIGNUPACT/API-ERROR", t.toString())
                //signUpView.onSignUpFailure(400, t.message.toString())
                signUpView.onSignUpFailure(400, "네트워크 오류가 발생했습니다.")
            }
        })
    }
}