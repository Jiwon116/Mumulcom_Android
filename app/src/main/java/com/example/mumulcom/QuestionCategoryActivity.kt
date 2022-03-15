package com.example.mumulcom

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mumulcom.databinding.ActivityQuestioncategoryBinding


class QuestionCategoryActivity : AppCompatActivity() {

    lateinit var binding: ActivityQuestioncategoryBinding

    private var codingQuestionCheck : Boolean = false // default 값
    private var conceptQuestionCheck : Boolean = false // default 값
    private var isSelect: Boolean=false // default 값

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestioncategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setSelectCompletion()//선택완료버튼
        initView()  // view 초기화
        initCodingOrConceptQuestionButton() // 코딩 질문 & 개념 질문 버튼 초기화

    }

    private fun initView(){

        if (codingQuestionCheck==false&&conceptQuestionCheck==false){
            isSelect=false
            setSelectCompletion()
            binding.questioncategorySelectCompletionIv.setOnClickListener {
                Toast.makeText(this@QuestionCategoryActivity, "질문 유형을 선택해주세요", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        // 코딩 질문 버튼
        binding.questioncategoryCodingQuestionIv.setOnClickListener {
            codingQuestionCheck = true
            conceptQuestionCheck = false
            initCodingOrConceptQuestionButton()
            isSelect=true
            setSelectCompletion()
            binding.questioncategorySelectCompletionIv.setOnClickListener {
                startActivity(Intent(this, CheckCodingQuestionActivity::class.java))
                finish()
            }
        }


        // 개념 질문 버튼
        binding.questioncategoryConceptQuestionIv.setOnClickListener {
            conceptQuestionCheck= true
            codingQuestionCheck = false
            isSelect=true
            initCodingOrConceptQuestionButton()
            setSelectCompletion()
            binding.questioncategorySelectCompletionIv.setOnClickListener {
                startActivity(Intent(this, CheckConceptQuestionActivity::class.java))
                finish()
            }
        }

    }

    fun setSelectCompletion(){
        if(isSelect){
            binding.questioncategorySelectCompletionIv.setImageResource(R.drawable.ic_select_complete)
        }else{
            binding.questioncategorySelectCompletionIv.setImageResource(R.drawable.ic_bf_selectcompletion)
        }
    }




    fun initCodingOrConceptQuestionButton(){
        if(codingQuestionCheck){
            binding.questioncategoryCodingQuestionIv.setImageResource(R.drawable.coding_question_check_img)
            binding.questioncategoryCodingQuestionIv.isEnabled = false // 이미 선택되었으면 선택 못함.
            // 코딩 질문만 보여줌
        }else{
            binding.questioncategoryCodingQuestionIv.setImageResource(R.drawable.coding_question_img)
            binding.questioncategoryCodingQuestionIv.isEnabled = true
        }

        if(conceptQuestionCheck){
            binding.questioncategoryConceptQuestionIv.setImageResource(R.drawable.ic_concept_question_check_img)
            binding.questioncategoryConceptQuestionIv.isEnabled = false // 이미 선택되었으면 선택 못함.
            // 개념 질문만 보여줌

        }else{
            binding.questioncategoryConceptQuestionIv.setImageResource(R.drawable.ic_concept_question_img)
            binding.questioncategoryConceptQuestionIv.isEnabled = true
        }
    }
}