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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mumulcom.databinding.ActivityProfileModifyBinding
import java.util.regex.Pattern

class ProfileModifyActivity : AppCompatActivity(), View.OnClickListener, SignupNicknameView, ProfileModifyView {
    lateinit var binding: ActivityProfileModifyBinding

    private var jwt: String = ""    // jwt
    private var userIdx: Long = 0   // 유저 인덱스
    private var nickname: String = ""   // 유저 닉네임
    private var group: String = ""  // 유저 소속
    private var myCategories: MutableList<String> = mutableListOf() // 유저 카테고리
    private var profileImgUrl: String = ""  // 유저 프로필 이미지

    private var validCurrentNickname: Boolean = true   // 현재 입력한 아이디가 유효한가
    private var validNickname: Boolean = true   // 중복이 아닌 아이디인가

    private var initPosition: Int = -1
    private var categories: String? = null

    private val nicknameValidation = "^[가-힣a-z0-9]{2,8}$"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileModifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SharedPreference에 저장한 정보 불러오기
        jwt = getJwt(this)
        userIdx = getUserIdx(this)
        nickname = getNickname(this)
        group = getGroup(this)
        myCategories = getCategories(this)
        profileImgUrl = getProfileImgUrl(this)

        // 기존 유저 정보 설정
        initView()

