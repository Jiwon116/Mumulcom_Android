package com.example.mumulcom

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mumulcom.databinding.FragmentScrapBinding


class ScrapFragment : Fragment(), ScrapCodingView, ScrapConceptView {
    lateinit var binding: FragmentScrapBinding

    private var jwt: String = ""    // jwt
    private var userIdx: Long = 0   // 유저 인덱스
    private var isReplied: Boolean = false  // 답변 달린 질문만 보기 -> true / 전체 질문 보기 -> false
    private var bigCategory: String? = null    // 선택한 상위 카테고리
    private var smallCategory: String? = null  // 선택한 하위 카테고리

    // 리사이클러뷰 어댑터
    private lateinit var scrapAdapter: ScrapAdapter
    private lateinit var scrapConceptAdapter: ScrapAdapter

    // 스피너 어댑터
    private lateinit var bigCategoryAdapter: ArrayAdapter<String>
    private lateinit var smallCategoryAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScrapBinding.inflate(inflater, container, false)

        jwt = context?.let { getJwt(it) }.toString()
        userIdx = context?.let { getUserIdx(it) }!!

        // default값은 코딩 질문
        initCodingRecyclerView()
        checkCodingReplied()

        // 카테고리 초기화
        setupBigCategorySpinner()
        setupBigCategorySpinnerHandler(1)   // 코딩 질문 -> 1 / 개념 질문 -> 2

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        binding.scrapRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.scrap_coding_btn -> {
                    initCodingRecyclerView()
                    checkCodingReplied()

                    // 카테고리 초기화
                    setupBigCategorySpinner()
                    setupBigCategorySpinnerHandler(1)
                }
                R.id.scrap_concept_btn -> {
                    initConceptRecyclerView()
                    checkConceptReplied()

                    // 카테고리 초기화
                    setupBigCategorySpinner()
                    setupBigCategorySpinnerHandler(2)
                }
            }
        }
    }

    /********************* 리사이클러뷰 ********************/
    private fun initCodingRecyclerView() {
        getScrapCodingQuestion(jwt, userIdx, isReplied, bigCategory, smallCategory)
        scrapAdapter = ScrapAdapter(context)
        scrapAdapter.setScrapClickListener(object: ScrapAdapter.ScrapClickListener {
            override fun onItemClick(question: Question) {
                startCodingQuestionDetailActivity(question)// 질문 상세 보기 페이지로 이동
            }
        })
        binding.scrapResultRv.adapter = scrapAdapter
        binding.scrapResultRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
    }

    private fun startCodingQuestionDetailActivity(question: Question){
        val intent = Intent(requireContext(), QuestionDetailActivity::class.java)
        intent.putExtra("bigCategoryName", question.bigCategoryName) // 상위 카테고리명 넘김
        intent.putExtra("questionIdx", question.questionIdx) // 질문 고유 번호 넘김
        intent.putExtra("type", 1)  // 코딩 질문
        startActivity(intent)
    }

    private fun initConceptRecyclerView() {
        getScrapCodingQuestion(jwt, userIdx, isReplied, bigCategory, smallCategory)
        scrapConceptAdapter = ScrapAdapter(context)
        scrapConceptAdapter.setScrapClickListener(object: ScrapAdapter.ScrapClickListener {
            override fun onItemClick(question: Question) {
                startConceptQuestionDetailActivity(question)// 질문 상세 보기 페이지로 이동
            }
        })
        binding.scrapResultRv.adapter = scrapConceptAdapter
        binding.scrapResultRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
    }

    private fun startConceptQuestionDetailActivity(question: Question){
        val intent = Intent(requireContext(), QuestionDetailActivity::class.java)
        intent.putExtra("bigCategoryName", question.bigCategoryName) // 상위 카테고리명 넘김
        intent.putExtra("questionIdx", question.questionIdx) // 질문 고유 번호 넘김
        intent.putExtra("type", 2)  // 개념 질문
        startActivity(intent)
    }

    /********************* 답변 달린 질문 체크 ********************/
    private fun checkCodingReplied() {
        // 답변 단글만 보기
        binding.scrapReplyLy.setOnClickListener {
            isReplied = !isReplied

            if(isReplied){ // 답변 달림
                binding.scrapReplyIv.setImageResource(R.drawable.ic_check_ok)
            }else{
                binding.scrapReplyIv.setImageResource(R.drawable.ic_check_no)
            }

            getScrapCodingQuestion(jwt, userIdx, isReplied, bigCategory, smallCategory)
        }
    }

    private fun checkConceptReplied() {
        // 답변 단글만 보기
        binding.scrapReplyLy.setOnClickListener {
            isReplied = !isReplied

            if(isReplied){ // 답변 달림
                binding.scrapReplyIv.setImageResource(R.drawable.ic_check_ok)
            }else{
                binding.scrapReplyIv.setImageResource(R.drawable.ic_check_no)
            }

            getScrapConceptQuestion(jwt, userIdx, isReplied, bigCategory, smallCategory)
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
        bigCategoryAdapter = object : ArrayAdapter<String>(requireContext(), R.layout.item_big_category){
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
        binding.scrapBigCategorySp.adapter = bigCategoryAdapter
        // 스피너 초기값을 마지막 아이템으로 설정
        binding.scrapBigCategorySp.setSelection(bigCategoryAdapter.count)

        // droplist를 스피너와 간격을 두고 나오게 함 -> 아이템 크기 = 125px
        binding.scrapBigCategorySp.dropDownVerticalOffset = dipToPixels(45f).toInt()
    }

    // 스피너 클릭 이벤트 핸들러
    private fun setupBigCategorySpinnerHandler(type: Int) {
        binding.scrapBigCategorySp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                binding.scrapBigCategorySp.setBackgroundResource(R.drawable.bg_category_outline)
                binding.scrapSmallCategorySp.isEnabled = true

                when(type) {
                    1 -> {
                        if (binding.scrapBigCategorySp.getItemAtPosition(position).equals("앱")) {
                            selectedCategory(position)
                            binding.scrapSmallCategorySp.visibility = View.VISIBLE
                            getScrapCodingQuestion(jwt, userIdx, isReplied, bigCategory, smallCategory)
                            // 앱 하위 카테고리 (안드로이드, iOS)
                            val smallCategoryAppArray = resources.getStringArray(R.array.small_category_app)
                            setupSmallCategorySpinner(smallCategoryAppArray)
                            setupSmallCategorySpinnerHandler(1)
                        }

                        else if (binding.scrapBigCategorySp.getItemAtPosition(position).equals("웹")) {
                            selectedCategory(position)
                            binding.scrapSmallCategorySp.visibility = View.VISIBLE
                            getScrapCodingQuestion(jwt, userIdx, isReplied, bigCategory, smallCategory)
                            // 웹 하위 카테고리 (HTML, CSS, React)
                            val smallCategoryWebArray = resources.getStringArray(R.array.small_category_web)
                            setupSmallCategorySpinner(smallCategoryWebArray)
                            setupSmallCategorySpinnerHandler(1)
                        }

                        else if (binding.scrapBigCategorySp.getItemAtPosition(position).equals("서버")) {
                            selectedCategory(position)
                            binding.scrapSmallCategorySp.visibility = View.VISIBLE
                            getScrapCodingQuestion(jwt, userIdx, isReplied, bigCategory, smallCategory)
                            // 서버 하위 카테고리 (Node.js, Spring)
                            val smallCategoryServerArray = resources.getStringArray(R.array.small_category_server)
                            setupSmallCategorySpinner(smallCategoryServerArray)
                            setupSmallCategorySpinnerHandler(1)
                        }

                        else if (binding.scrapBigCategorySp.getItemAtPosition(position).equals("프로그래밍 언어")) {
                            selectedCategory(position)
                            binding.scrapSmallCategorySp.visibility = View.VISIBLE
                            getScrapCodingQuestion(jwt, userIdx, isReplied, bigCategory, smallCategory)
                            // 프로그래밍 언어 하위 카테고리 (C, C++, JavaScript, Java, Python)
                            val smallCategoryProgramingArray = resources.getStringArray(R.array.small_category_programing)
                            setupSmallCategorySpinner(smallCategoryProgramingArray)
                            setupSmallCategorySpinnerHandler(1)
                        }

                        else if (binding.scrapBigCategorySp.getItemAtPosition(position).equals("기타")) {
                            selectedCategory(position)
                            // 기타 선택 시 하위 선택 스피너 사라짐
                            binding.scrapSmallCategorySp.visibility = View.GONE
                            getScrapCodingQuestion(jwt, userIdx, isReplied, bigCategory, smallCategory)
                            // 하위 카테고리에 null 값
                            smallCategory = null
                        }

                        else if (binding.scrapBigCategorySp.getItemAtPosition(position).equals("상위 선택")) {
                            bigCategory = null
                            Log.i(TAG, "상위 카테고리 확인: $bigCategory")
                            binding.scrapSmallCategorySp.visibility = View.VISIBLE
                            getScrapCodingQuestion(jwt, userIdx, isReplied, bigCategory, smallCategory)
                            // 하위 선택 뜨기
                            val smallCategoryArray = resources.getStringArray(R.array.small_category)
                            setupSmallCategorySpinner(smallCategoryArray)
                            setupSmallCategorySpinnerHandler(1)
                            // 하위 카테고리 스피너 사용 불가
                            binding.scrapSmallCategorySp.isEnabled = false
                        }
                    }

                    2 -> {
                        if (binding.scrapBigCategorySp.getItemAtPosition(position).equals("앱")) {
                            selectedCategory(position)
                            binding.scrapSmallCategorySp.visibility = View.VISIBLE
                            getScrapConceptQuestion(jwt, userIdx, isReplied, bigCategory, smallCategory)
                            // 앱 하위 카테고리 (안드로이드, iOS)
                            val smallCategoryAppArray = resources.getStringArray(R.array.small_category_app)
                            setupSmallCategorySpinner(smallCategoryAppArray)
                            setupSmallCategorySpinnerHandler(2)
                        }

                        else if (binding.scrapBigCategorySp.getItemAtPosition(position).equals("웹")) {
                            selectedCategory(position)
                            binding.scrapSmallCategorySp.visibility = View.VISIBLE
                            getScrapConceptQuestion(jwt, userIdx, isReplied, bigCategory, smallCategory)
                            // 웹 하위 카테고리 (HTML, CSS, React)
                            val smallCategoryWebArray = resources.getStringArray(R.array.small_category_web)
                            setupSmallCategorySpinner(smallCategoryWebArray)
                            setupSmallCategorySpinnerHandler(2)
                        }

                        else if (binding.scrapBigCategorySp.getItemAtPosition(position).equals("서버")) {
                            selectedCategory(position)
                            binding.scrapSmallCategorySp.visibility = View.VISIBLE
                            getScrapConceptQuestion(jwt, userIdx, isReplied, bigCategory, smallCategory)
                            // 서버 하위 카테고리 (Node.js, Spring)
                            val smallCategoryServerArray = resources.getStringArray(R.array.small_category_server)
                            setupSmallCategorySpinner(smallCategoryServerArray)
                            setupSmallCategorySpinnerHandler(2)
                        }

                        else if (binding.scrapBigCategorySp.getItemAtPosition(position).equals("프로그래밍 언어")) {
                            selectedCategory(position)
                            binding.scrapSmallCategorySp.visibility = View.VISIBLE
                            getScrapConceptQuestion(jwt, userIdx, isReplied, bigCategory, smallCategory)
                            // 프로그래밍 언어 하위 카테고리 (C, C++, JavaScript, Java, Python)
                            val smallCategoryProgramingArray = resources.getStringArray(R.array.small_category_programing)
                            setupSmallCategorySpinner(smallCategoryProgramingArray)
                            setupSmallCategorySpinnerHandler(2)
                        }

                        else if (binding.scrapBigCategorySp.getItemAtPosition(position).equals("기타")) {
                            selectedCategory(position)
                            // 기타 선택 시 하위 선택 스피너 사라짐
                            binding.scrapSmallCategorySp.visibility = View.GONE
                            getScrapConceptQuestion(jwt, userIdx, isReplied, bigCategory, smallCategory)
                            // 하위 카테고리에 null 값
                            smallCategory = null
                        }

                        else if (binding.scrapBigCategorySp.getItemAtPosition(position).equals("상위 선택")) {
                            bigCategory = null
                            Log.i(TAG, "상위 카테고리 확인: $bigCategory")
                            binding.scrapSmallCategorySp.visibility = View.VISIBLE
                            getScrapConceptQuestion(jwt, userIdx, isReplied, bigCategory, smallCategory)
                            // 하위 선택 뜨기
                            val smallCategoryArray = resources.getStringArray(R.array.small_category)
                            setupSmallCategorySpinner(smallCategoryArray)
                            setupSmallCategorySpinnerHandler(2)
                            // 하위 카테고리 스피너 사용 불가
                            binding.scrapSmallCategorySp.isEnabled = false
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
        binding.scrapBigCategorySp.setBackgroundResource(R.drawable.bg_category_selected)
        // bigCategory 변수에 상위 카테고리 저장하기
        bigCategory = binding.scrapBigCategorySp.getItemAtPosition(position).toString()
        Log.i(TAG, "상위 카테고리 확인: $bigCategory")
    }



    // big category spinner에 arrayadapter 연결
    private fun setupSmallCategorySpinner(array: Array<String>) {
        smallCategoryAdapter = object : ArrayAdapter<String>(requireContext(), R.layout.item_small_category){
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
        binding.scrapSmallCategorySp.adapter = smallCategoryAdapter
        // 스피너 초기값을 마지막 아이템으로 설정
        binding.scrapSmallCategorySp.setSelection(smallCategoryAdapter.count)

        // droplist를 스피너와 간격을 두고 나오게 함 -> 아이템 크기 = 125px
        binding.scrapSmallCategorySp.dropDownVerticalOffset = dipToPixels(45f).toInt()
    }

    // 스피너 클릭 이벤트 핸들러
    private fun setupSmallCategorySpinnerHandler(type: Int) {
        binding.scrapSmallCategorySp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                binding.scrapSmallCategorySp.setBackgroundResource(R.drawable.bg_category_outline)

                when(type) {
                    1 -> {
                        if (!binding.scrapSmallCategorySp.getItemAtPosition(position).equals("하위 선택")) {
                            // 하위 카테고리 선택하면 배경 변경
                            binding.scrapSmallCategorySp.setBackgroundResource(R.drawable.bg_category_selected)
                            // SmallCategory 변수에 하위 카테고리 저장하기
                            smallCategory = binding.scrapSmallCategorySp.getItemAtPosition(position).toString()
                            Log.i(TAG, "하위 카테고리 확인: $smallCategory")
                            getScrapCodingQuestion(jwt, userIdx, isReplied, bigCategory, smallCategory)
                        } else {
                            smallCategory = null
                            Log.i(TAG, "하위 카테고리 확인: $smallCategory")
                            getScrapCodingQuestion(jwt, userIdx, isReplied, bigCategory, smallCategory)
                            // Toast.makeText(context, "상위 카테고리를 선택해주세요!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    2 -> {
                        if (!binding.scrapSmallCategorySp.getItemAtPosition(position).equals("하위 선택")) {
                            // 하위 카테고리 선택하면 배경 변경
                            binding.scrapSmallCategorySp.setBackgroundResource(R.drawable.bg_category_selected)
                            // SmallCategory 변수에 하위 카테고리 저장하기
                            smallCategory = binding.scrapSmallCategorySp.getItemAtPosition(position).toString()
                            Log.i(TAG, "하위 카테고리 확인: $smallCategory")
                            getScrapConceptQuestion(jwt, userIdx, isReplied, bigCategory, smallCategory)
                        } else {
                            smallCategory = null
                            Log.i(TAG, "하위 카테고리 확인: $smallCategory")
                            getScrapConceptQuestion(jwt, userIdx, isReplied, bigCategory, smallCategory)
                            // Toast.makeText(context, "상위 카테고리를 선택해주세요!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }





    /********************* API 호출 ********************/
    private fun getScrapCodingQuestion(jwt: String, userIdx: Long, isReplied: Boolean, bigCategory: String?, smallCategory: String?){
        val scrapCodingQuestionService = ScrapService()
        scrapCodingQuestionService.setScrapCodingService(this)

        scrapCodingQuestionService.getScrapCodingQuestions(jwt, userIdx, isReplied, bigCategory, smallCategory)
    }

    override fun onGetScrapCodingLoading() {
        binding.scrapLoadingPb.visibility = View.VISIBLE
        Log.d("Scrap/Coding/API","로딩중...")
    }

    override fun onGetScrapCodingSuccess(result: ArrayList<Question>?) {
        binding.scrapLoadingPb.visibility = View.GONE
        Log.d("Scrap/Coding/API","성공")
        if (result != null) { // 해당 카테고리에 대한 질문이 있을때 어댑터에 추가
            scrapAdapter.addScrap(result)
        }
    }

    override fun onGetScrapCodingFailure(code: Int, message: String) {
        binding.scrapLoadingPb.visibility = View.GONE
        when(code){
            400-> Log.d("Scrap/Coding/API", message)
        }
    }

    private fun getScrapConceptQuestion(jwt: String, userIdx: Long, isReplied: Boolean, bigCategory: String?, smallCategory: String?){
        val scrapConceptQuestionService = ScrapService()
        scrapConceptQuestionService.setScrapConceptService(this)

        scrapConceptQuestionService.getScrapConceptQuestions(jwt, userIdx, isReplied, bigCategory, smallCategory)
    }

    override fun onGetScrapConceptLoading() {
        binding.scrapLoadingPb.visibility = View.VISIBLE
        Log.d("Scrap/Concept/API","로딩중...")
    }

    override fun onGetScrapConceptSuccess(result: ArrayList<Question>?) {
        binding.scrapLoadingPb.visibility = View.GONE
        Log.d("Scrap/Concept/API","성공")
        if (result != null) { // 해당 카테고리에 대한 질문이 있을때 어댑터에 추가
            scrapConceptAdapter.addScrap(result)
        }
    }

    override fun onGetScrapConceptFailure(code: Int, message: String) {
        binding.scrapLoadingPb.visibility = View.GONE
        when(code){
            400-> Log.d("Scrap/Concept/API", message)
        }
    }

}