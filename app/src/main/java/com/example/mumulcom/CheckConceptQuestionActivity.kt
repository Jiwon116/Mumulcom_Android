package com.example.mumulcom

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.mumulcom.databinding.ActivityCheckconceptquestionBinding
import com.example.test.ViewPagerAdapter
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


// CheckConcetpuestionView
class CheckConceptQuestionActivity:AppCompatActivity(), CheckConceptQuestionView {

    lateinit var binding: ActivityCheckconceptquestionBinding

    private var images = arrayListOf<String>()
    var photoList = arrayListOf<Photo>()
    private var jwt: String = ""
    private var userIdx: Long = 0
    private lateinit var title: String
    private lateinit var content: String
    private var bigCategoryIdx: Long = 0
    private var smallCategoryIdx: Long = 0

    private var bigCategory: String? = null    // 선택한 상위 카테고리
    private var smallCategory: String? = null  // 선택한 하위 카테고리

    //뷰페이저+파이어스토리지
    lateinit var viewPagerAdapter: ViewPagerAdapter
    lateinit var storage: FirebaseStorage
    lateinit var firestore: FirebaseFirestore//파이어스토리지
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>//이동(카메라 앨범)

    // 스피너 어댑터
    private lateinit var bigCategoryAdapter: ArrayAdapter<String>
    private lateinit var smallCategoryAdapter: ArrayAdapter<String>

    var count=0//이미지수
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCheckconceptquestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // jwt = getJwt(this)
        // userIdx = getUserIdx(this)
        jwt =
            "eyJ0eXBlIjoiand0IiwiYWxnIjoiSFMyNTYifQ.eyJ1c2VySWR4IjoxMywiaWF0IjoxNjQ1MDM1NDU1LCJleHAiOjE2NDY1MDY2ODR9.GVcX6bK1dpM_x7BD1jEU2-R5LogJ3oG4ulm4yQ-e7jg"
        userIdx = 13
        Log.d("jwt", jwt)

        storage = FirebaseStorage.getInstance()
        firestore = FirebaseFirestore.getInstance()
        // 카테고리 초기화
        setupBigCategorySpinner()
        setupBigCategorySpinnerHandler()

        // 화면 배경 누르면 키보드 사라지기
        binding.conceptBack.setOnClickListener {
            CloseKeyboard()
        }

        //startActivityresult대신
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            }

// 뷰페이저 어댑터 생성
        viewPagerAdapter = ViewPagerAdapter(this, photoList)
        binding.checkconceptquestionVp.adapter = viewPagerAdapter
        binding.checkconceptquestionVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL // 방향을 가로로
        binding.checkconceptIndicator.setViewPager(binding.checkconceptquestionVp)
        binding.checkconceptIndicator.createIndicators(5, 0)

        //이미지 5개 이하일대만 이동가능
        if (count<5){
            //추가버튼
            binding.checkconceptquestionPlusIv.setOnClickListener {
                val intent =
                    Intent(this, ConceptCameraShootingActivity::class.java)
                activityResultLauncher.launch(intent)
            }
        }
        //리스트에 추가
