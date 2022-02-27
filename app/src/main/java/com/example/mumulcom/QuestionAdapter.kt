package com.example.mumulcom

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mumulcom.databinding.QuestionListItemBinding

class QuestionAdapter(val context: Context):RecyclerView.Adapter<QuestionAdapter.ViewHolder>() {

    private val questionList = ArrayList<Question>()


    // 클릭 인터페이스 정의
    interface QuestionClickListener{
        fun onItemClick(question: Question)
    }

    // 리스너 객체를 전달받는 함수랑 리스너 객체를 저장할 변수
    private lateinit var questionClickListener : QuestionClickListener

    fun setQuestionClickListener(questionClickListener: QuestionClickListener){
        this.questionClickListener = questionClickListener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionAdapter.ViewHolder {
        val binding: QuestionListItemBinding = QuestionListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuestionAdapter.ViewHolder, position: Int) {
        holder.bind(questionList[position])
        //recyclerView 의 각 아이템을 클릭할때
        holder.itemView.setOnClickListener {
            questionClickListener.onItemClick(questionList[position])
        }
    }

    override fun getItemCount(): Int {
        return questionList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addQuestions(questions:ArrayList<Question>){
        this.questionList.clear()
        this.questionList.addAll(questions)

        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding:QuestionListItemBinding):RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun bind(question:Question){

            Glide.with(context).load(question.profileImgUrl).into(binding.itemIconIv)  // 프로필 이미지

            binding.itemTitleTv.text = question.title // 질문 제목
            binding.itemNameTv.text = question.nickname // 작성자 닉네임
            binding.itemDateTv.text = question.createdAt // 질문한 날짜

            binding.itemCommentTv.text = question.replyCount.toString() // 댓글 수
            binding.itemLikeTv.text = question.likeCount.toString() // 좋아요 갯수


            binding.itemBigCategoryTv.text = "#"+question.bigCategoryName

            if(question.smallCategoryName!=null){
                binding.itemSmallCategoryTv.text = "#"+question.smallCategoryName
            } else {
                binding.itemSmallCategoryTv.visibility = View.GONE
            }
        }
    }
}