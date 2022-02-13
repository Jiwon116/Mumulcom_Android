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

    fun login(email: Login) {
        val loginService = getRetrofit().create(LoginRetrofitInterface::class.java)

        loginView.onLoginLoading()

        loginService.login(email).enqueue(object : Callback<LoginResponse>{
            @SuppressLint("LongLogTag")
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                Log.d("LOGIN/API-RESPONSE", response.toString())

                if(response.isSuccessful && response.code() == 200) {
                    val resp = response.body()!!
                    Log.d("LOGIN/API-RESPONSE", resp.toString())

                    when(resp.code) {
                        1000 -> {
                            resp.result?.let { loginView.onLoginSuccess(it) }
                            Log.d("LOGIN","성공")
                        }
                        else -> loginView.onLoginFailure(resp.code, resp.message)
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.d("LOGIN/API-ERROR", t.toString())
                loginView.onLoginFailure(400, "네트워크 오류가 발생했습니다.")
            }
        })
    }
}