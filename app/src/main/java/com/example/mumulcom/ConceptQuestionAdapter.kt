package com.example.mumulcom

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mumulcom.databinding.QuestionSearchListItemBinding

class ConceptQuestionAdapter(var context: Context)
    :RecyclerView.Adapter<ConceptQuestionAdapter.ConceptQuestionViewHolder>() {

    private val conceptQuestionList = ArrayList<ConceptQuestion>()

    // 클릭 인터페이스 정의
    interface ConceptQuestionClickListener{
        fun onItemClick(conceptQuestion: ConceptQuestion)
    }

    // 리스너 객체를 전달받는 함수랑 리스너 객체를 저장할 변수
    private lateinit var conceptQuestionClickListener : ConceptQuestionAdapter.ConceptQuestionClickListener

    fun setConceptQuestionClickListener(conceptQuestionClickListener: ConceptQuestionAdapter.ConceptQuestionClickListener){
        this.conceptQuestionClickListener = conceptQuestionClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConceptQuestionAdapter.ConceptQuestionViewHolder {
        val binding:QuestionSearchListItemBinding = QuestionSearchListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ConceptQuestionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConceptQuestionAdapter.ConceptQuestionViewHolder, position: Int) {
        holder.bind(conceptQuestionList[position])
        //recyclerView 의 각 아이템을 클릭할때
        holder.itemView.setOnClickListener {
            conceptQuestionClickListener.onItemClick(conceptQuestionList[position])
        }
    }

    override fun getItemCount(): Int {
        return conceptQuestionList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addConceptQuestions(conceptQuestion:ArrayList<ConceptQuestion>){
        this.conceptQuestionList.clear()
        this.conceptQuestionList.addAll(conceptQuestion)
        notifyDataSetChanged()
    }

    inner class ConceptQuestionViewHolder(val binding: QuestionSearchListItemBinding):RecyclerView.ViewHolder(binding.root){

        // # 문자열 바로 사용해서 추가함
        @SuppressLint("SetTextI18n")
        fun bind(conceptQuestion: ConceptQuestion) {
            Glide.with(context).load(conceptQuestion.profileImgUrl).into(binding.searchItemProfileIv);
            binding.searchItemNicknameTv.text = conceptQuestion.nickname
            binding.searchItemDateTv.text = conceptQuestion.createdAt
            binding.searchItemTitleTv.text = conceptQuestion.title
            binding.searchItemContentTv.text = conceptQuestion.content
            if(conceptQuestion.bigCategoryName!=null){
                binding.searchItemTag2Tv.text = "#" + conceptQuestion.bigCategoryName
            }
            if(conceptQuestion.smallCategoryName!=null){
                binding.searchItemTag2Tv.text = "#" + conceptQuestion.smallCategoryName
            }
            binding.searchItemCommentTv.text = conceptQuestion.replyCount.toString()
            binding.searchItemLikeTv.text = conceptQuestion.likeCount.toString()
        }

    }
}