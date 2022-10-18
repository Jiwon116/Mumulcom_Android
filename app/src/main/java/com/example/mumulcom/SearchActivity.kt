package com.example.mumulcom

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mumulcom.databinding.ActivitySearchBinding
import kotlin.collections.ArrayList

class SearchActivity : AppCompatActivity(), SearchCodingQuestionView, SearchConceptQuestionView {
    lateinit var binding: ActivitySearchBinding

    private var query: String? = null

    private lateinit var codingQuestionAdapter: CodingQuestionAdapter
    private var matchedCodingQuestion: ArrayList<CodingQuestion> = arrayListOf()    // keyword와 일치하는 데이터

    private lateinit var conceptQuestionAdapter: ConceptQuestionAdapter
    private var matchedConceptQuestion: ArrayList<ConceptQuestion> = arrayListOf()  // keyword와 일치하는 데이터


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // default값은 코딩 질문
        initCodingRecyclerView(query)
        codingPerformSearch()

        binding.searchRadioGroup.setOnCheckedChangeListener { group, checkedId, ->
            when(checkedId) {
                R.id.search_coding_btn -> {
                    query = binding.searchView.query.toString()
                    Log.d("검색어", query.toString())
                    initCodingRecyclerView(query)
                    codingPerformSearch()
                }
                R.id.search_concept_btn -> {
                    query = binding.searchView.query.toString()
                    Log.d("검색어", query.toString())
                    initConceptRecyclerView(query)
                    conceptPerformSearch()
                }
            }
        }

        // 화면 배경 누르면 키보드 사라지기
        binding.searchLy.setOnClickListener {
            closeKeyboard()
        }

