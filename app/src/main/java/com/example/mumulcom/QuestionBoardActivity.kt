package com.example.mumulcom

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mumulcom.QuestionAdapter
import com.example.mumulcom.Question
import com.example.mumulcom.databinding.ActivityQuestionBoardBinding
import com.example.mumulcom.CategoryQuestionService
import com.example.mumulcom.CategoryQuestionView

// 카테고리별 질문 목록
class QuestionBoardActivity : AppCompatActivity(), CategoryQuestionView {
    private lateinit var binding : ActivityQuestionBoardBinding
    private var codingQuestionCheck : Boolean = true // default 값 (코딩 질문)
    private var conceptQuestionCheck : Boolean = false // default 값 (개념 질문)
    private var type : Int = 1  // 질문 타입은 기본이 코딩 질문으로 (개념질문 : 2)
    private var sort : Int = 1 // 정렬 기준은 기본이 최신순으로 (핫한순 : 2)

    private var recentQuestionCheck : Boolean = true // (최신순)
    private var hotQuestionCheck : Boolean = false // (핫한순)
    private var isReplied : Boolean = false //(답변달린 질문만 보기 체크)

    private lateinit var title : String
    private var bigCategoryIdx : Int =0
    private var smallCategoryIdx : Int? = null
    private var smallCategoryIdx_ : Int?=null

    private lateinit var questionAdapter : QuestionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        title = intent.getStringExtra("category")!!
        binding.categoryNameTv.text = title
        bigCategoryIdx = intent.getIntExtra("bigCategoryIdx",0) // 상위 카테고리 값 받음
        smallCategoryIdx = intent.getIntExtra("smallCategoryIdx",-1) // 하위 카테고리 값 받음

        if(smallCategoryIdx!==-1){
            smallCategoryIdx_ = smallCategoryIdx
        }

        Log.d("QuestionBoard:bigCategoryIdx",bigCategoryIdx.toString())
        Log.d("QuestionBoard:smallCategoryIdx",smallCategoryIdx.toString())
        Log.d("QuestionBoard:smallCategoryIdx_",smallCategoryIdx_.toString())


        initView()  // view 초기화
        initCodingOrConceptQuestionButton() // 코딩 질문 & 개념 질문 버튼 초기화
        initRecentOrHotQuestionTextButton() // 최신순 & 핫한순 버튼 초기화
        initCheckCommentButton() // 답변 달린 글만 보기 버튼 초기화

//        getCategoryQuestions()
//        initRecyclerView()



        // 새로고침
        binding.refreshLayout.setOnRefreshListener {
            // todo 서버에서 데이터 reload

            getCategoryQuestions()
            initRecyclerView()

            binding.refreshLayout.isRefreshing = false
        }


        Log.d("lifecycle","QuestionBoardActivity onCreate")


        binding.searchIv.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

    }// end of onCreate

    override fun onStart() {
        super.onStart()
        //     Log.d("lifecycle","QuestionBoardActivity onStart")

        getCategoryQuestions()
        initRecyclerView()

    }