        // 닉네임 바로바로 검사
        binding.profileModifyNicknameEt.addTextChangedListener(object: TextWatcher {
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
                checkNickname() // 닉네임 유효성 검사 함수
                nickname = p0.toString()

                // edittext 배경도 회색으로 다시 변경
                binding.profileModifyNicknameEt.setBackgroundResource(R.drawable.basic_outline_gray)
            }
        })

        // 닉네임 중복 검사 (중복확인버튼)
        binding.profileModifyDuplicateCheckBt.setOnClickListener {
            if (validCurrentNickname) { // 닉네임 유효할때만 중복 검사
                // 중복 확인
                getNicknameCheck()
                // 키보드 사라지기
                closeKeyboard()
            } else {
                Toast.makeText(this, "닉네임을 확인해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        // 카테고리 클릭 이벤트
        binding.profileModifyCategoryAndroidBt.setOnClickListener(this)
        binding.profileModifyCategoryIosBt.setOnClickListener(this)
        binding.profileModifyCategoryHtmlBt.setOnClickListener(this)
        binding.profileModifyCategoryCssBt.setOnClickListener(this)
        binding.profileModifyCategoryReactBt.setOnClickListener(this)
        binding.profileModifyCategoryNodejsBt.setOnClickListener(this)
        binding.profileModifyCategorySpringBt.setOnClickListener(this)
        binding.profileModifyCategoryCBt.setOnClickListener(this)
        binding.profileModifyCategoryCppBt.setOnClickListener(this)
        binding.profileModifyCategoryJsBt.setOnClickListener(this)
        binding.profileModifyCategoryJavaBt.setOnClickListener(this)
        binding.profileModifyCategoryPythonBt.setOnClickListener(this)

        // 화면 배경 누르면 키보드 사라지기
        binding.profileModifyLy.setOnClickListener {
            closeKeyboard()
        }

        // 뒤로 가기 버튼 클릭
        binding.profileModifyBackIv.setOnClickListener {
            onBackPressed()
        }

        // 저장 버튼 클릭 -> 프로필 프래그먼트로 이동
        binding.profileModifySaveIv.setOnClickListener {
            setProfile()
        }
    }

    private fun initView() {
        // 기존 유저 닉네임 & 이미 검증 받은 닉네임이기 때문에 검은색 테두리
        binding.profileModifyNicknameEt.setText(nickname)
        binding.profileModifyNicknameEt.setBackgroundResource(R.drawable.basic_outline_black)

        // 기존 유저 소속 포지션 설정 & 스피너 설정
        when (group) {
            "청소년" -> initPosition = 0
            "대학생" -> initPosition = 1
            "대학원생" -> initPosition = 2
            "직장인" -> initPosition = 3
            "취준생" -> initPosition = 4
            "기타" -> initPosition = 5
        }
        setupSpinner(initPosition)
        setupSpinnerHandler()

        // 기존 유저 관심분야
        if (myCategories.contains("안드로이드")) binding.profileModifyCategoryAndroidBt.isSelected = true
        if (myCategories.contains("iOS")) binding.profileModifyCategoryIosBt.isSelected = true
        if (myCategories.contains("HTML")) binding.profileModifyCategoryHtmlBt.isSelected = true
        if (myCategories.contains("CSS")) binding.profileModifyCategoryCssBt.isSelected = true
        if (myCategories.contains("React")) binding.profileModifyCategoryReactBt.isSelected = true
        if (myCategories.contains("Node.js")) binding.profileModifyCategoryNodejsBt.isSelected = true
        if (myCategories.contains("Spring")) binding.profileModifyCategorySpringBt.isSelected = true
        if (myCategories.contains("C")) binding.profileModifyCategoryCBt.isSelected = true
        if (myCategories.contains("C++")) binding.profileModifyCategoryCppBt.isSelected = true
        if (myCategories.contains("JavaScript")) binding.profileModifyCategoryJsBt.isSelected = true
        if (myCategories.contains("Java")) binding.profileModifyCategoryJavaBt.isSelected = true
        if (myCategories.contains("Python")) binding.profileModifyCategoryPythonBt.isSelected = true

        // 기존 유저 프로필
        when {
            profileImgUrl.contains("1.png") -> {
                binding.profileModifyCharacter1.setImageResource(R.drawable.ic_profile_character_1_select)
            }
            profileImgUrl.contains("2.png") -> {
                binding.profileModifyCharacter2.setImageResource(R.drawable.ic_profile_character_2_select)
            }
            profileImgUrl.contains("3.png") -> {
                binding.profileModifyCharacter3.setImageResource(R.drawable.ic_profile_character_3_select)
            }
            profileImgUrl.contains("4.png") -> {
                binding.profileModifyCharacter4.setImageResource(R.drawable.ic_profile_character_4_select)
            }
            profileImgUrl.contains("5.png") -> {
                binding.profileModifyCharacter5.setImageResource(R.drawable.ic_profile_character_5_select)
            }
            profileImgUrl.contains("6.png") -> {
                binding.profileModifyCharacter6.setImageResource(R.drawable.ic_profile_character_6_select)
            }
        }
        changeCharacter()
    }

    // 닉네임 유효성 검사 함수
    fun checkNickname(): Boolean {
        val n = binding.profileModifyNicknameEt.text.toString().trim() // 공백제거
        val p = Pattern.matches(nicknameValidation, n) // 패턴 맞는지 확인
        return if (p) {
            // 닉네임 형태가 패턴에 적합할 경우
            binding.profileModifyNicknameRule1Tv.visibility = View.VISIBLE
            binding.profileModifyNicknameRule2Tv.visibility = View.VISIBLE
            binding.profileModifyNicknameValidIv.visibility = View.VISIBLE

            binding.profileModifyNicknameErrorTv.visibility = View.GONE
            binding.profileModifyNicknameErrorIv.visibility = View.GONE
            binding.profileModifyNicknameDuplicateValidTv.visibility = View.GONE
            binding.profileModifyNicknameDuplicateErrorTv.visibility = View.GONE
            validCurrentNickname = true
            true
        } else {
            // 닉네임 형태가 패턴에 적합하지 않을 경우
            binding.profileModifyNicknameErrorTv.visibility = View.VISIBLE
            binding.profileModifyNicknameErrorIv.visibility = View.VISIBLE

            binding.profileModifyNicknameRule1Tv.visibility = View.GONE
            binding.profileModifyNicknameRule2Tv.visibility = View.GONE
            binding.profileModifyNicknameValidIv.visibility = View.GONE
            binding.profileModifyNicknameDuplicateValidTv.visibility = View.GONE
            binding.profileModifyNicknameDuplicateErrorTv.visibility = View.GONE
            validCurrentNickname = false
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

            Log.d(TAG, "닉네임 중복")

            // 중복된 닉네임입니다 출력
            binding.profileModifyNicknameDuplicateErrorTv.visibility = View.VISIBLE
            binding.profileModifyNicknameErrorIv.visibility = View.VISIBLE

            binding.profileModifyNicknameRule1Tv.visibility = View.GONE
            binding.profileModifyNicknameRule2Tv.visibility = View.GONE
            binding.profileModifyNicknameValidIv.visibility = View.GONE
        } else {    // 닉네임 사용 가능
            validNickname = !result // validNickname = true

            Log.d(TAG, "닉네임: $nickname validNickname: $validNickname")

            // 사용 가능한 닉네임입니다 출력
            binding.profileModifyNicknameDuplicateValidTv.visibility = View.VISIBLE
            binding.profileModifyNicknameValidIv.visibility = View.VISIBLE

            binding.profileModifyNicknameRule1Tv.visibility = View.GONE
            binding.profileModifyNicknameRule2Tv.visibility = View.GONE
            binding.profileModifyNicknameErrorIv.visibility = View.GONE

            // edittext 배경 변경
            binding.profileModifyNicknameEt.setBackgroundResource(R.drawable.basic_outline_black)
        }
    }

    override fun getNicknameCheckFailure(code: Int, message: String) {
        when(code){
            400-> Log.d("SignUpActivity/NicknameCheck/API", message)
        }
    }

    // 스피너에 어레이 어댑터 연결
    private fun setupSpinner(initPosition: Int) {
        val groups = resources.getStringArray(R.array.signup_group)
        val adapter = object : ArrayAdapter<String>(this, R.layout.item_spinner){
            @SuppressLint("CutPasteId")
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                return super.getView(position, convertView, parent)
            }
        }
        // 아이템 추가
        adapter.addAll(groups.toMutableList())
        // 어댑터 연결
        binding.profileModifyGroupSp.adapter = adapter

        // 스피너 초기값을 유저 소속으로 지정
        binding.profileModifyGroupSp.setSelection(initPosition)

        // droplist를 스피너와 간격을 두고 나오게 함 -> 아이템 크기 = 125px
        binding.profileModifyGroupSp.dropDownVerticalOffset = dipToPixels(45f).toInt()
    }

    // dp 값을 px 값으로 변환해주는 함수
    private fun dipToPixels(dipValue: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dipValue,
            resources.displayMetrics
        )
    }

    // 스피너 클릭 이벤트 핸들러
    private fun setupSpinnerHandler() {
        binding.profileModifyGroupSp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // 소속을 선택하면 group 변수에 사용자 소속 저장하기
                group = binding.profileModifyGroupSp.getItemAtPosition(position).toString()
                Log.d(TAG, "소속 확인: $group")
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }

    // 카테고리 각 버튼의 클릭 이벤트
    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.profile_modify_category_android_bt -> {
                categories = "안드로이드"
                selectCategories(view, categories)
            }
            R.id.profile_modify_category_ios_bt -> {
                categories = "iOS"
                selectCategories(view, categories)
            }
            R.id.profile_modify_category_html_bt -> {
                categories = "HTML"
                selectCategories(view, categories)
            }
            R.id.profile_modify_category_css_bt -> {
                categories = "CSS"
                selectCategories(view, categories)
            }
            R.id.profile_modify_category_react_bt -> {
                categories = "React"
                selectCategories(view, categories)
            }
            R.id.profile_modify_category_nodejs_bt -> {
                categories = "Node.js"
                selectCategories(view, categories)
            }
            R.id.profile_modify_category_spring_bt -> {
                categories = "Spring"
                selectCategories(view, categories)
            }
            R.id.profile_modify_category_c_bt -> {
                categories = "C"
                selectCategories(view, categories)
            }
            R.id.profile_modify_category_cpp_bt -> {
                categories = "C++"
                selectCategories(view, categories)
            }
            R.id.profile_modify_category_js_bt -> {
                categories = "JavaScript"
                selectCategories(view, categories)
            }
            R.id.profile_modify_category_java_bt -> {
                categories = "Java"
                selectCategories(view, categories)
            }
            R.id.profile_modify_category_python_bt -> {
                categories = "Python"
                selectCategories(view, categories)
            }
        }
    }

    // 카테고리 선택
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
    }

    // 캐릭터 선택
    private fun changeCharacter() {
        binding.profileModifyCharacter1.setOnClickListener {
            profileImgUrl = "https://mumulcom-bucket.s3.ap-northeast-2.amazonaws.com/character/1.png"
            binding.profileModifyCharacter1.setImageResource(R.drawable.ic_profile_character_1_select)
            binding.profileModifyCharacter2.setImageResource(R.drawable.ic_profile_character_2_no_select)
            binding.profileModifyCharacter3.setImageResource(R.drawable.ic_profile_character_3_no_select)
            binding.profileModifyCharacter4.setImageResource(R.drawable.ic_profile_character_4_no_select)
            binding.profileModifyCharacter5.setImageResource(R.drawable.ic_profile_character_5_no_select)
            binding.profileModifyCharacter6.setImageResource(R.drawable.ic_profile_character_6_no_select)
            Log.d("프로필 선택: ", profileImgUrl)
        }
        binding.profileModifyCharacter2.setOnClickListener {
            profileImgUrl = "https://mumulcom-bucket.s3.ap-northeast-2.amazonaws.com/character/2.png"
            binding.profileModifyCharacter1.setImageResource(R.drawable.ic_profile_character_1_no_select)
            binding.profileModifyCharacter2.setImageResource(R.drawable.ic_profile_character_2_select)
            binding.profileModifyCharacter3.setImageResource(R.drawable.ic_profile_character_3_no_select)
            binding.profileModifyCharacter4.setImageResource(R.drawable.ic_profile_character_4_no_select)
            binding.profileModifyCharacter5.setImageResource(R.drawable.ic_profile_character_5_no_select)
            binding.profileModifyCharacter6.setImageResource(R.drawable.ic_profile_character_6_no_select)
            Log.d("프로필 선택: ", profileImgUrl)
        }
        binding.profileModifyCharacter3.setOnClickListener {
            profileImgUrl = "https://mumulcom-bucket.s3.ap-northeast-2.amazonaws.com/character/3.png"
            binding.profileModifyCharacter1.setImageResource(R.drawable.ic_profile_character_1_no_select)
            binding.profileModifyCharacter2.setImageResource(R.drawable.ic_profile_character_2_no_select)
            binding.profileModifyCharacter3.setImageResource(R.drawable.ic_profile_character_3_select)
            binding.profileModifyCharacter4.setImageResource(R.drawable.ic_profile_character_4_no_select)
            binding.profileModifyCharacter5.setImageResource(R.drawable.ic_profile_character_5_no_select)
            binding.profileModifyCharacter6.setImageResource(R.drawable.ic_profile_character_6_no_select)
            Log.d("프로필 선택: ", profileImgUrl)
        }
        binding.profileModifyCharacter4.setOnClickListener {
            profileImgUrl = "https://mumulcom-bucket.s3.ap-northeast-2.amazonaws.com/character/4.png"
            binding.profileModifyCharacter1.setImageResource(R.drawable.ic_profile_character_1_no_select)
            binding.profileModifyCharacter2.setImageResource(R.drawable.ic_profile_character_2_no_select)
            binding.profileModifyCharacter3.setImageResource(R.drawable.ic_profile_character_3_no_select)
            binding.profileModifyCharacter4.setImageResource(R.drawable.ic_profile_character_4_select)
            binding.profileModifyCharacter5.setImageResource(R.drawable.ic_profile_character_5_no_select)
            binding.profileModifyCharacter6.setImageResource(R.drawable.ic_profile_character_6_no_select)
            Log.d("프로필 선택: ", profileImgUrl)
        }
        binding.profileModifyCharacter5.setOnClickListener {
            profileImgUrl = "https://mumulcom-bucket.s3.ap-northeast-2.amazonaws.com/character/5.png"
            binding.profileModifyCharacter1.setImageResource(R.drawable.ic_profile_character_1_no_select)
            binding.profileModifyCharacter2.setImageResource(R.drawable.ic_profile_character_2_no_select)
            binding.profileModifyCharacter3.setImageResource(R.drawable.ic_profile_character_3_no_select)
            binding.profileModifyCharacter4.setImageResource(R.drawable.ic_profile_character_4_no_select)
            binding.profileModifyCharacter5.setImageResource(R.drawable.ic_profile_character_5_select)
            binding.profileModifyCharacter6.setImageResource(R.drawable.ic_profile_character_6_no_select)
            Log.d("프로필 선택: ", profileImgUrl)
        }
        binding.profileModifyCharacter6.setOnClickListener {
            profileImgUrl = "https://mumulcom-bucket.s3.ap-northeast-2.amazonaws.com/character/6.png"
            binding.profileModifyCharacter1.setImageResource(R.drawable.ic_profile_character_1_no_select)
            binding.profileModifyCharacter2.setImageResource(R.drawable.ic_profile_character_2_no_select)
            binding.profileModifyCharacter3.setImageResource(R.drawable.ic_profile_character_3_no_select)
            binding.profileModifyCharacter4.setImageResource(R.drawable.ic_profile_character_4_no_select)
            binding.profileModifyCharacter5.setImageResource(R.drawable.ic_profile_character_5_no_select)
            binding.profileModifyCharacter6.setImageResource(R.drawable.ic_profile_character_6_select)
            Log.d("프로필 선택: ", profileImgUrl)
        }
    }

    private fun closeKeyboard() {
        val view = this.currentFocus
        if(view != null) {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun setProfile() {
        val profileService = ProfileService()
        profileService.setProfileModifyService(this)

        profileService.setProfiles(jwt, userIdx, ProfileModify(userIdx, nickname, group, myCategories, profileImgUrl))
    }

    override fun onSetProfileLoading() {
        Log.d("ProfileModify/API", "프로필 업데이트 중...")
    }

    override fun onSetProfileSuccess(profile: Profile) {
        if (profile != null) {
            Log.d("profileModify/API", "성공")
            saveNickname(this, profile.nickname)
            saveGroup(this, profile.group)
            profile.myCategories?.let { saveCategories(this, it) }
            saveProfileImgUrl(this, profile.profileImgUrl)

            Log.d("nickname", profile.nickname)
            Log.d("group", profile.group)
            Log.d("myCategories", profile.myCategories.toString())
            Log.d("profileImgUrl", profile.profileImgUrl)
        }

        // 메인 액티비티로 넘어가기
        // startProfileActivity()
        finish()
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        finish()
    }

    override fun onSetProfileFailure(code: Int, message: String) {
        when(code){
            400-> Log.d("ProfileModify/API", message)
        }
    }
}
