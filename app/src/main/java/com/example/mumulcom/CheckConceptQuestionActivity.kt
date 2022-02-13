package com.example.mumulcom

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.example.mumulcom.databinding.ActivityCheckconceptquestionBinding


class CheckConceptQuestionActivity:AppCompatActivity() {

    lateinit var binding: ActivityCheckconceptquestionBinding

    // private lateinit var getResultText: ActivityResultLauncher<Intent>
    private var albumDatas=ArrayList<Album>()//뷰페이저

    private var realUri: String=""

    private var bigCategory: String? = null    // 선택한 상위 카테고리
    private var smallCategory: String? = null  // 선택한 하위 카테고리

    // 스피너 어댑터
    private lateinit var bigCategoryAdapter: ArrayAdapter<String>
    private lateinit var smallCategoryAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCheckconceptquestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 카테고리 초기화
        setupBigCategorySpinner()
        setupBigCategorySpinnerHandler(1)

        binding.checkconceptquestionBackIv.setOnClickListener {
            startActivity(Intent(this, QuestionCategoryActivity::class.java))
        }

        binding.checkconceptquestionPlusIv.setOnClickListener {
            startActivity(Intent(this, ConceptCameraShootingActivity::class.java))
        }
        binding.checkconceptquestionQuestionIv.setOnClickListener {
            required()
        }

//        binding.checkconceptquestionBackIv.setOnClickListener {
//            if (getIntent().getExtras() != null) { //if  has value pass from A
//                val image = intent.extras!!.getString("image")
//                if (image != null) {
//                    val imageUri = Uri.parse(image)
//                } else {
//                    Toast.makeText(getApplication(), "null", Toast.LENGTH_SHORT).show();
//                }
//                Log.d("u4", intent.getStringExtra("image").toString())
//                Log.d("u5", Uri.parse(intent.getStringExtra("image")).toString())
//            }
//        }


        //임시뷰페이저
        albumDatas.apply {
//            if (realUri!=null) {
//                add(Album(Uri.parse(intent.getStringExtra(realUri))))
//                Log.d("real", (intent.getStringExtra(realUri).toString()))
//            }
//            add(Album(com.example.mumulcom.R.drawable.ic_edit))
//            add(Album(com.example.mumulcom.R.drawable.ic_edit))
//            add(Album(com.example.mumulcom.R.drawable.ic_edit))
//            add(Album(com.example.mumulcom.R.drawable.ic_edit))
//            add(Album(com.example.mumulcom.R.drawable.ic_edit))
        }

