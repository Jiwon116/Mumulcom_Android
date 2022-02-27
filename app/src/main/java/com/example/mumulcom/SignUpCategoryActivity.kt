package com.example.mumulcom

import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mumulcom.databinding.ActivitySignupCategoryBinding

class SignUpCategoryActivity : AppCompatActivity(), View.OnClickListener, SignUpView {
    lateinit var binding: ActivitySignupCategoryBinding

    private lateinit var email: String
    private lateinit var name: String
    private lateinit var nickname: String
    private lateinit var group: String

    private var categories: String? = null

    private var myCategories: MutableList<String> = mutableListOf()   // defalut값은 null, 건너뛰기 가능

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SignUpActivity에서 intent로 받아온 값들
        email = intent.getStringExtra("email").toString()
        name = intent.getStringExtra("name").toString()
        nickname = intent.getStringExtra("nickname").toString()
        group = intent.getStringExtra("group").toString()

        // 카테고리 클릭 이벤트
        binding.signupCategoryAndroidBt.setOnClickListener(this)
        binding.signupCategoryIosBt.setOnClickListener(this)
        binding.signupCategoryHtmlBt.setOnClickListener(this)
        binding.signupCategoryCssBt.setOnClickListener(this)
        binding.signupCategoryReactBt.setOnClickListener(this)
        binding.signupCategoryNodejsBt.setOnClickListener(this)
        binding.signupCategorySpringBt.setOnClickListener(this)
        binding.signupCategoryCBt.setOnClickListener(this)
        binding.signupCategoryCppBt.setOnClickListener(this)
        binding.signupCategoryJsBt.setOnClickListener(this)
        binding.signupCategoryJavaBt.setOnClickListener(this)
        binding.signupCategoryPythonBt.setOnClickListener(this)

        // 건너뛰기 버튼 클릭 시 (categories = null)
        // 사용자 정보 서버로 보내고 바로 MainActivity
        binding.signupNextStepIv.setOnClickListener {
            signUp()
        }

