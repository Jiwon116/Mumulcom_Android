package com.example.mumulcom

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Base64.DEFAULT
import android.util.Base64.encodeToString
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.mumulcom.databinding.ActivityQuestionDetailBinding
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


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
    private  var images : ArrayList<String> = ArrayList<String>()
    private var myCodingSkill : String? = null
    private var content : String? = null

    private  var pathList: ArrayList<Bitmap> = ArrayList()

    lateinit var file : File

    private var isFirst : Boolean = true


    private lateinit var resultLauncher : ActivityResultLauncher<Intent>

    private lateinit var repliesForQuestionAdapter: RepliesForQuestionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // QuestionBoardActivity 에서 넘어온 값 setting
        val intent = intent
        bigCategoryName = intent.getStringExtra("bigCategoryName")!!
        questionIdx = intent.getLongExtra("questionIdx",0) // 받아온 질문 고유번호 -> api 호출시 넘김
        type = intent.getIntExtra("type",0) // 1: 코딩질문, 2: 개념질문
        binding.categoryNameTv.text = bigCategoryName


//        when(type){
//            1-> getDetailCodingQuestion() // 코딩 질문
//            2-> getDetailConceptQuestion() // 개념질문
//        }

//        getRepliesForQuestion() // 질문에 대한 답변 받아오는 함수
//        initRecyclerView()


        binding.backIv.setOnClickListener {  // 뒤로 가기 버튼 클릭시
            finish()
        }

        binding.refreshLayout.setOnRefreshListener {
            //  서버에서 데이터 reload
            getRepliesForQuestion() // 질문에 대한 답변 받아오는 함수
    //        initRecyclerView()

            when(type){ // 내용 업데이트
                1-> getDetailCodingQuestion() // 코딩 질문
                2-> getDetailConceptQuestion() // 개념질문
            }

            binding.refreshLayout.isRefreshing = false
        }

        binding.clickLikeIv.setOnClickListener {  // 해당 질문에 좋아요를 눌렀을때
            isLiked = !isLiked
            setLikeQuestion() // 질문에 대한 좋아요 처리

        }

        binding.clickScrapIv.setOnClickListener {
            isScrap = !isScrap
            setScrapQuestion()// 질문에 대한 스크랩 처리
        }

        binding.questionFloatingButton.setOnClickListener { // 답변달기 페이지로 이동
            val intent = Intent(this,AnswerActivity::class.java)
            intent.putExtra("questionIdx",questionIdx) //  type : Long
            intent.putStringArrayListExtra("images",images)  //type : arrayList<string>
            Log.d("checkimage",images.toString())
            intent.putExtra("myCodingSkill",myCodingSkill)
            intent.putExtra("content",title)
            Log.d("aaa",myCodingSkill.toString())
            Log.d("aaa",title.toString())
            startActivity(intent)
        }


        // 답변에 댓글에서 이미지 추가에 대한 처리 부분
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                result->
            if(result.resultCode== Activity.RESULT_OK){
//                val intent = result.data
//
//                selectedUri = intent?.data
                result.data?.data?.let{ uri->
                    pathList.clear()
                    val inputStream = uri.let{
                        contentResolver.openInputStream(
                            it
                        )
                    }
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream!!.close()
                    pathList.add(bitmap)
                }

                if(result.data?.data!=null){ // 사진을 제대로 가져옴.
                    Toast.makeText(this ,"사진을 가져왔습니다.",Toast.LENGTH_SHORT).show()
                    Log.d("imagee",result!!.data?.data.toString())
                }else{
                    Toast.makeText(this ,"사진을 가져오지 못했습니다.",Toast.LENGTH_SHORT).show()
                }

            }else return@registerForActivityResult
        }
    }// end of onCreate



    override fun onStart() {
        super.onStart()
        when(type){
            1-> getDetailCodingQuestion() // 코딩 질문
            2-> getDetailConceptQuestion() // 개념질문
        }

    }

    private fun setScrapQuestion(){ // 질문 스크랩할때
        if(isScrap){ // 스크랩을 했을때
            binding.clickScrapIv.setImageResource(R.drawable.ic_scrap)
            // 서버호출
            Toast.makeText(this,"해당 질문을 스크랩 했습니다.",Toast.LENGTH_SHORT).show()
            setScrapForQuestion()

        }else{ // 스크랩를 취소했을때
            binding.clickScrapIv.setImageResource(R.drawable.ic_bottom_scrap_no_select)
            //  서버호출
            Toast.makeText(this,"해당 질문 스크랩을 취소했습니다.",Toast.LENGTH_SHORT).show()
            setScrapForQuestion()
        }
    }

    private fun setLikeQuestion(){
        if(isLiked){ // 좋아요를 했을때
            binding.clickLikeIv.setImageResource(R.drawable.ic_liked)

            // 서버호출
            setLikeForQuestion()


            Handler(Looper.getMainLooper()).postDelayed({
                when(type){  // -> 좋아요 업댓
                    1-> getLikeCodingQuestion() // 코딩 질문
                    2-> getLikeConceptQuestion() // 개념질문
                }
            },500)

            Toast.makeText(this,"해당 질문에 좋아요를 눌렀습니다.",Toast.LENGTH_SHORT).show()

        }else{ // 좋아요를 취소했을때
            binding.clickLikeIv.setImageResource(R.drawable.ic_like)
            //  서버호출
            setLikeForQuestion()

            Handler(Looper.getMainLooper()).postDelayed({
                when(type){  // -> 좋아요 업댓
                    1-> getLikeCodingQuestion() // 코딩 질문
                    2-> getLikeConceptQuestion() // 개념질문
                }
            },500)

            Toast.makeText(this,"해당 질문에 좋아요를 취소했습니다.",Toast.LENGTH_SHORT).show()


        }
    }


    private fun setScrapForQuestion(){ // 질문 스크랩
        val scrapQuestionService = ScrapQuestionService()
        scrapQuestionService.setScrapQuestionView(this)
        scrapQuestionService.getScrapQuestion(getJwt(this), LikeSend(questionIdx, getUserIdx(this)))
    }


    private fun setLikeForQuestion(){ // 질문 좋아요
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
    private fun getLikeCodingQuestion(){ // 좋아요수만 업뎃
        val detailCodingQuestionService = DetailCodingQuestionService()
        detailCodingQuestionService.setDetailCodingQuestionService(this)
        detailCodingQuestionService.getLikeCountCodingQuestion(questionIdx, getUserIdx(this))
    }
    private fun getLikeConceptQuestion(){ // 좋아요수만 업뎃
        val detailConceptQuestionService = DetailConceptQuestionService()
        detailConceptQuestionService.setDetailConceptQuestionService(this)
        detailConceptQuestionService.getLikeCountConceptQuestion(questionIdx, getUserIdx(this))
    }




    // ----------- DetailConceptQuestionView implement : 개념 질문 -----------------

    override fun onGetDetailConceptQuestionsLoading() {
        Log.d("개념질문 상세페이지/API","로딩중...")
    }

    override fun onGetDetailConceptQuestionsSuccess(result: ArrayList<DetailConceptQuestion>) {

        isAdopted =result[0].isAdopted  // 채택여부

        if(result[0].userIdx == getUserIdx(this)){
            isWriter = true
        }

        getRepliesForQuestion() // 질문에 대한 답변 받아오는 함수
        initRecyclerView()


        Glide.with(this).load(result[0].profileImgUrl).into(binding.profileIv) // 프로필 이미지
        binding.nickNameTv.text = result[0].nickname // 닉네임
        binding.createdAtTv.text = result[0].createdAt // 작성날짜
        binding.questionIv.setImageResource(R.drawable.ic_concept_question_check_img) // 코딩 이미지로바꿈
        binding.titleTv.text = result[0].title // 제목
        binding.bigCategoryTv.text = "#"+result[0].bigCategoryName // 상위 카테고리
        if(result[0].smallCategoryName!=null){
            binding.smallCategoryTv.text = "#"+result[0].smallCategoryName // 하위 카테고리
        }


        //  이미지 있으면 그 수만큼 viewpager 어댑터에 넘기고 없으면 이미지 보여주는 부분 gone 처리
        if(result[0].questionImgUrls.size == 0){
            binding.pictureLinearLayout.visibility = View.GONE
        }else{
            val imageViewPagerAdapter = ImageViewPagerAdapter(this)
            imageViewPagerAdapter.addQuestions(result[0].questionImgUrls!!)
            binding.viewPager.adapter = imageViewPagerAdapter
            binding.indicator.setViewPager(binding.viewPager)
            images.addAll(result[0].questionImgUrls)
            Log.d("checkimage--",images.toString())

        }
//        images = result[0].questionImgUrls

        if(result[0].userIdx== getUserIdx(this)){ // 내 글을 스크랩 불가
            //binding.clickScrapIv.isClickable = false  // 못누름
            binding.clickScrapIv.visibility = View.INVISIBLE  // 이미지 안보임.
        }

        binding.currentErrorTv.text = "내용 : "+result[0].content // 질문 내용
        binding.codingSkillConstraintLayout.visibility = View.GONE

         content = result[0].content

        if(result[0].isLiked =="Y"){ // 이미 좋아요를 했을때
            isLiked = true
            binding.clickLikeIv.setImageResource(R.drawable.ic_liked)
        }

        if(result[0].isScraped=="Y"){ // 이미 스크랩을 했을때
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

    override fun onGetLikeCountConceptQuestion(result: ArrayList<DetailConceptQuestion>) {
        binding.likeCountTv.text = result[0].likeCount.toString() // 좋아요 수만 업댓
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

        title=result[0].title
        //  이미지 있으면 그 수만큼 viewpager 어댑터에 넘기고 없으면 이미지 보여주는 부분 gone 처리
        Log.d("이미지test",result[0].questionImgUrls.toString())

        if(result[0].questionImgUrls.size == 0){
            binding.pictureLinearLayout.visibility = View.GONE
        }else{
            val imageViewPagerAdapter = ImageViewPagerAdapter(this)
            imageViewPagerAdapter.addQuestions(result[0].questionImgUrls!!)
            binding.viewPager.adapter = imageViewPagerAdapter
            binding.indicator.setViewPager(binding.viewPager)

            Log.d("이미지test","어댑터로 넘김")
            images.addAll(result[0].questionImgUrls)
            Log.d("checkimage--",images.toString())
        }

        // images = result[0].questionImgUrls


        binding.currentErrorTv.text = "내용 : "+ result[0].currentError // 질문 내용

        if(result[0].codeQuestionUrl==""||result[0].codeQuestionUrl==null){ // 오류 코드 첨부
            binding.myErrorCodeLayout.visibility = View.GONE

        }else{
            binding.myErrorCodeLayout.visibility = View.VISIBLE
            binding.myErrorCodeTv.text = result[0].codeQuestionUrl
        }
        Log.d("co", result[0].codeQuestionUrl.toString())

        if(result[0].myCodingSkill == ""||result[0].myCodingSkill==null){ // 내 코딩 실력
            binding.codingSkillConstraintLayout.visibility = View.GONE
        }else{
            binding.codingSkillConstraintLayout.visibility = View.VISIBLE
            binding.myCodingSkillTv.text = result[0].myCodingSkill
        }
        myCodingSkill = result[0].myCodingSkill

        Log.d("코딩질문 idx",result[0].questionIdx.toString())

        if(result[0].userIdx== getUserIdx(this)){ // 내 글을 스크랩 불가
            //binding.clickScrapIv.isClickable = false  // 못누름
            binding.clickScrapIv.visibility = View.INVISIBLE  // 이미지 안보임.
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


        content = result[0].currentError

    }

    override fun onGetLikeCountCodingQuestion(result: ArrayList<DetailCodingQuestion>) {
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
     //   repliesForQuestionAdapter.replaceItem(result)


        repliesForQuestionAdapter.setRepliesClickListener(object : RepliesForQuestionAdapter.RepliesItemClickListener{
            override fun onRemoveAnswerButton(isClicked: Boolean) { // 답변하기 버튼 제거
                if(isClicked){
                    binding.questionFloatingButton.visibility = View.GONE
                }else{
                    binding.questionFloatingButton.visibility = View.VISIBLE
                }
            }

            override fun setIsLike() {
                // 답변 데이터 갱신
                getRepliesForQuestion() // 질문에 대한 답변 받아오는 함수
                initRecyclerView()

            }

            override fun goBackCommentActivity(_replyIdx: Long, profileImage: String, nickname: String, createdAt: String, replyUrl: String?, content: String, replyImgUrl: ArrayList<String>
            ) {
                Log.d("abctest1:",_replyIdx.toString())
                val intent = Intent(this@QuestionDetailActivity,CommentActivity::class.java)
                intent.putExtra("replyIdx",_replyIdx)
                intent.putExtra("profileImage",profileImage) // 프로필 이미지
                intent.putExtra("nickname",nickname) // 닉네임
                intent.putExtra("createdAt",createdAt) // 작성 날짜
                intent.putExtra("content",content) // 내용
                intent.putExtra("replyUrl",replyUrl) // 참고 url
                intent.putStringArrayListExtra("replyImgResult",replyImgUrl) // 답변 이미지
                Log.d("replyImageResult",replyImgUrl.toString())
                startActivity(intent)

            }

            override fun goReportActivity(profileId: Long) {
//                val intent=Intent(this@QuestionDetailActivity,ProfileActivity::class.java)
//                intent.putExtra("profileIdx",profileId)
//                startActivity(intent)
                Log.d("abctest1:",profileId.toString())
                val reportDialogFragment = ReportDialogFragment(profileId)
                reportDialogFragment.show(supportFragmentManager,ReportDialogFragment.TAG)
            }
        })

        isFirst =false
    }
    private fun getPhoto(){
        when{
            ContextCompat.checkSelfPermission(
                this@QuestionDetailActivity,android.Manifest.permission.READ_EXTERNAL_STORAGE
            )==PackageManager.PERMISSION_GRANTED->{
                navigatePhoto() // 엘범에서 사진 선택
            }
            shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)->{
                // 교육용 팝업 확인후 권한 팝업 띄움
                showPermissionContextPopup()
            }
            else->{
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1010)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            1010->
                if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    navigatePhoto()
                }else{
                    Toast.makeText(this, "앨범에 대한 접근 권한을 거부하셨습니다.", Toast.LENGTH_SHORT).show()
                }
        }
    }


    private fun navigatePhoto() { // 갤러리에서 사진 가져오는 함수
        // SAF 기능 구현
        val intent = Intent(Intent.ACTION_GET_CONTENT) // ACTION_GET_CONTENT 은 SAF 기능을 실행시켜 content 를 가져온다.
        intent.type = "image/*"
        resultLauncher.launch(intent)

    }
    private fun showPermissionContextPopup(){
        AlertDialog.Builder(this)
            .setTitle("권한 팝업")
            .setMessage("앨범 접근에 대한 권한을 허락해주세요.")
            .setPositiveButton("동의") { _, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1010)
            }
            .create()
            .show()
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
