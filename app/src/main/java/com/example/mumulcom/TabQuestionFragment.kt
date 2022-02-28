package com.example.mumulcom

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mumulcom.databinding.FragmentTabMyQuestionBinding

class TabQuestionFragment : Fragment(), TabCodingQuestionView, TabConceptQuestionView {
    lateinit var binding: FragmentTabMyQuestionBinding

    private lateinit var tabQuestionAdapter: TabQuestionAdapter
    private lateinit var tabConceptQuestionAdapter: TabQuestionAdapter

    private var jwt : String = ""
    private var userIdx : Long = 0
    private var isReplied : Boolean = false //(답변달린 질문만 보기 체크)

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentTabMyQuestionBinding.inflate(inflater, container, false)

        jwt = context?.let { getJwt(it) }.toString()
        userIdx = context?.let { getUserIdx(it) }!!

        Log.d("jwt: ", jwt)
        Log.d("userIdx : ", userIdx.toString())

        // default값은 코딩 질문
        initCodingRecyclerView()
        checkCodingReplied()

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        binding.tabMyQuestionRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.tab_my_question_coding_btn -> {
                    initCodingRecyclerView()
                    checkCodingReplied()
                }
                R.id.tab_my_question_concept_btn -> {
                    initConceptRecyclerView()
                    checkConceptReplied()
                }
            }
        }
    }


    private fun initCodingRecyclerView() {
        getTabCodingQuestion(jwt, userIdx, isReplied)
        tabQuestionAdapter = TabQuestionAdapter(context)
        tabQuestionAdapter.setTabQuestionClickListener(object: TabQuestionAdapter.TabQuestionClickListener {
            override fun onItemClick(question: Question) {
                startCodingQuestionDetailActivity(question)// 질문 상세 보기 페이지로 이동
            }
        })
        binding.tabMyQuestionResultRv.adapter = tabQuestionAdapter
        binding.tabMyQuestionResultRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
    }

    private fun startCodingQuestionDetailActivity(question: Question){
        val intent = Intent(requireContext(), QuestionDetailActivity::class.java)
        intent.putExtra("bigCategoryName", question.bigCategoryName) // 상위 카테고리명 넘김
        intent.putExtra("questionIdx", question.questionIdx) // 질문 고유 번호 넘김
        intent.putExtra("type", 1)  // 코딩 질문
        startActivity(intent)
    }

    private fun initConceptRecyclerView() {
        getTabConceptQuestion(jwt, userIdx, isReplied)
        tabConceptQuestionAdapter = TabQuestionAdapter(context)
        tabConceptQuestionAdapter.setTabQuestionClickListener(object: TabQuestionAdapter.TabQuestionClickListener {
            override fun onItemClick(question: Question) {
                startConceptQuestionDetailActivity(question)// 질문 상세 보기 페이지로 이동
            }
        })
        binding.tabMyQuestionResultRv.adapter = tabConceptQuestionAdapter
        binding.tabMyQuestionResultRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
    }

    private fun startConceptQuestionDetailActivity(question: Question){
        val intent = Intent(requireContext(), QuestionDetailActivity::class.java)
        intent.putExtra("bigCategoryName", question.bigCategoryName) // 상위 카테고리명 넘김
        intent.putExtra("questionIdx", question.questionIdx) // 질문 고유 번호 넘김
        intent.putExtra("type", 2)  // 코딩 질문
        startActivity(intent)
    }

    private fun checkCodingReplied() {
        // 답변 단글만 보기
        binding.tabMyQuestionReplyLy.setOnClickListener {
            isReplied = !isReplied

            if(isReplied){ // 답변 달림
                binding.tabMyQuestionReplyIv.setImageResource(R.drawable.ic_check_ok)
            }else{
                binding.tabMyQuestionReplyIv.setImageResource(R.drawable.ic_check_no)
            }

            getTabCodingQuestion(jwt, userIdx, isReplied)
        }
    }

    private fun checkConceptReplied() {
        // 답변 단글만 보기
        binding.tabMyQuestionReplyLy.setOnClickListener {
            isReplied = !isReplied

            if(isReplied){ // 답변 달림
                binding.tabMyQuestionReplyIv.setImageResource(R.drawable.ic_check_ok)
            }else{
                binding.tabMyQuestionReplyIv.setImageResource(R.drawable.ic_check_no)
            }

            getTabConceptQuestion(jwt, userIdx, isReplied)
        }
    }

    private fun getTabCodingQuestion(jwt: String, userIdx: Long, isReplied: Boolean){
        val tabCodingQuestionService = TabService()
        tabCodingQuestionService.setTabCodingQuestionService(this)

        tabCodingQuestionService.getTabCodingQuestions(jwt, userIdx, isReplied)
    }

    override fun onGetCodingQuestionsLoading() {
        binding.tabMyQuestionLoadingPb.visibility = View.VISIBLE
        Log.d("TabQuestion/Coding/API","로딩중...")
    }

    override fun onGetCodingQuestionsSuccess(result: ArrayList<Question>?) {
        binding.tabMyQuestionLoadingPb.visibility = View.GONE
        Log.d("TabQuestion/Coding/API","성공")
        if (result != null) { // 해당 카테고리에 대한 질문이 있을때 어댑터에 추가
            tabQuestionAdapter.addTabQuestions(result)
        }
    }

    override fun onGetCodingQuestionsFailure(code: Int, message: String) {
        binding.tabMyQuestionLoadingPb.visibility = View.GONE
        when(code){
            400-> Log.d("TabQuestion/Coding/API", message)
        }
    }

    private fun getTabConceptQuestion(jwt: String, userIdx: Long, isReplied: Boolean){
        val tabConceptQuestionService = TabService()
        tabConceptQuestionService.setTabConceptQuestionService(this)

        tabConceptQuestionService.getTabConceptQuestions(jwt, userIdx, isReplied)
    }

    override fun onGetConceptQuestionsLoading() {
        binding.tabMyQuestionLoadingPb.visibility = View.VISIBLE
        Log.d("TabQuestion/Concept/API","로딩중...")
    }

    override fun onGetConceptQuestionsSuccess(result: ArrayList<Question>?) {
        binding.tabMyQuestionLoadingPb.visibility = View.GONE
        Log.d("TabQuestion/Concept/API","성공")
        if (result != null) { // 해당 카테고리에 대한 질문이 있을때 어댑터에 추가
            tabConceptQuestionAdapter.addTabQuestions(result)
        }
    }

    override fun onGetConceptQuestionsFailure(code: Int, message: String) {
        binding.tabMyQuestionLoadingPb.visibility = View.GONE
        when(code){
            400-> Log.d("TabQuestion/Concept/API", message)
        }
    }
}