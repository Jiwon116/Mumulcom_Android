package com.example.mumulcom

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.mumulcom.databinding.ActivityAnswerBinding
import com.example.test.AnswerQuestionVPAdater
import com.example.test.ViewPagerAdapter
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


class AnswerActivity:AppCompatActivity(), AnswerView {
    lateinit var binding: ActivityAnswerBinding

    private lateinit var storage: FirebaseStorage
    private lateinit var firestore: FirebaseFirestore //파이어스토리지
    val IMAGE_PICK=1111
    var selectImage:Uri?=null
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

        // jwt = getJwt(this)
        // userIdx = getUserIdx(this)
        jwt =
            "eyJ0eXBlIjoiand0IiwiYWxnIjoiSFMyNTYifQ.eyJ1c2VySWR4IjoxMywiaWF0IjoxNjQ1MDM1NDU1LCJleHAiOjE2NDY1MDY2ODR9.GVcX6bK1dpM_x7BD1jEU2-R5LogJ3oG4ulm4yQ-e7jg"
        userIdx = 13
        Log.d("jwt", jwt)

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

//답변하기 부분 사진 추가버튼
        binding.answerImagePlusIv.setOnClickListener {
            val intent =
                Intent(this, AnswerCameraShootingActivity::class.java)
            activityResultLauncher.launch(intent)
//            var intent = Intent(Intent.ACTION_PICK)
//            intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//            intent.action = Intent.ACTION_GET_CONTENT
        }

        //답변하기 리사이클러뷰
        photoAdapter = PhotoAdapter(this, photoList)
        binding.answerImageReferenceVp.adapter = photoAdapter

        // 뷰페이저 어댑터 생성+답변하기에 질문쪽
        answerQuestionVPAdater = AnswerQuestionVPAdater(this, answerList)
        binding.answerImageVp.adapter = answerQuestionVPAdater
        binding.answerImageVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL // 방향을 가로로
        binding.answerIndicator.setViewPager(binding.answerImageVp)
        binding.answerIndicator.createIndicators(5, 0)



        firestore.collection("answer-images").addSnapshotListener {
                querySnapshot, FirebaseFIrestoreException ->
            if(querySnapshot!=null){
                for(dc in querySnapshot.documentChanges){
                    if(dc.type== DocumentChange.Type.ADDED){
                        var photo=dc.document.toObject(Photo::class.java)
                        photoList.add(photo)//url추가
                        images.add(photo.imageUrl)
                        count++
                        if (count>=5){
                            //추가버튼
                            binding.answerImagePlusIv.visibility=View.GONE
                        }
                        Log.d("count", count.toString())
                    }
                }
                photoAdapter.notifyDataSetChanged()
            }
        }

//필수 부분 작성되면 답변하기 누르기
        binding.answerAnswerIv.setOnClickListener {
            required()
            finish()
        }

        binding.answerBackIv.setOnClickListener {
//            firestore.collection("answer-images").document().delete().addOnSuccessListener {
//                Log.d("delete", "삭제성공")
//            }.addOnFailureListener {
//                Log.d("delete", "삭제실패")
//            }
            finish()
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

    private fun getAnswer(): Answer {  // view에서 받은 값들
        //questionIdx = intent.getLongExtra("questionIdx",0) // 받아온 질문 고유번호 -> api 호출시 넘김
        questionIdx= 272//의문?? 이러면 답변한 사람의 인덱스가 보내지는 건가?
        replyUrl=binding.answerAnswerCodeEt.text.toString()
        content=binding.answerExplanationEt.text.toString()

        Log.d("questionIdx : ", questionIdx.toString())
        Log.d("userIdx : ", userIdx.toString())
        Log.d("replyUrl : ", replyUrl)
        Log.d("content : ", content)
        Log.d("images", images.toString())

        return Answer(questionIdx, userIdx, replyUrl, content, images)
    }

    private fun required() {

        if (binding.answerExplanationEt.text.isEmpty()) {

            Toast.makeText(this, "설명을 작성해주세요.", Toast.LENGTH_SHORT).show()

            return
        }

        binding.answerAnswerIv.setImageResource(R.drawable.ic_click_answer)

        val answerService=AnswerService()

        answerService.setanswerView(this)
//jwt바꿔야함
        answerService.answer(jwt, getAnswer())
        Log.d("ANSWER/API","Hello")
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

