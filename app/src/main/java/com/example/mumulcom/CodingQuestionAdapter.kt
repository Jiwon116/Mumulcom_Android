package com.example.mumulcom

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import android.content.Context
import com.example.mumulcom.databinding.QuestionSearchListItemBinding

class CodingQuestionAdapter(var context: Context)
    :RecyclerView.Adapter<CodingQuestionAdapter.CodingQuestionViewHolder>() {

    private val codingQuestionList = ArrayList<CodingQuestion>()

    // 클릭 인터페이스 정의
    interface CodingQuestionClickListener{
        fun onItemClick(codingQuestion: CodingQuestion)
    }

    // 리스너 객체를 전달받는 함수랑 리스너 객체를 저장할 변수
    private lateinit var codingQuestionClickListener : CodingQuestionAdapter.CodingQuestionClickListener

    fun setCodingQuestionClickListener(codingQuestionClickListener: CodingQuestionAdapter.CodingQuestionClickListener){
        this.codingQuestionClickListener = codingQuestionClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CodingQuestionAdapter.CodingQuestionViewHolder {
        val binding:QuestionSearchListItemBinding = QuestionSearchListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CodingQuestionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CodingQuestionAdapter.CodingQuestionViewHolder, position: Int) {
        holder.bind(codingQuestionList[position])
        //recyclerView 의 각 아이템을 클릭할때
        holder.itemView.setOnClickListener {
            codingQuestionClickListener.onItemClick(codingQuestionList[position])
        }
    }

    override fun getItemCount(): Int {
        return codingQuestionList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addCodingQuestions(codingQuestions:ArrayList<CodingQuestion>){
        this.codingQuestionList.clear()
        this.codingQuestionList.addAll(codingQuestions)

        notifyDataSetChanged()
    }

    inner class CodingQuestionViewHolder(val binding: QuestionSearchListItemBinding):RecyclerView.ViewHolder(binding.root){

        // # 문자열 바로 사용해서 추가함
        @SuppressLint("SetTextI18n")
        fun bind(codingQuestion: CodingQuestion) {
            Glide.with(context).load(codingQuestion.profileImgUrl).into(binding.searchItemProfileIv);
            binding.searchItemNicknameTv.text = codingQuestion.nickname
            binding.searchItemDateTv.text = codingQuestion.createdAt
            binding.searchItemTitleTv.text = codingQuestion.title
            binding.searchItemContentTv.text = codingQuestion.currentError
            if(codingQuestion.bigCategoryName!=null){
                binding.searchItemTag1Tv.text = "#"+codingQuestion.bigCategoryName
            }
            if(codingQuestion.smallCategoryName!=null){
                binding.searchItemTag2Tv.text = "#"+codingQuestion.smallCategoryName
            }
            binding.searchItemCommentTv.text = codingQuestion.replyCount.toString()
            binding.searchItemLikeTv.text = codingQuestion.likeCount.toString()
        }

    }
}