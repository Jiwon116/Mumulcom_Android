package com.example.mumulcom

import android.annotation.SuppressLint
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginService {
    private lateinit var loginView: LoginView

    fun setLoginView(loginView: SplashActivity) {
        this.loginView = loginView
    }

    fun login(email: Login){
        val loginService = getRetrofit().create(LoginRetrofitInterface::class.java)

        loginView.onLoginLoading()

        loginService.login(email)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {

                    if(response.isSuccessful) {
                        val resp = response.body()!!

                        when(resp.code){
                            1000-> resp.result?.let { loginView.onLoginSuccess(it) }
                            else-> loginView.onLoginFailure(resp.code,resp.message)
                        }
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    loginView.onLoginFailure(400,"네트워크 오류가 발생했습니다.")
                }

            })
    }
}