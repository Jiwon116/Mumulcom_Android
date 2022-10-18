package com.example.mumulcom

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.mumulcom.databinding.ActivitySplashBinding
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.user.UserApiClient

@Suppress("UnclearPrecedenceOfBinaryExpression")
class SplashActivity : AppCompatActivity(), LoginView {
    lateinit var binding:ActivitySplashBinding

    private var email: String = ""  // 유저 이메일
    private var fcmToken: String = ""   // fcm token
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

                // firebase 토큰을 가져오는 부분
                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                    if (task.isSuccessful) { // 성공 시
                        fcmToken = task.result
                        Log.d("FCM token(task result)", fcmToken)

                        login(email, fcmToken)    // 로그인
                    }
                }

                // updateResult()
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



    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        setIntent(intent) // 새로 들어온 인텐트로 교체

        updateResult(true)
    }

    private fun updateResult(isNewIntent: Boolean = false) {
        // isNewIntent 앱이 새로 켜진건지
        val msg = intent.getStringExtra("notificationType") ?: "앱 런처" + if (isNewIntent) {
            "(으)로 갱신했습니다."
        } else {
            "(으)로 실행했습니다."
        }

        Log.d(ContentValues.TAG, msg)
    }

    private fun login(email: String, fcmToken: String) {
        val login = Login(email, fcmToken)

        val loginService = LoginService()
        loginService.setLoginView(this)
        loginService.login(login)
    }

    override fun onLoginLoading() {
        Log.d("Login/API","로그인 로딩 중...")
    }

    override fun onLoginSuccess(user: LoginUser) {
        jwt = user.jwt
        saveJwt(this, user.jwt)
        saveUserIdx(this, user.userIdx)
        saveEmail(this, user.email)
        saveName(this, user.name)
        saveNickname(this, user.nickname)
        saveGroup(this, user.group)
        user.myCategories?.let { saveCategories(this, it) }
        saveProfileImgUrl(this, user.profileImgUrl)
        saveFcmToken(this, user.fcmToken)

        Log.d("jwt", user.jwt)
        Log.d("userIdx", user.userIdx.toString())
        Log.d("email", user.email)
        Log.d("name", user.name)
        Log.d("nickname", user.nickname)
        Log.d("group", user.group)
        Log.d("myCategories", user.myCategories.toString())
        Log.d("profileImgUrl", user.profileImgUrl)
        Log.d("FCM token(user fcm token)", user.fcmToken)
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