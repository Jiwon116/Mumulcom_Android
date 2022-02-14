package com.example.mumulcom

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.mumulcom.RecentQuestionAdapter
import com.example.mumulcom.Question
import com.example.mumulcom.databinding.FragmentHomeBinding
import com.example.mumulcom.QuestionService
import com.example.mumulcom.RecentQuestionView


class HomeFragment : Fragment(), RecentQuestionView {
    lateinit var binding: FragmentHomeBinding
    private lateinit var recentQuestionAdapter: RecentQuestionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)


        initCategoryButton()

        binding.searchBtn.setOnClickListener {
            val intent = Intent(requireContext(), SearchActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }// en of onCreate()

    override fun onStart() {
        super.onStart()

        getQuestions()
        initViewPager()


    }



    private fun initViewPager(){

        recentQuestionAdapter = RecentQuestionAdapter(requireContext())
        recentQuestionAdapter.setQuestionClickListener(object : RecentQuestionAdapter.QuestionClickListener{
            override fun onItemClick(question: Question) {
                startQuestionDetailActivity(question)// 질문 상세 보기 페이지로 이동
            }

        })
        binding.recentQuestionVp.adapter = recentQuestionAdapter
        binding.recentQuestionVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL

    }

    private fun startQuestionDetailActivity(question: Question){
        val intent = Intent(requireContext(),QuestionDetailActivity::class.java)
        intent.putExtra("bigCategoryName",question.bigCategoryName) // 상위 카테고리명 넘김
        intent.putExtra("questionIdx",question.questionIdx) // 질문 고유 번호 넘김
        intent.putExtra("type", question.type)
        startActivity(intent)


    }

    private fun getQuestions(){
        val questionService = QuestionService()
        questionService.setRecentQuestionView(this)


        questionService.getQuestions(getUserIdx(requireContext()), getJwt(requireContext())) // 현재 로그인한 사용자 정보 넣어줌.
    }






    // ----------- RecentQuestionView implement -----------------



    override fun onGetQuestionsLoading() {
        binding.homeProgressbar.visibility = View.VISIBLE
        Log.d("HomeFragment/API","로딩중...")

    }

    override fun onGetQuestionsSuccess(result: ArrayList<Question>) {
        binding.homeProgressbar.visibility = View.INVISIBLE


        if(result.isEmpty()){ // 내가 한 질문이 없을 경우  -> []
            // viewPager 지우고 텍스트 대체
            binding.recentQuestionVp.visibility = View.GONE
            binding.homeIndicator.visibility = View.GONE
            binding.noMyQuestionTv.visibility = View.VISIBLE
        }else{
            recentQuestionAdapter.addQuestions(result)
            binding.noMyQuestionTv.visibility = View.GONE
            binding.recentQuestionVp.visibility = View.VISIBLE
            binding.homeIndicator.visibility = View.VISIBLE
        }


        // indicator 연결
        binding.homeIndicator.setViewPager(binding.recentQuestionVp)
    }

    override fun onGetQuestionsFailure(code: Int, message: String) {
        binding.homeProgressbar.visibility = View.INVISIBLE
        Log.d("HomeFragment/API","실패 ")
        when(code){
            400-> Log.d("HomeFragment/API",message)
        }
    }












    private fun initCategoryButton() {
        // ------------          앱       -------------------------
        binding.appAllTv.setOnClickListener {
            //앱  전체 게시판 클릭시
            val intent = Intent(context,QuestionBoardActivity::class.java)
            intent.putExtra("category","앱")
            intent.putExtra("bigCategoryIdx",1)
            //       intent.putExtra("smallCategoryIdx",0)
            startActivity(intent)
        }
        binding.androidTv.setOnClickListener {
            // 안드로이드 클릭시
            val intent = Intent(context,QuestionBoardActivity::class.java)
            intent.putExtra("category","안드로이드")
            intent.putExtra("bigCategoryIdx",1)
            intent.putExtra("smallCategoryIdx",1)
            startActivity(intent)
        }
        binding.iosTV.setOnClickListener {
            // IOS 클릭시
            val intent = Intent(context,QuestionBoardActivity::class.java)
            intent.putExtra("category","ios")
            intent.putExtra("bigCategoryIdx",1)
            intent.putExtra("smallCategoryIdx",2)
            startActivity(intent)
        }



        // --------------         웹       -----------------------------

        binding.webAllTv.setOnClickListener {
            //웹  전체 게시판 클릭시
            val intent = Intent(context,QuestionBoardActivity::class.java)
            intent.putExtra("category","웹")
            intent.putExtra("bigCategoryIdx",2)
            //     intent.putExtra("smallCategoryIdx",0)
            startActivity(intent)
        }
        binding.htmlTv.setOnClickListener {
            //html  전체 게시판 클릭시
            val intent = Intent(context,QuestionBoardActivity::class.java)
            intent.putExtra("category","HTML")
            intent.putExtra("bigCategoryIdx",2)
            intent.putExtra("smallCategoryIdx",3)
            startActivity(intent)
        }
        binding.cssTv.setOnClickListener {
            //CSS  전체 게시판 클릭시
            val intent = Intent(context,QuestionBoardActivity::class.java)
            intent.putExtra("category","CSS")
            intent.putExtra("bigCategoryIdx",2)
            intent.putExtra("smallCategoryIdx",4)
            startActivity(intent)
        }
        binding.reactTv.setOnClickListener {
            // React 게시판 클릭시
            val intent = Intent(context,QuestionBoardActivity::class.java)
            intent.putExtra("category","React")
            intent.putExtra("bigCategoryIdx",2)
            intent.putExtra("smallCategoryIdx",5)
            startActivity(intent)
        }


        // --------------         서버      -----------------------------

        binding.serverAllTv.setOnClickListener {
            //서버  전체 게시판 클릭시
            val intent = Intent(context,QuestionBoardActivity::class.java)
            intent.putExtra("category","서버")
            intent.putExtra("bigCategoryIdx",3)
            //    intent.putExtra("smallCategoryIdx",1)
            startActivity(intent)
        }


        binding.nodeTv.setOnClickListener {
            // Node Js 게시판 클릭시
            val intent = Intent(context,QuestionBoardActivity::class.java)
            intent.putExtra("category","Node Js")
            intent.putExtra("bigCategoryIdx",3)
            intent.putExtra("smallCategoryIdx",6)
            startActivity(intent)
        }

        binding.springTv.setOnClickListener {
            //  SPRING   게시판 클릭시
            val intent = Intent(context,QuestionBoardActivity::class.java)
            intent.putExtra("category","Spring")
            intent.putExtra("bigCategoryIdx",3)
            intent.putExtra("smallCategoryIdx",7)
            startActivity(intent)
        }
        // --------------         프로그래밍 언어      -----------------------------

        binding.languageAllTv.setOnClickListener {
            //프로그래밍 언어 전체 게시판 클릭시
            val intent = Intent(context,QuestionBoardActivity::class.java)
            intent.putExtra("category","프로그래밍 언어")
            intent.putExtra("bigCategoryIdx",4)
            //       intent.putExtra("smallCategoryIdx",0)
            startActivity(intent)
        }
        binding.cTv.setOnClickListener {
            // C 게시판 클릭시
            val intent = Intent(context,QuestionBoardActivity::class.java)
            intent.putExtra("category","프로그래밍 언어")
            intent.putExtra("bigCategoryIdx",1)
            intent.putExtra("smallCategoryIdx",8)
            startActivity(intent)
        }
        binding.javaTv.setOnClickListener {
            // Java 게시판 클릭시
            val intent = Intent(context,QuestionBoardActivity::class.java)
            intent.putExtra("category","Java")
            intent.putExtra("bigCategoryIdx",4)
            intent.putExtra("smallCategoryIdx",9)
            startActivity(intent)
        }
        binding.jSTv.setOnClickListener {
            // Js 게시판 클릭시
            val intent = Intent(context,QuestionBoardActivity::class.java)
            intent.putExtra("category","JavaScript")
            intent.putExtra("bigCategoryIdx",4)
            intent.putExtra("smallCategoryIdx",10)
            startActivity(intent)
        }
        binding.pythonTv.setOnClickListener {
            // Python 게시판 클릭시
            val intent = Intent(context,QuestionBoardActivity::class.java)
            intent.putExtra("category","Python")
            intent.putExtra("bigCategoryIdx",4)
            intent.putExtra("smallCategoryIdx",11)
            startActivity(intent)
        }

        binding.cPlusTv.setOnClickListener {
            // C++  게시판 클릭시
            val intent = Intent(context,QuestionBoardActivity::class.java)
            intent.putExtra("category","C++")
            intent.putExtra("bigCategoryIdx",4)
            intent.putExtra("smallCategoryIdx",12)
            startActivity(intent)
        }
        binding.etcAllTv.setOnClickListener {
            // 기타 게시판
            val intent = Intent(context,QuestionBoardActivity::class.java)
            intent.putExtra("category","기타")
            intent.putExtra("bigCategoryIdx",5)
//            intent.putExtra("smallCategoryIdx",1)
            startActivity(intent)
        }


    }// end of initCategoryButton




}