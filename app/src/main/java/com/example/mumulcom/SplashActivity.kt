package com.example.mumulcom

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mumulcom.databinding.ActivitySplashBinding
import com.kakao.sdk.user.UserApiClient

class SplashActivity : AppCompatActivity(), LoginView {
    lateinit var binding:ActivitySplashBinding

    private var email: String = ""  // 유저 이메일
    private var jwt: String = ""    // jwt

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 사용자 정보 요청
        UserApiClient.instance.me { kakaoUser, error ->
            if (error != null) {    // 카카오 로그인이 되어있지 않으면
                Log.e(ContentValues.TAG, "사용자 정보 요청 실패", error)
            }
            else if (kakaoUser != null) {   // 카카오 로그인이 되어있으면
                email = kakaoUser.kakaoAccount?.email.toString()
                Log.i(ContentValues.TAG, "사용자 정보 요청 성공 - 이메일: $email")
                login(email)    // 로그인
            }
        }

        Handler(Looper.getMainLooper()).postDelayed({
            if (email.isNotEmpty() && jwt.isNotEmpty()) {  // 이메일 값과 jwt 값이 있으면
                Log.d(ContentValues.TAG, "MainActivity 실행")
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {    // 이메일 또는 jwt 값이 비어있으면 -> 카카오 로그인 또는 회원가입 진행해야함
                Log.d(ContentValues.TAG, "LoginActivity 실행")
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } }, 3000) //1초가 1000
    }

    private fun login(email: String) {
        val login = Login(email)

        val loginService = LoginService()
        loginService.setLoginView(this)
        loginService.login(login)
    }

    override fun onLoginLoading() {
        Log.d("Login/API","로그인 로딩 중...")
    }

    override fun onLoginSuccess(user: User) {
        jwt = user.jwt
        saveJwt(this, user.jwt)
        saveUserIdx(this, user.userIdx)
        saveEmail(this, user.email)
        saveName(this, user.name)
        saveNickname(this, user.nickname)
        saveGroup(this, user.group)
        user.myCategories?.let { saveCategories(this, it) }
        saveProfileImgUrl(this, user.profileImgUrl)

        Log.d("jwt", user.jwt)
        Log.d("userIdx", user.userIdx.toString())
        Log.d("email", user.email)
        Log.d("name", user.name)
        Log.d("nickname", user.nickname)
        Log.d("group", user.group)
        Log.d("myCategories", user.myCategories.toString())
        Log.d("profileImgUrl", user.profileImgUrl)
    }

    override fun onLoginFailure(code: Int, message: String) {
        saveJwt(this, "")
        when(code) {
            3014 -> {   // DB에 없는 이메일일 경우
                Log.d("Login/API", message)
            }
            400 -> {
                Log.d("Login/API", message)
            }
        }
    }
}