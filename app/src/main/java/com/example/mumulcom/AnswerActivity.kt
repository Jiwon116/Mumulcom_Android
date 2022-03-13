package com.example.mumulcom

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.viewpager2.widget.ViewPager2
import com.example.mumulcom.databinding.ActivityAnswerBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*


class AnswerActivity:AppCompatActivity(), AnswerView {
    lateinit var binding: ActivityAnswerBinding

    private lateinit var storage: FirebaseStorage
    private lateinit var firestore: FirebaseFirestore //파이어스토리지
    lateinit var photoAdapter:PhotoAdapter//리사이클러뷰
    private var images = arrayListOf<String>()
    var photoList = arrayListOf<Photo>()
    var answerList = arrayListOf<Album>()//답변하기에 떠있는 질문창
    private var jwt: String = ""
    private var userIdx: Long = 1
    private lateinit var title: String
    private var questionIdx: Long=0
    private lateinit var replyUrl: String
    private lateinit var content:String
    lateinit var answerQuestionVPAdater: AnswerQuestionVPAdater
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>//이동(카메라 앨범)
    var count=0//이미지 수


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnswerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)

         jwt = getJwt(this)
         userIdx = getUserIdx(this)

        storage = FirebaseStorage.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // 화면 배경 누르면 키보드 사라지기
        binding.answerBack.setOnClickListener {
            CloseKeyboard()
        }

        //startActivityresult대신
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            }

        //답변하기 측 이미지 삽입 부분
        //5개 이하일때만 추가가능
        if (count<5){
            //추가버튼
            binding.answerImagePlusIv.setOnClickListener {
                val intent =
                    Intent(this, AnswerCameraShootingActivity::class.java)
                activityResultLauncher.launch(intent)
//                finish()
            }
        }

        //질문하기 세팅
        getquestion()

       //질문측 이미지
        val qimage = intent.getStringArrayListExtra("images")
        Log.d("get/qimage", qimage.toString())

        //이미지 없을시 그 줄 삭제
        if(qimage.toString().length<=2){
            binding.answerPictureLinearLayout.visibility=View.GONE
        }
        Log.d("pathll", qimage.toString().length.toString())

        // 뷰페이저 어댑터 생성+답변하기의 질문쪽
        answerQuestionVPAdater = AnswerQuestionVPAdater(this)
        answerQuestionVPAdater.addQuestions(qimage!!)
        binding.answerImageVp.adapter = answerQuestionVPAdater
        binding.answerImageVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL // 방향을 가로로
        binding.answerIndicator.setViewPager(binding.answerImageVp)


        //뒤로가기 버튼
        binding.answerBackIv.setOnClickListener {
            finish()
        }

        //필수 부분 작성되면 답변하기 누르기
        binding.answerAnswerIv.setOnClickListener {
            required()
        }

    }

    //카메라 앨범 이미지 가져오기
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            var imagePath = data?.getStringExtra("path")!!

            photoList.apply {
                add(Photo(imagePath))
                Log.d("SEND/path", imagePath)
                count++
                Log.d("path/count", count.toString())
//                images.add(imagePath)
                Log.d("anan",  images.add(imagePath).toString())
            }
            Log.d("GETGET", photoList.toString())
            //이미지가 5개부터는 추가 불
            if (count>=5){
                //추가버튼
                binding.answerImagePlusIv.setOnClickListener {
                    Toast.makeText(this, "이미지는 최대 5개까지 넣을 수 있습니다", Toast.LENGTH_SHORT).show()
                }
            }

            //이미지 set되는 부분
            if (imagePath != null) {
                var fileName =
                    SimpleDateFormat("yyyyMMddHHmmss").format(Date()) // 파일명이 겹치면 안되기 떄문에 시년월일분초 지정
                storage.getReference().child("image").child(fileName)
                    .putFile(imagePath.toUri())//어디에 업로드할지 지정
                    .addOnSuccessListener { taskSnapshot -> // 업로드 정보를 담는다
                        taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { it ->
                            var imageUrl = it.toString()
                            var photo = Photo(imageUrl)
                            firestore.collection("answer-images")
                                .document().set(photo)
                                .addOnSuccessListener {
                                }
                            Log.d("gege/imageUrl", imageUrl)
                            Log.d("gege/photo", photo.toString())
                            images.add(imageUrl)
                        }
                    }

            }

            //답변하기 리사이클러뷰
            photoAdapter = PhotoAdapter(this, photoList)
            binding.answerImageReferenceVp.adapter = photoAdapter
            photoAdapter.notifyDataSetChanged()


        }

    }

    fun getquestion(){
        //질문 내용
        val questionTV=binding.answerQuestionTv
        val secondIntent = intent
        val message = secondIntent.getStringExtra("content")
        questionTV.setText("질문: "+ message)

        //현재코딩실력 내용
        val myCodingSkill=binding.answerCodingLevelContentTv
        val levelmessage = secondIntent.getStringExtra("myCodingSkill")

        binding.answerLevelLinearLayout.visibility=View.GONE
        if (levelmessage!="") {
            myCodingSkill.setText(" " + levelmessage)
            binding.answerLevelLinearLayout.visibility=View.VISIBLE

        }
        Log.d("get/level", levelmessage.toString())
        if (levelmessage==null) {
            binding.answerLevelLinearLayout.visibility=View.GONE

        }
    }

    // 키보드 사라지는 함수
    fun CloseKeyboard() {
        var view = this.currentFocus

        if(view != null) {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    //api서버값
    private fun getAnswer(): Answer {
//        intent.putExtra("questionIdx",questionIdx) //  type : Long
        questionIdx= intent.getLongExtra("questionIdx",questionIdx)
        replyUrl=binding.answerAnswerCodeEt.text.toString()
        content=binding.answerExplanationEt.text.toString()

        Log.d("answer/questionIdx : ", questionIdx.toString())
        Log.d("answer/userIdx : ", userIdx.toString())
        Log.d("answer/replyUrl : ", replyUrl)
        Log.d("answer/content : ", content)
        Log.d("answer/images", images.toString())

        return Answer(questionIdx, userIdx, replyUrl, content, images)
    }

    //api서버 전송
    private fun answerif(){
        val answerService=AnswerService()

        answerService.setanswerView(this)

        answerService.answer(getJwt(this), getAnswer())
        Log.d("ANSWER/API","Hello")
    }

    private fun required() {

        if (binding.answerExplanationEt.text.isEmpty()) {

            Toast.makeText(this, "설명을 작성해주세요.", Toast.LENGTH_SHORT).show()

            return
        }

        binding.answerAnswerIv.setImageResource(R.drawable.ic_click_answer)

        //승인 버튼 눌러야 api전송
        val builder = AlertDialog.Builder(this).create()
        val dialogView = layoutInflater.inflate(R.layout.dialog_answer, null)

        builder?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        builder?.setCancelable(false)
        builder?.setCanceledOnTouchOutside(false)

        val approve = dialogView.findViewById<Button>(R.id.dialog_approve_btn)
        approve.setOnClickListener {
            answerif()
            builder.dismiss()
        }

        val cancle = dialogView.findViewById<Button>(R.id.dialog_cancel_btn)
        cancle.setOnClickListener {
            builder.dismiss()
        }

        builder.setView(dialogView)
        builder.show()

    }

    override fun onAnswerLoading() {
        Toast.makeText(this, "잠시만 기다려주세요", Toast.LENGTH_SHORT).show()
    }

    override fun onAnswerFailure(code: Int, message: String) {
        Toast.makeText(this, "실패", Toast.LENGTH_SHORT).show()
    }

    override fun onAnswerSuccess(result: Replies?) {
        Toast.makeText(this, "성공", Toast.LENGTH_SHORT).show()

        finish()
    }


}