        // 선택완료 버튼 클릭 시
        // 사용자 정보 서버로 보내고 바로 MainActivity
        binding.signupSelectDoneIv.setOnClickListener {
            signUp()
        }
    }

    // 각 버튼의 클릭 이벤트
    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.signup_category_android_bt -> {
                categories = "안드로이드"
                selectCategories(view, categories)
            }
            R.id.signup_category_ios_bt -> {
                categories = "iOS"
                selectCategories(view, categories)
            }
            R.id.signup_category_html_bt -> {
                categories = "HTML"
                selectCategories(view, categories)
            }
            R.id.signup_category_css_bt -> {
                categories = "CSS"
                selectCategories(view, categories)
            }
            R.id.signup_category_react_bt -> {
                categories = "React"
                selectCategories(view, categories)
            }
            R.id.signup_category_nodejs_bt -> {
                categories = "Node.js"
                selectCategories(view, categories)
            }
            R.id.signup_category_spring_bt -> {
                categories = "Spring"
                selectCategories(view, categories)
            }
            R.id.signup_category_c_bt -> {
                categories = "C"
                selectCategories(view, categories)
            }
            R.id.signup_category_cpp_bt -> {
                categories = "C++"
                selectCategories(view, categories)
            }
            R.id.signup_category_js_bt -> {
                categories = "JavaScript"
                selectCategories(view, categories)
            }
            R.id.signup_category_java_bt -> {
                categories = "Java"
                selectCategories(view, categories)
            }
            R.id.signup_category_python_bt -> {
                categories = "Python"
                selectCategories(view, categories)
            }
        }
    }

    // TODO 변수 타입 확인하기..! 에러는 안나는데 경고가 뜸
    private fun selectCategories(view: View, categories: String?) {

        if (myCategories.size < 5) {
            // 버튼 클릭 시 배경 변경을 위해
            // isSelected 값 변경 (클릭 시 state_selected 변경)
            view.isSelected = !view.isSelected

            if (view.isSelected) {   // isSelected = true -> 카테고리 선택함
                myCategories.add(categories.toString()) // 카테고리 리스트에 추가
            } else {
                myCategories.remove(categories.toString())  // 카테고리 리스트에서 삭제
            }
        } else if (myCategories.size == 5 && view.isSelected) {
            myCategories.remove(categories.toString())
            view.isSelected = false
        } else {
            Toast.makeText(this, "카테고리는 5개까지 선택해주세요!", Toast.LENGTH_SHORT).show()
        }

        Log.d("myCategories", myCategories.toString())
        Log.d("myCategories size", myCategories.size.toString())

        // 카테고리 리스트 크기에 따라
        // 상단 건너뛰기 버튼 <-> 선택 완료 버튼
        if (myCategories.size == 0) {
            binding.signupNextStepIv.visibility = View.VISIBLE
            binding.signupSelectDoneIv.visibility = View.GONE
        } else {
            binding.signupNextStepIv.visibility = View.GONE
            binding.signupSelectDoneIv.visibility = View.VISIBLE
        }

        // 카테고리 리스트 크기에 따라서
        // 별 버튼 색상 변경
        if (myCategories.size == 0) {
            binding.signupCategoryStar1Iv.setImageResource(R.drawable.ic_signup_category_no_select)
            binding.signupCategoryStar2Iv.setImageResource(R.drawable.ic_signup_category_no_select)
            binding.signupCategoryStar3Iv.setImageResource(R.drawable.ic_signup_category_no_select)
            binding.signupCategoryStar4Iv.setImageResource(R.drawable.ic_signup_category_no_select)
            binding.signupCategoryStar5Iv.setImageResource(R.drawable.ic_signup_category_no_select)
        } else if (myCategories.size == 1) {
            binding.signupCategoryStar1Iv.setImageResource(R.drawable.ic_signup_category_select)
            binding.signupCategoryStar2Iv.setImageResource(R.drawable.ic_signup_category_no_select)
            binding.signupCategoryStar3Iv.setImageResource(R.drawable.ic_signup_category_no_select)
            binding.signupCategoryStar4Iv.setImageResource(R.drawable.ic_signup_category_no_select)
            binding.signupCategoryStar5Iv.setImageResource(R.drawable.ic_signup_category_no_select)
        } else if (myCategories.size == 2) {
            binding.signupCategoryStar1Iv.setImageResource(R.drawable.ic_signup_category_select)
            binding.signupCategoryStar2Iv.setImageResource(R.drawable.ic_signup_category_select)
            binding.signupCategoryStar3Iv.setImageResource(R.drawable.ic_signup_category_no_select)
            binding.signupCategoryStar4Iv.setImageResource(R.drawable.ic_signup_category_no_select)
            binding.signupCategoryStar5Iv.setImageResource(R.drawable.ic_signup_category_no_select)
        } else if (myCategories.size == 3) {
            binding.signupCategoryStar1Iv.setImageResource(R.drawable.ic_signup_category_select)
            binding.signupCategoryStar2Iv.setImageResource(R.drawable.ic_signup_category_select)
            binding.signupCategoryStar3Iv.setImageResource(R.drawable.ic_signup_category_select)
            binding.signupCategoryStar4Iv.setImageResource(R.drawable.ic_signup_category_no_select)
            binding.signupCategoryStar5Iv.setImageResource(R.drawable.ic_signup_category_no_select)
        } else if (myCategories.size == 4) {
            binding.signupCategoryStar1Iv.setImageResource(R.drawable.ic_signup_category_select)
            binding.signupCategoryStar2Iv.setImageResource(R.drawable.ic_signup_category_select)
            binding.signupCategoryStar3Iv.setImageResource(R.drawable.ic_signup_category_select)
            binding.signupCategoryStar4Iv.setImageResource(R.drawable.ic_signup_category_select)
            binding.signupCategoryStar5Iv.setImageResource(R.drawable.ic_signup_category_no_select)
        } else if (myCategories.size == 5) {
            binding.signupCategoryStar1Iv.setImageResource(R.drawable.ic_signup_category_select)
            binding.signupCategoryStar2Iv.setImageResource(R.drawable.ic_signup_category_select)
            binding.signupCategoryStar3Iv.setImageResource(R.drawable.ic_signup_category_select)
            binding.signupCategoryStar4Iv.setImageResource(R.drawable.ic_signup_category_select)
            binding.signupCategoryStar5Iv.setImageResource(R.drawable.ic_signup_category_select)
        }
    }

    private fun getUser(): SignUp {
        Log.d(
            TAG, "email : $email " +
                    "\nname: $name " +
                    "\nnickname: $nickname " +
                    "\ngroup: $group " +
                    "\nmyCategories: $myCategories"
        )

        return SignUp(email, name, nickname, group, myCategories)
    }

    private fun signUp() {
        val signUpService = SignUpService()
        signUpService.setSignUpView(this)

        signUpService.signUp(getUser())

        Log.d("SIGNUPACT/ASNYC", "Hello, $name")
    }

    override fun onSignUpLoading() {
        binding.signupLoadingPb.visibility = View.VISIBLE
        Log.d("SingUpCategoryActivity/API", "회원가입 로딩 중...")
    }

    override fun onSignUpSuccess() {
        binding.signupLoadingPb.visibility = View.GONE
        startMainActivity()
        finish()
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        finish()
    }

    override fun onSignUpFailure(code: Int, message: String) {
        binding.signupLoadingPb.visibility = View.GONE

        when (code) {
            2021 -> {   // 이미 가입된 이메일입니다
                Log.d("SingUpCategoryActivity/API", message)
                dialogPopup()
            }
            2024 -> {   // DB에 없는 카테고리일 경우 - 잘못된 카테고리 명입니다
                Log.d("SingUpCategoryActivity/API", message)
            }
            400 -> {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun dialogPopup() {
        val customDialog = CustomDialog(finishApp = { finish() })
        customDialog.show(supportFragmentManager, "CustomDialog")
    }
}
