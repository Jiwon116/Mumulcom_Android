package com.example.mumulcom

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mumulcom.databinding.QuestionListItemBinding

class TabQuestionAdapter(var context: Context?)
    : RecyclerView.Adapter<TabQuestionAdapter.TabQuestionViewHolder>() {

    private val questionList = ArrayList<Question>()

    // 클릭 인터페이스 정의
    interface TabQuestionClickListener{
        fun onItemClick(question: Question)
    }

    // 리스너 객체를 전달받는 함수랑 리스너 객체를 저장할 변수
    private lateinit var tabQuestionClickListener : TabQuestionAdapter.TabQuestionClickListener

    fun setTabQuestionClickListener(tabQuestionClickListener: TabQuestionAdapter.TabQuestionClickListener){
        this.tabQuestionClickListener = tabQuestionClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabQuestionViewHolder {
        val binding:QuestionListItemBinding = QuestionListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TabQuestionViewHolder(binding)
    }

    override fun onBindViewHolder(holderQuestion: TabQuestionAdapter.TabQuestionViewHolder, position: Int) {
        holderQuestion.bind(questionList[position])
        //recyclerView 의 각 아이템을 클릭할때
        holderQuestion.itemView.setOnClickListener {
            tabQuestionClickListener.onItemClick(questionList[position])
        }
    }

    override fun getItemCount(): Int {
        return questionList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addTabQuestions(question:ArrayList<Question>){
        this.questionList.clear()
        this.questionList.addAll(question)

        notifyDataSetChanged()
    }

    inner class TabQuestionViewHolder(val binding: QuestionListItemBinding):RecyclerView.ViewHolder(binding.root){
        // # 문자열 바로 사용해서 추가함
        @SuppressLint("SetTextI18n")
        fun bind(question: Question) {
            context?.let { Glide.with(it).load(question.profileImgUrl).into(binding.itemIconIv) }
            binding.itemNameTv.text = question.nickname
            binding.itemDateTv.text = question.createdAt
            binding.itemTitleTv.text = question.title
            if(question.bigCategoryName!=null){
                binding.itemBigCategoryTv.text = "#" + question.bigCategoryName
            }
            if(question.smallCategoryName!=null){
                binding.itemSmallCategoryTv.text = "#" + question.smallCategoryName
            }
            binding.itemCommentTv.text = question.replyCount.toString()
            binding.itemLikeTv.text = question.likeCount.toString()
        }

    }
}