        // 뒤로가기 버튼 누르기
        binding.searchBackIv.setOnClickListener {
            onBackPressed()
        }


    }

    // 키보드 사라지는 함수
    fun closeKeyboard() {
        val view = this.currentFocus

        if(view != null) {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    // 리사이클러뷰에 코딩 질문 데이터와 어댑터 적용
    private fun initCodingRecyclerView(query: String?) {
        // 처음에는 검색 키워드 값 null
        getCodingQuestions(query)
        codingQuestionAdapter = CodingQuestionAdapter(this)
        codingQuestionAdapter.setCodingQuestionClickListener(object: CodingQuestionAdapter.CodingQuestionClickListener{
            override fun onItemClick(codingQuestion: CodingQuestion) {
                startCodingQuestionDetailActivity(codingQuestion)// 질문 상세 보기 페이지로 이동
            }
        })
        binding.searchQueryResultRv.adapter = codingQuestionAdapter
        binding.searchQueryResultRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)

        binding.searchQueryResultRv.apply {
            codingQuestionAdapter.addCodingQuestions(matchedCodingQuestion)
        }
    }

    private fun startCodingQuestionDetailActivity(codingQuestion: CodingQuestion){
        val intent = Intent(this,QuestionDetailActivity::class.java)
        intent.putExtra("bigCategoryName", codingQuestion.bigCategoryName) // 상위 카테고리명 넘김
        intent.putExtra("questionIdx", codingQuestion.questionIdx) // 질문 고유 번호 넘김
        intent.putExtra("type", 1)  // 코딩 질문
        startActivity(intent)
    }

    // 리사이클러뷰에 개념 질문 데이터와 어댑터 적용
    private fun initConceptRecyclerView(query: String?) {
        // 처음에는 검색 키워드 값 null
        getConceptQuestions(query)
        conceptQuestionAdapter = ConceptQuestionAdapter(this)
        conceptQuestionAdapter.setConceptQuestionClickListener(object: ConceptQuestionAdapter.ConceptQuestionClickListener{
            override fun onItemClick(conceptQuestion: ConceptQuestion) {
                startConceptQuestionDetailActivity(conceptQuestion)// 질문 상세 보기 페이지로 이동
            }
        })
        binding.searchQueryResultRv.adapter = conceptQuestionAdapter
        binding.searchQueryResultRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)

        binding.searchQueryResultRv.apply {
            conceptQuestionAdapter.addConceptQuestions(matchedConceptQuestion)
        }
    }

    private fun startConceptQuestionDetailActivity(conceptQuestion: ConceptQuestion){
        val intent = Intent(this,QuestionDetailActivity::class.java)
        intent.putExtra("bigCategoryName", conceptQuestion.bigCategoryName) // 상위 카테고리명 넘김
        intent.putExtra("questionIdx", conceptQuestion.questionIdx) // 질문 고유 번호 넘김
        intent.putExtra("type", 2)  // 개념 질문
        startActivity(intent)
    }

    private fun codingPerformSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(keyword: String?): Boolean {   // 검색 버튼 클릭했을 때
                // 자판에 있는 검색 버튼을 클릭했을때만 toast 나오게 하고 싶음
                initCodingRecyclerView(keyword)
                closeKeyboard()
                return true
            }

            override fun onQueryTextChange(keyword: String?): Boolean { // 텍스트가 바뀌는 순간순간
                return false
            }
        })
    }

    private fun conceptPerformSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(keyword: String?): Boolean {   // 검색 버튼 클릭했을 때
                // 자판에 있는 검색 버튼을 클릭했을때만 toast 나오게 하고 싶음
                initConceptRecyclerView(keyword)
                closeKeyboard()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean { // 텍스트가 바뀌는 순간순간
                return false
            }
        })
    }

    private fun getCodingQuestions(keyword: String?){
        val searchCodingQuestionService = SearchService()
        searchCodingQuestionService.setSearchCodingQuestionView(this)

        searchCodingQuestionService.getCodingQuestions(keyword)
    }


    override fun onGetCodingQuestionsLoading() {
        binding.searchLoadingPb.visibility = View.VISIBLE
        Log.d("SearchActivity/CodingQuestion/API","검색 로딩 중...")
    }

    override fun onGetCodingQuestionsSuccess(result: ArrayList<CodingQuestion>?) {
        binding.searchLoadingPb.visibility = View.GONE
        if (result != null) { // 검색 결과가 있으면 어댑터에 추가
            codingQuestionAdapter.addCodingQuestions(result)
        } else {
            Toast.makeText(this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onGetCodingQuestionsFailure(code: Int, message: String) {
        binding.searchLoadingPb.visibility = View.GONE
        when(code){
            400-> Log.d("SearchActivity/CodingQuestion/API", message)
        }
    }

    private fun getConceptQuestions(keyword:String?){
        val searchConceptQuestionService = SearchService()
        searchConceptQuestionService.setSearchConceptQuestionView(this)

        searchConceptQuestionService.getConceptQuestions(keyword)
    }


    override fun onGetConceptQuestionsLoading() {
        binding.searchLoadingPb.visibility = View.VISIBLE
        Log.d("SearchActivity/ConceptQuestion/API","검색 로딩 중...")
    }

    override fun onGetConceptQuestionsSuccess(result: ArrayList<ConceptQuestion>?) {
        binding.searchLoadingPb.visibility = View.GONE
        if (result != null) { // 검색 결과가 있으면 어댑터에 추가
            conceptQuestionAdapter.addConceptQuestions(result)
        } else {
            Toast.makeText(this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onGetConceptQuestionsFailure(code: Int, message: String) {
        binding.searchLoadingPb.visibility = View.GONE
        when(code){
            400-> Log.d("SearchActivity/ConceptQuestion/API", message)
        }
    }

/*
    @SuppressLint("NotifyDataSetChanged")
    private fun initRecyclerView() {
        questionAdapter = QuestionAdapter(question).also {
            binding.searchQueryResultRv.adapter = it
            binding.searchQueryResultRv.adapter!!.notifyDataSetChanged()
        }
    }

    private fun performSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {   // 검색 버튼 클릭했을 때
                // 자판에 있는 검색 버튼을 클릭했을때만 toast 나오게 하고 싶음
                // toast를 출력할건지에 대한 boolean 값을 함수에 넘겨줌
                search(query, true)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean { // 텍스트가 바뀌는 순간순간
                search(newText, false)
                return true
            }
        })
    }

    private fun search(text: String?, toast: Boolean) {
        // 1번이면 matchedQuestion에다 arrayList에 coding
        matchedQuestion = arrayListOf()

        text?.let {
            question.forEach { question ->
                // 질문 제목에서 일치하는 항목을 검색
                if (question.title.contains(text))
                    matchedQuestion.add(question)
            }
        }
        // 결과 보여주기
        updateRecyclerView()

        // 검색 결과가 없다면
        if (toast && matchedQuestion.isEmpty()) {
            Toast.makeText(this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
        }
        updateRecyclerView()
    }

    private fun updateRecyclerView() {
        binding.searchQueryResultRv.apply {
            questionAdapter.questionList = matchedQuestion
            questionAdapter.notifyDataSetChanged()
        }
    }
*/
}