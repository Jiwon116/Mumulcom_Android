package com.example.mumulcom

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.mumulcom.R
import com.example.mumulcom.databinding.ActivityQuestionDetailBinding
import com.example.mumulcom.getJwt
import com.example.mumulcom.getUserIdx


// 질문 상세 페이지 (개념/코딩)
class QuestionDetailActivity : AppCompatActivity(), DetailCodingQuestionView,
    DetailConceptQuestionView, LikeQuestionView,
    RepliesForQuestionView, ScrapQuestionView {// end of class
private lateinit var binding : ActivityQuestionDetailBinding
    private lateinit var bigCategoryName : String
    private var questionIdx : Long = 0 // default 값
    private var type : Int = 0
    private var isLiked = false // 질문에 대한 좋아요 변수
    private var isScrap = false // 질문에 대한 scrap 변수
    private  var isAdopted : String = "N"
    private  var isWriter : Boolean = false

    private lateinit var repliesForQuestionAdapter: RepliesForQuestionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val intent = intent
        bigCategoryName = intent.getStringExtra("bigCategoryName")!!
        questionIdx = intent.getLongExtra("questionIdx",0) // 받아온 질문 고유번호 -> api 호출시 넘김
        type = intent.getIntExtra("type",0) // 1: 코딩질문, 2: 개념질문
        binding.categoryNameTv.text = bigCategoryName
        Log.d("type확인 ",type.toString())

        when(type){
            1-> getDetailCodingQuestion() // 코딩 질문
            2-> getDetailConceptQuestion() // 개념질문
        }

//        getRepliesForQuestion() // 질문에 대한 답변 받아오는 함수
//        initRecyclerView()


        binding.backIv.setOnClickListener {  // 뒤로 가기 버튼 클릭시
            finish()
        }

        binding.refreshLayout.setOnRefreshListener {
            //  서버에서 데이터 reload
            getRepliesForQuestion() // 질문에 대한 답변 받아오는 함수
            initRecyclerView()

            when(type){  // -> 좋아요 업댓
                1-> getDetailCodingQuestion() // 코딩 질문
                2-> getDetailConceptQuestion() // 개념질문
            }


            binding.refreshLayout.isRefreshing = false
        }

        binding.clickLikeIv.setOnClickListener {  // 해당 질문에 좋아요를 눌렀을때

            isLiked = !isLiked  //
            setLikeQuestion() // 질문에 대한 좋아요 처리

        }

        binding.clickScrapIv.setOnClickListener {
            isScrap = !isScrap
            setScrapQuestion()// 질문에 대한 스크랩 처리
        }

        binding.questionFloatingButton.setOnClickListener {
            startActivity(Intent(this,AnswerActivity::class.java))
        }

    }// end of onCreate



    private fun setScrapQuestion(){
        if(isScrap){ // 스크랩을 했을때
            binding.clickScrapIv.setImageResource(R.drawable.ic_scrap)

            // 서버호출
            setScrapForQuestion()


        }else{ // 스크랩를 취소했을때
            binding.clickScrapIv.setImageResource(R.drawable.ic_bottom_scrap_no_select)
            //  서버호출
            setScrapForQuestion()


        }

    }

    private fun setLikeQuestion(){
        if(isLiked){ // 좋아요를 했을때
            binding.clickLikeIv.setImageResource(R.drawable.ic_liked)

            // 서버호출
            setLikeForQuestion()

        }else{ // 좋아요를 취소했을때
            binding.clickLikeIv.setImageResource(R.drawable.ic_like)
            //  서버호출
            setLikeForQuestion()
        }
    }


    private fun setScrapForQuestion(){
        val scrapQuestionService = ScrapQuestionService()
        scrapQuestionService.setScrapQuestionView(this)
        scrapQuestionService.getScrapQuestion(getJwt(this), LikeSend(questionIdx, getUserIdx(this)))

    }


    private fun setLikeForQuestion(){
        val likeQuestionService = LikeQuestionService()
        likeQuestionService.setLikeQuestionView(this)
        likeQuestionService.getLikeQuestion(getJwt(this), LikeSend(questionIdx, getUserIdx(this)))

    }



    private fun initRecyclerView(){
        // recyclerView <-> adapter 연결
        repliesForQuestionAdapter = RepliesForQuestionAdapter(this,isAdopted,isWriter)

        binding.recyclerView.adapter = repliesForQuestionAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL,false)
    }


    private fun getRepliesForQuestion(){// 질문에 대한 답변 받아오는 함수
        val repliesForQuestionService = RepliesForQuestionService()
        repliesForQuestionService.setRepliesForQuestionView(this)
        repliesForQuestionService.getRepliesForQuestion(questionIdx, getUserIdx(this))
    }

    private fun getDetailConceptQuestion(){ // 개념질문 가져옴
        val detailConceptQuestionService = DetailConceptQuestionService()
        detailConceptQuestionService.setDetailConceptQuestionService(this)
        detailConceptQuestionService.getDetailConceptQuestion(questionIdx, getUserIdx(this))

    }

    private fun getDetailCodingQuestion(){ // 코딩질문 가져옴
        val detailCodingQuestionService = DetailCodingQuestionService()
        detailCodingQuestionService.setDetailCodingQuestionService(this)
        detailCodingQuestionService.getDetailConceptQuestion(questionIdx, getUserIdx(this))

    }




    // ----------- DetailConceptQuestionView implement : 개념 질문 -----------------

    override fun onGetDetailConceptQuestionsLoading() {
        Log.d("개념질문 상세페이지/API","로딩중...")
    }

    override fun onGetDetailConceptQuestionsSuccess(result: ArrayList<DetailConceptQuestion>) {
//        Log.d("size 확인111",result.size.toString())
//        Log.d("size 확인111",result[0].nickname)

        isAdopted =result[0].isAdopted  // 채택여부

        if(result[0].userIdx == getUserIdx(this)){
            isWriter = true
        }

        getRepliesForQuestion() // 질문에 대한 답변 받아오는 함수
        initRecyclerView()


        binding.nickNameTv.text = result[0].nickname // 닉네임
        binding.createdAtTv.text = result[0].createdAt // 작성날짜
        binding.questionIv.setImageResource(R.drawable.ic_concept_question_check_img) // 코딩 이미지로바꿈
        Glide.with(this).load(result[0].profileImgUrl).into(binding.profileIv) // 프로필 이미지
        binding.titleTv.text = result[0].title // 제목
        binding.bigCategoryTv.text = "#"+result[0].bigCategoryName // 상위 카테고리

        if(result[0].smallCategoryName!=null){
            binding.smallCategoryTv.text = "#"+result[0].smallCategoryName // 하위 카테고리
        }


        //  이미지 있으면 그 수만큼 viewpager 어댑터에 넘기고 없으면 이미지 보여주는 부분 gone 처리
        if(result[0].questionImgUrls.size == 0){
            binding.pictureLinearLayout.visibility = View.GONE
            Log.d("이미지test","사진 viewpager gone")
        }else{
            val imageViewPagerAdapter = ImageViewPagerAdapter(this)
            imageViewPagerAdapter.addQuestions(result[0].questionImgUrls!!)
            binding.viewPager.adapter = imageViewPagerAdapter
            binding.indicator.setViewPager(binding.viewPager)


            Log.d("이미지test","어댑터로 넘김")

        }

        if(result[0].userIdx== getUserIdx(this)){ // 내 글을 스크랩 불가
            binding.clickScrapIv.isClickable = false
        }

        binding.currentErrorTv.text = result[0].content // 질문 내용
        binding.codingSkillConstraintLayout.visibility = View.GONE

        if(result[0].isLiked =="Y"){
            isLiked = true
            binding.clickLikeIv.setImageResource(R.drawable.ic_liked)
        }

        if(result[0].isScraped=="Y"){
            isScrap = true
            binding.clickScrapIv.setImageResource(R.drawable.ic_scrap)
        }



        if(result[0].userIdx == getUserIdx(this)){
            isWriter = true
        }


        binding.replyCountTv.text = result[0].replyCount.toString()  // 답변 수
        binding.likeCountTv.text = result[0].likeCount.toString() // 좋아요 수

        Log.d("개념질문 idx",result[0].questionIdx.toString())

    }


    override fun onGetDetailConceptQuestionsFailure(code: Int, message: String) {
        when(code){
            400-> Log.d("개념질문 상세페이지/API",message)
        }
    }






    // ----------- DetailCodingQuestionView implement : 코딩 질문 -----------------

    override fun onGetDetailCodingQuestionsLoading() {
        Log.d("코딩질문 상세페이지/API","로딩중...")
    }

    override fun onGetDetailCodingQuestionsSuccess(result: ArrayList<DetailCodingQuestion>) {

        isAdopted =result[0].isAdopted  // 채택여부

        if(result[0].userIdx == getUserIdx(this)){
            isWriter = true
        }


        getRepliesForQuestion() // 질문에 대한 답변 받아오는 함수
        initRecyclerView()


        binding.nickNameTv.text = result[0].nickname // 닉네임
        binding.createdAtTv.text = result[0].createdAt // 작성날짜
        Glide.with(this).load(result[0].profileImgUrl).into(binding.profileIv) // 프로필 이미지
        binding.titleTv.text = result[0].title // 제목
        binding.bigCategoryTv.text = "#"+result[0].bigCategoryName // 상위 카테고리
        if(result[0].smallCategoryName!=null){
            binding.smallCategoryTv.text = "#"+result[0].smallCategoryName // 하위 카테고리
        }

        //  이미지 있으면 그 수만큼 viewpager 어댑터에 넘기고 없으면 이미지 보여주는 부분 gone 처리
        Log.d("이미지test",result[0].questionImgUrls.toString())
//        Log.d("이미지test",result[0].questionImgUrls!!.isEmpty().toString())
//        Log.d("이미지test--",result[0].questionImgUrls[0].toString())

        if(result[0].questionImgUrls.size == 0){
            binding.pictureLinearLayout.visibility = View.GONE
            Log.d("이미지test","사진 viewpager gone")
        }else{
            val imageViewPagerAdapter = ImageViewPagerAdapter(this)
            imageViewPagerAdapter.addQuestions(result[0].questionImgUrls!!)
            binding.viewPager.adapter = imageViewPagerAdapter
            binding.indicator.setViewPager(binding.viewPager)


            Log.d("이미지test","어댑터로 넘김")

        }
        binding.currentErrorTv.text = result[0].currentError // 질문 내용


        if(result[0].myCodingSkill == null){ // 내 코딩 실력
            binding.codingSkillConstraintLayout.visibility = View.GONE
        }else{
            binding.myCodingSkillTv.text = result[0].myCodingSkill
        }

        Log.d("코딩질문 idx",result[0].questionIdx.toString())

        if(result[0].userIdx== getUserIdx(this)){ // 내 글을 스크랩 불가
            binding.clickScrapIv.isClickable = false
        }


        if(result[0].isLiked =="Y"){
            isLiked = true
            binding.clickLikeIv.setImageResource(R.drawable.ic_liked)
        }

        Log.d("scraped",result[0].isScraped)
        if(result[0].isScraped=="Y"){
            isScrap = true
            binding.clickScrapIv.setImageResource(R.drawable.ic_scrap)
        }



        binding.replyCountTv.text = result[0].replyCount.toString()  // 답변 수
        binding.likeCountTv.text = result[0].likeCount.toString() // 좋아요 수

    }

    override fun onGetDetailCodingQuestionsFailure(code: Int, message: String) {
        when(code){
            400-> Log.d("코딩질문 상세페이지/API",message)
        }
    }






    // ----------- RepliesForQuestionView implement : 질문에 대한 답변들 -----------------

    override fun onGetRepliesLoading() {
        Log.d("질문에 대한 답변/API","로딩중...")
    }

    override fun onGetRepliesSuccess(result: ArrayList<Reply>) {
        repliesForQuestionAdapter.addQuestions(result)
        repliesForQuestionAdapter.setRepliesClickListener(object : RepliesForQuestionAdapter.RepliesItemClickListener{
            override fun onRemoveAnswerButton(isClicked: Boolean) {
                if(isClicked){
                    binding.questionFloatingButton.visibility = View.GONE
                }else{
                    binding.questionFloatingButton.visibility = View.VISIBLE
                }
            }

//            override fun onClickAdoptButton(isClicked: Boolean) {
//                if(isClicked){
//                    getRepliesForQuestion() // 질문에 대한 답변 받아오는 함수
//                    initRecyclerView()
//                }
//            }


        })

    }

    override fun onGetRepliesFailure(code: Int, message: String) {
        when(code){
            400-> Log.d("질문에 대한 답변/API",message)
        }
    }


    // -------------- LikeQuestionView implement : 질문에 대한 좋아요 ---------------

    override fun onGetLikeQuestionLoading() {
        Log.d("질문에 대한 좋아요/API","로딩중...")
    }

    override fun onGetLikeQuestionSuccess(result: Like) {
        Log.d("likeTest","질문을 만든 유저 id : "+result.noticeTargetUserIdx) // 해당 질문을 작성한 유저 id
        Log.d("likeTest","좋아요 내용 :  "+result.noticeContent)  // 000 글을 좋아요 했습니다./취소 했습니다.

    }

    override fun onGetLikeQuestionFailure(code: Int, message: String) {
        when(code){
            400-> Log.d("질문에 대한 좋아요/API",message)
        }
    }





    // ----------------------- ScrapQuestionView implement : 질문에 대한 스크랩 -----------------


    override fun onGetScrapLoading() {
        Log.d("질문에 대한 스크랩/API","로딩중...")
    }

    override fun onGetScrapSuccess(message: String, result: String) {
        Log.d("scrapTest",message) // 응답 메시지
        Log.d("scrapTest",result)  // 스크랩 성공 여부
    }

    override fun onGetScrapFailure(code: Int, message: String) {
        when(code){
            400-> Log.d("질문에 대한 스크랩/API",message)
        }
    }


}