package com.example.mumulcom

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.mumulcom.databinding.ActivityAnswerBinding


class AnswerActivity:AppCompatActivity() {
    lateinit var binding: ActivityAnswerBinding

    private var albumDatas=ArrayList<Album>()//뷰페이저

    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnswerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 화면 배경 누르면 키보드 사라지기
        binding.answerBack.setOnClickListener {
            CloseKeyboard()
        }

//답변하기 부분 사진 추가버튼
        binding.answerImageReferenceIv.setOnClickListener {
            startActivity(Intent(this, AnswerCameraShootingActivity::class.java))
            binding.answerImageReference22Iv.visibility=View.VISIBLE
            binding.answerImageReference3Iv.visibility=View.VISIBLE
            binding.answerImageReference22Iv.setOnClickListener {
                startActivity(Intent(this, AnswerCameraShootingActivity::class.java))
                binding.answerImageReference33Iv.visibility=View.VISIBLE
                binding.answerImageReference4Iv.visibility=View.VISIBLE
                binding.answerImageReference33Iv.setOnClickListener {
                    startActivity(Intent(this, AnswerCameraShootingActivity::class.java))
                    binding.answerImageReference44Iv.visibility=View.VISIBLE
                    binding.answerImageReference5Iv.visibility=View.VISIBLE
                    binding.answerImageReference5Iv.setOnClickListener {
                        startActivity(Intent(this, AnswerCameraShootingActivity::class.java))
                    }
                }
            }
        }
//필수 부분 작성되면 답변하기 누르기
        binding.answerAnswerIv.setOnClickListener {
            required()
        }


//임시뷰페이저
        albumDatas.apply {
//            var photoUri: Uri? = null
//            if (photoUri!=null) {
//                add(Album(Uri.parse(intent.getStringExtra("uri"))))
//            }
//            add(Album(com.example.mumulcom.R.drawable.ic_gallery))
//            add(Album(com.example.mumulcom.R.drawable.ic_web))
//            add(Album(com.example.mumulcom.R.drawable.ic_gallery))
//            add(Album(com.example.mumulcom.R.drawable.ic_gallery))
//            add(Album(com.example.mumulcom.R.drawable.ic_gallery))
        }


        //임시뷰페이저
        albumDatas.apply {
//            var uri = getIntent().getStringExtra("imageUri");
//            Uri.parse("imageUri")
//            add(Album(Uri.parse("imageUri")))
//            add(Album(Uri.parse("imageUri")))
//            add(Album(Uri.parse("imageUri")))
//            add(Album(Uri.parse("imageUri")))
//            add(Album(Uri.parse("imageUri")))
            add(Album(com.example.mumulcom.R.drawable.ic_gallery))
            add(Album(com.example.mumulcom.R.drawable.ic_web))
            add(Album(com.example.mumulcom.R.drawable.ic_gallery))
            add(Album(com.example.mumulcom.R.drawable.ic_gallery))
            add(Album(com.example.mumulcom.R.drawable.ic_gallery))
        }

        val ViewPagerAdapter=ViewPagerAdapter(albumDatas)
        binding.answerImageVp.adapter=ViewPagerAdapter// 뷰페이저 어댑터 생성
        binding.answerImageVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL // 방향을 가로로
        binding.answerIndicator.setViewPager(binding.answerImageVp)
        binding.answerIndicator.createIndicators(5,0);
    }

    // 키보드 사라지는 함수
    fun CloseKeyboard() {
        var view = this.currentFocus

        if(view != null) {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun required() {

        if (binding.answerExplanationEt.text.isEmpty()) {

            Toast.makeText(this, "설명을 작성해주세요.", Toast.LENGTH_SHORT).show()

            return
        }

        binding.answerAnswerIv.setImageResource(R.drawable.ic_click_answer)
    }


}