        val ViewPagerAdapter = ViewPagerAdapter(albumDatas)
        binding.checkconceptquestionVp.adapter = ViewPagerAdapter// 뷰페이저 어댑터 생성
        binding.checkconceptquestionVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL // 방향을 가로로
        binding.checkconceptIndicator.setViewPager(binding.checkconceptquestionVp)
        binding.checkconceptIndicator.createIndicators(5, 0);


    }


    private fun required() {
        /*if (selectDatas.isEmpty()||selectBottomDatas.isEmpty()){
            Toast.makeText(this, "카테고리를 선택해주세요.", Toast.LENGTH_SHORT).show()

            return
        }*/
        if (binding.checkconceptquestionTitleTextEt.text.isEmpty()) {

            Toast.makeText(this, "제목을 작성해주세요.", Toast.LENGTH_SHORT).show()

            return
        }
        if (binding.checkconceptquestionCuriousconceptEt.text.isEmpty()) {

            Toast.makeText(this, "궁금한 개념을 작성해주세요.", Toast.LENGTH_SHORT).show()

            return
        }
        binding.checkconceptquestionQuestionIv.setImageResource(com.example.mumulcom.R.drawable.ic_click_question)
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
    private fun setupBigCategorySpinnerHandler(type: Int) {
        binding.checkconceptquestionBigCategorySp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                binding.checkconceptquestionBigCategorySp.setBackgroundResource(R.drawable.bg_category_outline)
                binding.checkconceptquestionSmallCategorySp.isEnabled = true

                when(type) {
                    1 -> {
                        if (binding.checkconceptquestionBigCategorySp.getItemAtPosition(position).equals("앱")) {
                            selectedCategory(position)
                            binding.checkconceptquestionSmallCategorySp.visibility = View.VISIBLE
                            // 앱 하위 카테고리 (안드로이드, iOS)
                            val smallCategoryAppArray = resources.getStringArray(R.array.small_category_app)
                            setupSmallCategorySpinner(smallCategoryAppArray)
                            setupSmallCategorySpinnerHandler(1)
                        }

                        else if (binding.checkconceptquestionBigCategorySp.getItemAtPosition(position).equals("웹")) {
                            selectedCategory(position)
                            binding.checkconceptquestionSmallCategorySp.visibility = View.VISIBLE
                            // 웹 하위 카테고리 (HTML, CSS, React)
                            val smallCategoryWebArray = resources.getStringArray(R.array.small_category_web)
                            setupSmallCategorySpinner(smallCategoryWebArray)
                            setupSmallCategorySpinnerHandler(1)
                        }

                        else if (binding.checkconceptquestionBigCategorySp.getItemAtPosition(position).equals("서버")) {
                            selectedCategory(position)
                            binding.checkconceptquestionSmallCategorySp.visibility = View.VISIBLE
                            // 서버 하위 카테고리 (Node.js, Spring)
                            val smallCategoryServerArray = resources.getStringArray(R.array.small_category_server)
                            setupSmallCategorySpinner(smallCategoryServerArray)
                            setupSmallCategorySpinnerHandler(1)
                        }

                        else if (binding.checkconceptquestionBigCategorySp.getItemAtPosition(position).equals("프로그래밍 언어")) {
                            selectedCategory(position)
                            binding.checkconceptquestionSmallCategorySp.visibility = View.VISIBLE
                            // 프로그래밍 언어 하위 카테고리 (C, C++, JavaScript, Java, Python)
                            val smallCategoryProgramingArray = resources.getStringArray(R.array.small_category_programing)
                            setupSmallCategorySpinner(smallCategoryProgramingArray)
                            setupSmallCategorySpinnerHandler(1)
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
                            setupSmallCategorySpinnerHandler(1)
                            // 하위 카테고리 스피너 사용 불가
                            binding.checkconceptquestionSmallCategorySp.isEnabled = false
                        }
                    }

                    2 -> {
                        if (binding.checkconceptquestionBigCategorySp.getItemAtPosition(position).equals("앱")) {
                            selectedCategory(position)
                            binding.checkconceptquestionSmallCategorySp.visibility = View.VISIBLE
                            // 앱 하위 카테고리 (안드로이드, iOS)
                            val smallCategoryAppArray = resources.getStringArray(R.array.small_category_app)
                            setupSmallCategorySpinner(smallCategoryAppArray)
                            setupSmallCategorySpinnerHandler(2)
                        }

                        else if (binding.checkconceptquestionBigCategorySp.getItemAtPosition(position).equals("웹")) {
                            selectedCategory(position)
                            binding.checkconceptquestionSmallCategorySp.visibility = View.VISIBLE
                            // 웹 하위 카테고리 (HTML, CSS, React)
                            val smallCategoryWebArray = resources.getStringArray(R.array.small_category_web)
                            setupSmallCategorySpinner(smallCategoryWebArray)
                            setupSmallCategorySpinnerHandler(2)
                        }

                        else if (binding.checkconceptquestionBigCategorySp.getItemAtPosition(position).equals("서버")) {
                            selectedCategory(position)
                            binding.checkconceptquestionSmallCategorySp.visibility = View.VISIBLE
                            // 서버 하위 카테고리 (Node.js, Spring)
                            val smallCategoryServerArray = resources.getStringArray(R.array.small_category_server)
                            setupSmallCategorySpinner(smallCategoryServerArray)
                            setupSmallCategorySpinnerHandler(2)
                        }

                        else if (binding.checkconceptquestionBigCategorySp.getItemAtPosition(position).equals("프로그래밍 언어")) {
                            selectedCategory(position)
                            binding.checkconceptquestionSmallCategorySp.visibility = View.VISIBLE
                            // 프로그래밍 언어 하위 카테고리 (C, C++, JavaScript, Java, Python)
                            val smallCategoryProgramingArray = resources.getStringArray(R.array.small_category_programing)
                            setupSmallCategorySpinner(smallCategoryProgramingArray)
                            setupSmallCategorySpinnerHandler(2)
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
                            setupSmallCategorySpinnerHandler(2)
                            // 하위 카테고리 스피너 사용 불가
                            binding.checkconceptquestionSmallCategorySp.isEnabled = false
                        }
                    }
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
    private fun setupSmallCategorySpinnerHandler(type: Int) {
        binding.checkconceptquestionSmallCategorySp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                binding.checkconceptquestionSmallCategorySp.setBackgroundResource(R.drawable.bg_category_outline)

                when(type) {
                    1 -> {
                        if (!binding.checkconceptquestionSmallCategorySp.getItemAtPosition(position).equals("하위 선택")) {
                            // 하위 카테고리 선택하면 배경 변경
                            binding.checkconceptquestionSmallCategorySp.setBackgroundResource(R.drawable.bg_category_selected)
                            // SmallCategory 변수에 하위 카테고리 저장하기
                            smallCategory = binding.checkconceptquestionSmallCategorySp.getItemAtPosition(position).toString()
                            Log.i(ContentValues.TAG, "하위 카테고리 확인: $smallCategory")
                        } else {
                            smallCategory = null
                            Log.i(ContentValues.TAG, "하위 카테고리 확인: $smallCategory")
                            // Toast.makeText(context, "상위 카테고리를 선택해주세요!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    2 -> {
                        if (!binding.checkconceptquestionSmallCategorySp.getItemAtPosition(position).equals("하위 선택")) {
                            // 하위 카테고리 선택하면 배경 변경
                            binding.checkconceptquestionSmallCategorySp.setBackgroundResource(R.drawable.bg_category_selected)
                            // SmallCategory 변수에 하위 카테고리 저장하기
                            smallCategory = binding.checkconceptquestionSmallCategorySp.getItemAtPosition(position).toString()
                            Log.i(ContentValues.TAG, "하위 카테고리 확인: $smallCategory")
                        } else {
                            smallCategory = null
                            Log.i(ContentValues.TAG, "하위 카테고리 확인: $smallCategory")
                            // Toast.makeText(context, "상위 카테고리를 선택해주세요!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }
}