//    override fun onResume() {
//        super.onResume()
//        Log.d("lifecycle","QuestionBoardActivity onResume")
//    }
//
//    override fun onPause() {
//        super.onPause()
//        Log.d("lifecycle","QuestionBoardActivity onPause")
//
//    }
//
//    override fun onStop() {
//        super.onStop()
//        Log.d("lifecycle","QuestionBoardActivity onStop")
//
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        Log.d("lifecycle","QuestionBoardActivity onDestroy")
//
//    }

    private fun initRecyclerView(){
        // recyclerView <-> adapter 연결
        questionAdapter = QuestionAdapter(this)
        questionAdapter.setQuestionClickListener(object: QuestionAdapter.QuestionClickListener{
            override fun onItemClick(question: Question) {
                startQuestionDetailActivity(question)// 질문 상세 보기 페이지로 이동
            }

        })
        binding.questionBoardRv.adapter = questionAdapter
        binding.questionBoardRv.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)

    }
    private fun startQuestionDetailActivity(question: Question){
        val intent = Intent(this,QuestionDetailActivity::class.java)
        intent.putExtra("bigCategoryName",question.bigCategoryName) // 상위 카테고리명 넘김
        intent.putExtra("questionIdx",question.questionIdx) // 질문 고유 번호 넘김
        intent.putExtra("type",type) // 코딩질문 or 개념질문
        startActivity(intent)


    }



    private fun getCategoryQuestions(){
        val categoryQuestionService = CategoryQuestionService()
        categoryQuestionService.setCategoryQuestionService(this)

        Log.d("value","type "+type.toString())
        Log.d("value","sort "+sort.toString())
        Log.d("value","bigCategoryIdx "+bigCategoryIdx.toString())
        Log.d("value","smallCategoryIdx "+smallCategoryIdx_.toString())
        Log.d("value","isReplied "+isReplied.toString())
        categoryQuestionService.getCategoryQuestions(type,sort,bigCategoryIdx,smallCategoryIdx_,isReplied,0,10)
    }


    // ----------- CategoryQuestionView implement -----------------
    override fun onGetQuestionsLoading() {
        Log.d("QuestionBoardActivity/API","로딩중...")
    }

    override fun onGetQuestionsSuccess(result: ArrayList<Question>?) {
        Log.d("QuestionBoardActivity/API","데이터 받아옴")
        if (result != null) { // 해당 카테고리에 대한 질문이 있을때 어댑터에 추가
            questionAdapter.addQuestions(result)
        }

    }

    override fun onGetQuestionsFailure(code: Int, message: String) {
        when(code){
            400-> Log.d("QuestionBoardActivity/API",message)
        }
    }




    //-------------------------- 함수 정의 --------------------------------

    private fun initView(){
        // 상단 뒤로가기 버튼
        binding.backIv.setOnClickListener {
            finish()
        }

        // 코딩 질문 버튼
        binding.codingQuestionIv.setOnClickListener {
            codingQuestionCheck = true
            conceptQuestionCheck = false
            initCodingOrConceptQuestionButton()
            type=1
            //  api 다시 호출
            getCategoryQuestions()
            initRecyclerView()
        }


        // 개념 질문 버튼
        binding.conceptQuestionIv.setOnClickListener {
            conceptQuestionCheck= true
            codingQuestionCheck = false
            initCodingOrConceptQuestionButton()
            type=2
            // api 다시 호출
            getCategoryQuestions()
            initRecyclerView()

        }

        // 최신순
        binding.sortRecentTv.setOnClickListener {
            recentQuestionCheck = true
            hotQuestionCheck = false
            initRecentOrHotQuestionTextButton()
            sort = 1
            // api 다시 호출
            getCategoryQuestions()
            initRecyclerView()

        }

        //핫한순
        binding.sortHotTv.setOnClickListener {
            hotQuestionCheck = true
            recentQuestionCheck = false
            initRecentOrHotQuestionTextButton()
            sort=2
            //  api 다시 호출
            getCategoryQuestions()
            initRecyclerView()

        }


        // 답변 단글만 보기
        binding.questionBoardReplyLy.setOnClickListener {
            isReplied = !isReplied
            initCheckCommentButton()
            getCategoryQuestions()
            initRecyclerView()


        }

        binding.questionFloatingButton.setOnClickListener {
            startActivity(Intent(this,QuestionCategoryActivity::class.java))
        }
    }

    private fun initRecentOrHotQuestionTextButton() {
        if(recentQuestionCheck){
            binding.sortRecentTv.setTextColor(Color.parseColor("#474A57"))
            binding.sortRecentTv.isEnabled = false
            // 최근 질문순으로 보여줌
        }else{
            binding.sortRecentTv.setTextColor(Color.parseColor("#C4C4C4"))
            binding.sortRecentTv.isEnabled = true
        }

        if(hotQuestionCheck){
            binding.sortHotTv.setTextColor(Color.parseColor("#474A57"))
            binding.sortHotTv.isEnabled = false
            // 인기 질문 순으로 보여줌
        }else{
            binding.sortHotTv.setTextColor(Color.parseColor("#C4C4C4"))
            binding.sortHotTv.isEnabled = true
        }

    }

    fun initCodingOrConceptQuestionButton(){

        if(codingQuestionCheck){
            binding.codingQuestionIv.setImageResource(R.drawable.coding_question_check_img)
            binding.codingQuestionIv.isEnabled = false // 이미 선택되었으면 선택 못함.
            // 코딩 질문만 보여줌
        }else{
            binding.codingQuestionIv.setImageResource(R.drawable.coding_question_img)
            binding.codingQuestionIv.isEnabled = true
        }

        if(conceptQuestionCheck){
            binding.conceptQuestionIv.setImageResource(R.drawable.ic_concept_question_check_img)
            binding.conceptQuestionIv.isEnabled = false // 이미 선택되었으면 선택 못함.
            // 개념 질문만 보여줌

        }else{
            binding.conceptQuestionIv.setImageResource(R.drawable.ic_concept_question_img)
            binding.conceptQuestionIv.isEnabled = true
        }

    }

    private fun initCheckCommentButton(){
        if(isReplied){ // 답변 달린 댓글만 보기
            binding.ifAnswerIsCheckIv.setImageResource(R.drawable.ic_check_ok)

        }else{
            binding.ifAnswerIsCheckIv.setImageResource(R.drawable.ic_check_no)
        }
    }


}// end of class