package com.example.mumulcom

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mumulcom.databinding.FragmentTabMyAnswerBinding

// 내가 단 답변
class TabAnswerFragment : Fragment(), TabCodingAnswerView, TabConceptAnswerView {
    lateinit var binding: FragmentTabMyAnswerBinding

    private lateinit var tabAnswerAdapter: TabAnswerAdapter
    private lateinit var tabConceptAnswerAdapter: TabAnswerAdapter

    private var jwt : String = ""
    private var userIdx : Long = 0
    private var isAdopted : Boolean = false //(답변달린 질문만 보기 체크)

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentTabMyAnswerBinding.inflate(inflater, container, false)

        jwt = context?.let { getJwt(it) }.toString()
        userIdx = context?.let { getUserIdx(it) }!!

        // default값은 코딩 질문
        initCodingRecyclerView()
        checkCodingAdopted()

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        binding.tabMyAnswerRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.tab_my_answer_coding_btn -> {
                    initCodingRecyclerView()
                    checkCodingAdopted()
                }
                R.id.tab_my_answer_concept_btn -> {
                    initConceptRecyclerView()
                    checkConceptAdopted()
                }
            }
        }
    }

    private fun initCodingRecyclerView() {
        getTabCodingAnswer(jwt, userIdx, isAdopted)
        tabAnswerAdapter = TabAnswerAdapter(context)
        tabAnswerAdapter.setTabAnswerClickListener(object: TabAnswerAdapter.TabAnswerClickListener {
            override fun onItemClick(question: Question) {
                startCodingQuestionDetailActivity(question)// 질문 상세 보기 페이지로 이동
            }
        })
        binding.tabMyAnswerResultRv.adapter = tabAnswerAdapter
        binding.tabMyAnswerResultRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
    }

    private fun startCodingQuestionDetailActivity(question: Question){
        val intent = Intent(requireContext(), QuestionDetailActivity::class.java)
        intent.putExtra("bigCategoryName", question.bigCategoryName) // 상위 카테고리명 넘김
        intent.putExtra("questionIdx", question.questionIdx) // 질문 고유 번호 넘김
        intent.putExtra("type", 1)  // 코딩 질문
        startActivity(intent)
    }

    private fun initConceptRecyclerView() {
        getTabConceptAnswer(jwt, userIdx, isAdopted)
        tabConceptAnswerAdapter = TabAnswerAdapter(context)
        tabConceptAnswerAdapter.setTabAnswerClickListener(object: TabAnswerAdapter.TabAnswerClickListener {
            override fun onItemClick(question: Question) {
                startConceptQuestionDetailActivity(question)// 질문 상세 보기 페이지로 이동
            }
        })
        binding.tabMyAnswerResultRv.adapter = tabConceptAnswerAdapter
        binding.tabMyAnswerResultRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
    }

    private fun startConceptQuestionDetailActivity(question: Question){
        val intent = Intent(requireContext(), QuestionDetailActivity::class.java)
        intent.putExtra("bigCategoryName", question.bigCategoryName) // 상위 카테고리명 넘김
        intent.putExtra("questionIdx", question.questionIdx) // 질문 고유 번호 넘김
        intent.putExtra("type", 2)  // 개념 질문
        startActivity(intent)
    }

    private fun checkCodingAdopted() {
        // 채택된 글만 보기
        binding.tabMyAnswerAdoptLy.setOnClickListener {
            isAdopted = !isAdopted

            if(isAdopted){ // 채택됨
                binding.tabMyAnswerAdoptIv.setImageResource(R.drawable.ic_check_ok)
            }else{
                binding.tabMyAnswerAdoptIv.setImageResource(R.drawable.ic_check_no)
            }

            getTabCodingAnswer(jwt, userIdx, isAdopted)
        }
    }

    private fun checkConceptAdopted() {
        // 채택된 글만 보기
        binding.tabMyAnswerAdoptLy.setOnClickListener {
            isAdopted = !isAdopted

            if(isAdopted){ // 채택됨
                binding.tabMyAnswerAdoptIv.setImageResource(R.drawable.ic_check_ok)
            }else{
                binding.tabMyAnswerAdoptIv.setImageResource(R.drawable.ic_check_no)
            }

            getTabConceptAnswer(jwt, userIdx, isAdopted)
        }
    }

    private fun getTabCodingAnswer(jwt: String, userIdx: Long, isAdopted: Boolean){
        val tabCodingAnswerService = TabService()
        tabCodingAnswerService.setTabCodingAnswerService(this)

        tabCodingAnswerService.getTabCodingAnswers(jwt, userIdx, isAdopted)
    }

    override fun onGetCodingAnswersLoading() {
        binding.tabMyAnswerLoadingPb.visibility = View.VISIBLE
        Log.d("TabAnswer/Coding/API","로딩중...")
    }

    override fun onGetCodingAnswersSuccess(result: ArrayList<Question>?) {
        binding.tabMyAnswerLoadingPb.visibility = View.GONE
        Log.d("TabAnswer/Coding/API","성공")
        if (result != null) { // 해당 카테고리에 대한 질문이 있을때 어댑터에 추가
            tabAnswerAdapter.addTabAnswers(result)
        }
    }

    override fun onGetCodingAnswersFailure(code: Int, message: String) {
        binding.tabMyAnswerLoadingPb.visibility = View.GONE
        when(code){
            400-> Log.d("TabAnswer/Coding/API", message)
        }
    }

    private fun getTabConceptAnswer(jwt: String, userIdx: Long, isAdopted: Boolean){
        val tabConceptAnswerService = TabService()
        tabConceptAnswerService.setTabConceptAnswerService(this)

        tabConceptAnswerService.getTabConceptAnswers(jwt, userIdx, isAdopted)
    }

    override fun onGetConceptAnswersLoading() {
        binding.tabMyAnswerLoadingPb.visibility = View.VISIBLE
        Log.d("TabAnswer/Concept/API","로딩중...")
    }

    override fun onGetConceptAnswersSuccess(result: ArrayList<Question>?) {
        binding.tabMyAnswerLoadingPb.visibility = View.GONE
        Log.d("TabAnswer/Concept/API","성공")
        if (result != null) { // 해당 카테고리에 대한 질문이 있을때 어댑터에 추가
            tabConceptAnswerAdapter.addTabAnswers(result)
        }
    }

    override fun onGetConceptAnswersFailure(code: Int, message: String) {
        binding.tabMyAnswerLoadingPb.visibility = View.GONE
        when(code){
            400-> Log.d("TabAnswer/Concept/API", message)
        }
    }

}