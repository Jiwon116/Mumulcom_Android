package com.example.mumulcom

import android.util.Log
import retrofit2.Call
import retrofit2.Response

class SignUpNicknameService {
    private lateinit var signupNicknameView: SignupNicknameView

    fun setSignupNicknameView(signupNicknameView: SignupNicknameView) {
        this.signupNicknameView = signupNicknameView
    }

    // nickname 중복체크를 하고 result 값 받아오기
    fun getNicknameCheck(nickname: String){
        val signUpNicknameService = getRetrofit().create(SignUpNicknameCheckInterface::class.java)

        signupNicknameView.getNicknameCheckLoading()

        signUpNicknameService.getNicknameCheck(nickname).enqueue(object :retrofit2.Callback<SignUpNicknameResponse>{
                override fun onResponse(call: Call<SignUpNicknameResponse>, response: Response<SignUpNicknameResponse>) {

                    val resp = response.body()!!

                    when(resp.code){
                        1000-> {signupNicknameView.getNicknameCheckSuccess(resp.result)
                            Log.d("signUpNicknameService/API","성공")
                        }
                        else-> signupNicknameView.getNicknameCheckFailure(resp.code,resp.message)
                    }

                }

                override fun onFailure(call: Call<SignUpNicknameResponse>, t: Throwable) {
                    signupNicknameView.getNicknameCheckFailure(400,"네트워크 오류가 발생했습니다.")
                }
            })
    }
}