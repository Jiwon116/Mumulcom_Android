package com.example.mumulcom

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mumulcom.databinding.ActivitySignupBinding
import com.kakao.sdk.user.UserApiClient
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity(), SignupNicknameView {
    lateinit var binding: ActivitySignupBinding

    private var email: String = ""
    private var name: String = ""
    private var group: String = ""
    private var nickname: String = ""

    private var validCurrentNickname: Boolean = false   // 현재 입력한 아이디가 유효한가
    private var validNickname: Boolean = false   // 중복이 아닌 아이디인가
    private var validGroup: Boolean = false    // 소속을 선택했는가

    private val nicknameValidation = "^[가-힣a-z0-9]{2,8}$"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 카카오 로그인 이후 사용자 정보 요청
        getUserInfo()

        setupSpinner()
        setupSpinnerHandler()

        // 닉네임 바로바로 검사
        binding.signupNicknameEt.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                // text가 변경된 후 호출
                // p0에는 변경 후의 문자열이 담겨있음
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // text가 변경되기 전 호출
                // p0에는 변경 전 문자열이 담겨있음
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // text가 바뀔때마다 호출
                // 텍스트가 바뀌었다는건 다시 중복 검사를 해야함
                validNickname = false
                changeButton()
                checkNickname() // 닉네임 유효성 검사 함수
                nickname = p0.toString()

                // edittext 배경도 회색으로 다시 변경
                binding.signupNicknameEt.setBackgroundResource(R.drawable.basic_outline_gray)
            }
        })

        // 닉네임 중복 검사 (중복확인버튼)
        binding.signupDuplicateCheckBt.setOnClickListener {
            // 중복 확인
            getNicknameCheck()
            // 키보드 사라지기
            CloseKeyboard()
        }

        // 화면 배경 누르면 키보드 사라지기
        binding.signupLy.setOnClickListener {
            CloseKeyboard()
        }
    }

    private fun getUserInfo() {
        // 사용자 정보 요청
        UserApiClient.instance.me { kakaoUser, error ->
            if (error != null) {
                Log.e(TAG, "사용자 정보 요청 실패", error)
            }
            else if (kakaoUser != null) {
                email = kakaoUser.kakaoAccount?.email.toString()
                name = kakaoUser.kakaoAccount?.profile?.nickname.toString()

                Log.i(TAG, "사용자 정보 요청 성공 - 이메일: $email 이름: $name")
            }
        }
    }

    // dp 값을 px 값으로 변환해주는 함수
    private fun dipToPixels(dipValue: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dipValue,
            resources.displayMetrics
        )
    }

    // 스피너에 어레이 어댑터 연결
    private fun setupSpinner() {
        val groups = resources.getStringArray(R.array.signup_group)
        val adapter = object : ArrayAdapter<String>(this, R.layout.item_spinner){
            @SuppressLint("CutPasteId")
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)
                if(position == count) {
                    // 마지막 포지션의 textView를 힌트용으로 사용
                    (v.findViewById<View>(R.id.item_spinner_tv) as TextView).text = ""
                    // 아이템의 마지막 값을 불러와 hint로 추가함
                    (v.findViewById<View>(R.id.item_spinner_tv) as TextView).hint = getItem(count)
                }
                return v
            }
            override fun getCount(): Int {
                // 마지막 아이템은 hint용이기 때문에 1을 빼줌
                return super.getCount() - 1
            }
        }
        // 아이템 추가
        adapter.addAll(groups.toMutableList())
        // hint로 사용할 문구를 마지막 아이템에 추가
        adapter.add("클릭하여 소속 찾기")
        // 어댑터 연결
        binding.signupGroupSp.adapter = adapter
        // 스피너 초기값을 마지막 아이템으로 설정
        binding.signupGroupSp.setSelection(adapter.count)

        // droplist를 스피너와 간격을 두고 나오게 함 -> 아이템 크기 = 125px
        binding.signupGroupSp.dropDownVerticalOffset = dipToPixels(45f).toInt()
    }

    // 스피너 클릭 이벤트 핸들러
    private fun setupSpinnerHandler() {
        binding.signupGroupSp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (!binding.signupGroupSp.getItemAtPosition(position).equals("클릭하여 소속 찾기")) {
                    // 소속 선택하면 배경 변경
                    binding.signupGroupSp.setBackgroundResource(R.drawable.bg_signup_group_selected)
                    // 소속을 선택하면 group 변수에 사용자 소속 저장하기
                    group = binding.signupGroupSp.getItemAtPosition(position).toString()
                    validGroup = true
                    Log.i(TAG, "소속 확인: $group validGroup: $validGroup")
                    changeButton()
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }

    // 닉네임 유효성 검사 함수
    fun checkNickname(): Boolean {
        var n = binding.signupNicknameEt.text.toString().trim() // 공백제거
        val p = Pattern.matches(nicknameValidation, n) // 패턴 맞는지 확인
        return if (p) {
            // 닉네임 형태가 패턴에 적합할 경우
            binding.signupNicknameRule1Tv.visibility = View.VISIBLE
            binding.signupNicknameRule2Tv.visibility = View.VISIBLE
            binding.signupNicknameValidIv.visibility = View.VISIBLE

            binding.signupNicknameErrorTv.visibility = View.GONE
            binding.signupNicknameErrorIv.visibility = View.GONE
            binding.signupNicknameDuplicateValidTv.visibility = View.GONE
            binding.signupNicknameDuplicateErrorTv.visibility = View.GONE
            validCurrentNickname = true
            changeButton()
            true
        } else {
            // 닉네임 형태가 패턴에 적합하지 않을 경우
            binding.signupNicknameErrorTv.visibility = View.VISIBLE
            binding.signupNicknameErrorIv.visibility = View.VISIBLE

            binding.signupNicknameRule1Tv.visibility = View.GONE
            binding.signupNicknameRule2Tv.visibility = View.GONE
            binding.signupNicknameValidIv.visibility = View.GONE
            binding.signupNicknameDuplicateValidTv.visibility = View.GONE
            binding.signupNicknameDuplicateErrorTv.visibility = View.GONE
            validCurrentNickname = false
            changeButton()
            false
        }
    }

    private fun getNicknameCheck() {
        val signUpNicknameService = SignUpNicknameService()
        signUpNicknameService.setSignupNicknameView(this)

        signUpNicknameService.getNicknameCheck(this.nickname)
    }

    override fun getNicknameCheckLoading() {
        Log.d("SignUpActivity/NicknameCheck/API","중복 확인 로딩 중...")
    }

    override fun getNicknameCheckSuccess(result: Boolean) {
        // result = ture -> 사용하는 사람이 있음
        // result = false -> 사용 가능
        if (result) { // 닉네임 사용 불가능
            validNickname = !result // validNickname = false

            Log.i(TAG, "닉네임 중복")

            // 중복된 닉네임입니다 출력
            binding.signupNicknameDuplicateErrorTv.visibility = View.VISIBLE
            binding.signupNicknameErrorIv.visibility = View.VISIBLE

            binding.signupNicknameRule1Tv.visibility = View.GONE
            binding.signupNicknameRule2Tv.visibility = View.GONE
            binding.signupNicknameValidIv.visibility = View.GONE

            changeButton()

        } else {    // 닉네임 사용 가능
            validNickname = !result // validNickname = true

            Log.i(TAG, "닉네임: $nickname validNickname: $validNickname")

            // 사용 가능한 닉네임입니다 출력
            binding.signupNicknameDuplicateValidTv.visibility = View.VISIBLE
            binding.signupNicknameValidIv.visibility = View.VISIBLE

            binding.signupNicknameRule1Tv.visibility = View.GONE
            binding.signupNicknameRule2Tv.visibility = View.GONE
            binding.signupNicknameErrorIv.visibility = View.GONE

            // edittext 배경 변경
            binding.signupNicknameEt.setBackgroundResource(R.drawable.basic_outline_black)

            changeButton()
        }
    }

    override fun getNicknameCheckFailure(code: Int, message: String) {
        when(code){
            400-> Log.d("SignUpActivity/NicknameCheck/API", message)
        }
    }

    fun changeButton() {
        if (validCurrentNickname && validNickname && validGroup) {
            // 버튼 선택 가능
            binding.signupTypingDoneNoSelectIv.setImageResource(R.drawable.ic_typing_done_select)
            binding.signupTypingDoneNoSelectIv.setOnClickListener {
                // intent로 사용자 정보 (email, name, nickname, group) 넘겨주기
                val intent = Intent(this, SignUpCategoryActivity::class.java)
                intent.putExtra("email", email)
                intent.putExtra("name", name)
                intent.putExtra("nickname", nickname)
                intent.putExtra("group", group)
                startActivity(intent)
                finish()
            }
        } else {
            // 버튼 선택 불가능
            binding.signupTypingDoneNoSelectIv.setImageResource(R.drawable.ic_typing_done_no_select)
        }
    }

    fun CloseKeyboard() {
        var view = this.currentFocus

        if(view != null) {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}