//        //5개이하까지만 이미지 삽입?
        firestore.collection("concept-images").addSnapshotListener {
                querySnapshot, FirebaseFIrestoreException ->
            if(querySnapshot!=null){
                for(dc in querySnapshot.documentChanges){
                    if(dc.type== DocumentChange.Type.ADDED){
                        var photo=dc.document.toObject(Photo::class.java)
                        photoList.add(photo)
                        images.add(photo.imageUrl)
                        count++
                        Log.d("count", count.toString())
                        if (count>=5){
                            //추가버튼
                            binding.checkconceptquestionPlusIv.setOnClickListener {
                                Toast.makeText(this, "이미지는 최대 5개까지 담을 수 있습니다", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                viewPagerAdapter.notifyDataSetChanged()
            }
//            images= listOf(images.toString())
//            Log.d("PHOTO/images-list=cc", images.toString())
        }

        binding.checkconceptquestionBackIv.setOnClickListener {
            startActivity(Intent(this, QuestionCategoryActivity::class.java))
            finish()
        }

        binding.checkconceptquestionQuestionIv.setOnClickListener {
            checkConceptQuestion()
            finish()
        }
    }


    private fun getConcept(): CheckConcept {  // view에서 받은 값들

        title=binding.checkconceptquestionTitleTextEt.text.toString()
        bigCategoryIdx=binding.checkconceptquestionBigCategorySp.selectedItemPosition.toLong()+1
        content=binding.checkconceptquestionCuriousconceptEt.text.toString()

        Log.d("images", images.toString())
        Log.d("userIdx : ", userIdx.toString())
        Log.d("title : ", title)
        Log.d("content : ", content)
        Log.d("bigCategoryIdx : ", bigCategoryIdx.toString())
        Log.d("smallCategoryIdx :", smallCategoryIdx.toString())
        return CheckConcept(images, userIdx, bigCategoryIdx, smallCategoryIdx, title, content)
    }


    private fun checkConceptQuestion(
    ) {
        if (binding.checkconceptquestionSmallCategorySp.isEnabled() == false) {
            Toast.makeText(this, "카테고리를 선택해주세요.", Toast.LENGTH_SHORT).show()

            return
        }
        if (binding.checkconceptquestionTitleTextEt.text.isEmpty()) {

            Toast.makeText(this, "제목을 작성해주세요.", Toast.LENGTH_SHORT).show()

            return
        }
        if (binding.checkconceptquestionCuriousconceptEt.text.isEmpty()) {

            Toast.makeText(this, "궁금한 부분을 작성해주세요.", Toast.LENGTH_SHORT).show()

            return
        }
        binding.checkconceptquestionQuestionIv.setImageResource(R.drawable.ic_click_question)
        val checkConceptQuestionService=CheckConceptQuestionService()

        checkConceptQuestionService.setcheckconceptquestionView(this)

        checkConceptQuestionService.checkConceptQuestion(jwt, getConcept())
        Log.d("CHECKCONCEPT/API","Hello")
    }

    // 키보드 사라지는 함수
    fun CloseKeyboard() {
        var view = this.currentFocus

        if (view != null) {
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }








    /********************* 스피너 ********************/
    // dp 값을 px 값으로 변환해주는 함수
    private fun dipToPixels(dipValue: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dipValue,
            resources.displayMetrics
        )
    }

    // big category spinner에 arrayadapter 연결
    private fun setupBigCategorySpinner() {
        // 상위 카테고리들 (앱, 웹, 서버, 프로그래밍 언어, 기타)
        val bigCategoryArray = resources.getStringArray(R.array.big_category)
        bigCategoryAdapter = object : ArrayAdapter<String>(this, R.layout.item_big_category){
            @SuppressLint("CutPasteId")
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)
                if(position == count) {
                    // 마지막 포지션의 textView를 힌트용으로 사용
                    (v.findViewById<View>(R.id.item_big_category_tv) as TextView).text = ""
                    // 아이템의 마지막 값을 불러와 hint로 추가함
                    (v.findViewById<View>(R.id.item_big_category_tv) as TextView).hint = getItem(count)
                }
                return v
            }
            override fun getCount(): Int {
                // 마지막 아이템은 hint용이기 때문에 1을 빼줌
                return super.getCount() - 1
            }
        }
        // 아이템 추가
        bigCategoryAdapter.addAll(bigCategoryArray.toMutableList())
        // hint로 사용할 문구를 마지막 아이템에 추가
        bigCategoryAdapter.add("상위 선택")
        // 어댑터 연결
        binding.checkconceptquestionBigCategorySp.adapter = bigCategoryAdapter
        // 스피너 초기값을 마지막 아이템으로 설정
        binding.checkconceptquestionBigCategorySp.setSelection(bigCategoryAdapter.count)

        // droplist를 스피너와 간격을 두고 나오게 함 -> 아이템 크기 = 125px
        binding.checkconceptquestionBigCategorySp.dropDownVerticalOffset = dipToPixels(45f).toInt()
    }

    // 스피너 클릭 이벤트 핸들러
    private fun setupBigCategorySpinnerHandler() {
        binding.checkconceptquestionBigCategorySp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                binding.checkconceptquestionBigCategorySp.setBackgroundResource(R.drawable.bg_category_outline)
                binding.checkconceptquestionSmallCategorySp.isEnabled = true

                if (binding.checkconceptquestionBigCategorySp.getItemAtPosition(position).equals("앱")) {
                    selectedCategory(position)
                    binding.checkconceptquestionSmallCategorySp.visibility = View.VISIBLE
                    // 앱 하위 카테고리 (안드로이드, iOS)
                    val smallCategoryAppArray = resources.getStringArray(R.array.small_category_app)
                    setupSmallCategorySpinner(smallCategoryAppArray)
                    setupSmallCategorySpinnerHandler()
                }

                else if (binding.checkconceptquestionBigCategorySp.getItemAtPosition(position).equals("웹")) {
                    selectedCategory(position)
                    binding.checkconceptquestionSmallCategorySp.visibility = View.VISIBLE
                    // 웹 하위 카테고리 (HTML, CSS, React)
                    val smallCategoryWebArray = resources.getStringArray(R.array.small_category_web)
                    setupSmallCategorySpinner(smallCategoryWebArray)
                    setupSmallCategorySpinnerHandler()
                }

                else if (binding.checkconceptquestionBigCategorySp.getItemAtPosition(position).equals("서버")) {
                    selectedCategory(position)
                    binding.checkconceptquestionSmallCategorySp.visibility = View.VISIBLE
                    // 서버 하위 카테고리 (Node.js, Spring)
                    val smallCategoryServerArray = resources.getStringArray(R.array.small_category_server)
                    setupSmallCategorySpinner(smallCategoryServerArray)
                    setupSmallCategorySpinnerHandler()
                }

                else if (binding.checkconceptquestionBigCategorySp.getItemAtPosition(position).equals("프로그래밍 언어")) {
                    selectedCategory(position)
                    binding.checkconceptquestionSmallCategorySp.visibility = View.VISIBLE
                    // 프로그래밍 언어 하위 카테고리 (C, C++, JavaScript, Java, Python)
                    val smallCategoryProgramingArray = resources.getStringArray(R.array.small_category_programing)
                    setupSmallCategorySpinner(smallCategoryProgramingArray)
                    setupSmallCategorySpinnerHandler()
                }

                else if (binding.checkconceptquestionBigCategorySp.getItemAtPosition(position).equals("기타")) {
                    selectedCategory(position)
                    // 기타 선택 시 하위 선택 스피너 사라짐
                    binding.checkconceptquestionSmallCategorySp.visibility = View.GONE
                    // 하위 카테고리에 null 값
                    smallCategory = null
                }

                else if (binding.checkconceptquestionBigCategorySp.getItemAtPosition(position).equals("상위 선택")) {
                    bigCategory = null
                    Log.i(ContentValues.TAG, "상위 카테고리 확인: $bigCategory")
                    binding.checkconceptquestionSmallCategorySp.visibility = View.VISIBLE
                    // 하위 선택 뜨기
                    val smallCategoryArray = resources.getStringArray(R.array.small_category)
                    setupSmallCategorySpinner(smallCategoryArray)
                    setupSmallCategorySpinnerHandler()
                    // 하위 카테고리 스피너 사용 불가
                    binding.checkconceptquestionSmallCategorySp.isEnabled = false
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }

    private fun selectedCategory(position: Int) {
        // 상위 선택하면 배경 변경
        binding.checkconceptquestionBigCategorySp.setBackgroundResource(R.drawable.bg_category_selected)
        // bigCategory 변수에 상위 카테고리 저장하기
        bigCategory = binding.checkconceptquestionBigCategorySp.getItemAtPosition(position).toString()
        Log.i(ContentValues.TAG, "상위 카테고리 확인: $bigCategory")
    }



    // big category spinner에 arrayadapter 연결
    private fun setupSmallCategorySpinner(array: Array<String>) {
        smallCategoryAdapter = object : ArrayAdapter<String>(this, R.layout.item_small_category){
            @SuppressLint("CutPasteId")
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)
                if(position == count) {
                    // 마지막 포지션의 textView를 힌트용으로 사용
                    (v.findViewById<View>(R.id.item_small_category_tv) as TextView).text = ""
                    // 아이템의 마지막 값을 불러와 hint로 추가함
                    (v.findViewById<View>(R.id.item_small_category_tv) as TextView).hint = getItem(count)
                }
                return v
            }
            override fun getCount(): Int {
                // 마지막 아이템은 hint용이기 때문에 1을 빼줌
                return super.getCount() - 1
            }
        }
        // 아이템 추가
        smallCategoryAdapter.addAll(array.toMutableList())
        // hint로 사용할 문구를 마지막 아이템에 추가
        smallCategoryAdapter.add("하위 선택")
        // 어댑터 연결
        binding.checkconceptquestionSmallCategorySp.adapter = smallCategoryAdapter
        // 스피너 초기값을 마지막 아이템으로 설정
        binding.checkconceptquestionSmallCategorySp.setSelection(smallCategoryAdapter.count)

        // droplist를 스피너와 간격을 두고 나오게 함 -> 아이템 크기 = 125px
        binding.checkconceptquestionSmallCategorySp.dropDownVerticalOffset = dipToPixels(45f).toInt()
    }

    // 스피너 클릭 이벤트 핸들러
    private fun setupSmallCategorySpinnerHandler() {
        binding.checkconceptquestionSmallCategorySp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                binding.checkconceptquestionSmallCategorySp.setBackgroundResource(R.drawable.bg_category_outline)
                if (!binding.checkconceptquestionSmallCategorySp.getItemAtPosition(position).equals("하위 선택")) {
                    // 하위 카테고리 선택하면 배경 변경
                    binding.checkconceptquestionSmallCategorySp.setBackgroundResource(R.drawable.bg_category_selected)
                    // SmallCategory 변수에 하위 카테고리 저장하기
                    smallCategory = binding.checkconceptquestionSmallCategorySp.getItemAtPosition(position).toString()
                    Log.i(ContentValues.TAG, "하위 카테고리 확인: $smallCategory")
                    if (bigCategory=="앱") {
                        smallCategoryIdx =
                            binding.checkconceptquestionSmallCategorySp.selectedItemPosition.toLong() + 1
                        Log.i(ContentValues.TAG, "하위 카테고리 넘버 확인: $smallCategoryIdx")
                    }
                    if (bigCategory=="웹") {
                        smallCategoryIdx =
                            binding.checkconceptquestionSmallCategorySp.selectedItemPosition.toLong() + 3
                        Log.i(ContentValues.TAG, "하위 카테고리 넘버 확인: $smallCategoryIdx")
                    }
                    if (bigCategory=="서버") {
                        smallCategoryIdx =
                            binding.checkconceptquestionSmallCategorySp.selectedItemPosition.toLong() + 6
                        Log.i(ContentValues.TAG, "하위 카테고리 넘버 확인: $smallCategoryIdx")
                    }
                    if (bigCategory=="프로그래밍 언어") {
                        smallCategoryIdx =
                            binding.checkconceptquestionSmallCategorySp.selectedItemPosition.toLong() + 8
                        Log.i(ContentValues.TAG, "하위 카테고리 넘버 확인: $smallCategoryIdx")
                    }
                    if (bigCategory=="기타") {
                        smallCategoryIdx =binding.checkconceptquestionSmallCategorySp.selectedItemPosition.toLong()
                        Log.i(ContentValues.TAG, "하위 카테고리 넘버 확인: $smallCategoryIdx")
                    }
                } else {
                    smallCategory = null
                    Log.i(ContentValues.TAG, "하위 카테고리 확인: $smallCategory")
                    // Toast.makeText(context, "상위 카테고리를 선택해주세요!", Toast.LENGTH_SHORT).show()
                }

            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }

    override fun onCheckConceptQuestionLoading() {
        Toast.makeText(this, "잠시만 기다려주세요", Toast.LENGTH_SHORT).show()
    }

    override fun onCheckConceptQuestionFailure(code: Int, message: String) {
        Toast.makeText(this, "실패", Toast.LENGTH_SHORT).show()
    }

    override fun onCheckConceptQuestionSuccess(result: String) {
        Toast.makeText(this, "성공", Toast.LENGTH_SHORT).show()

        finish()
    }


}
