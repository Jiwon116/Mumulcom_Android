package com.example.mumulcom

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mumulcom.databinding.RecentQuestionListItemBinding

class RecentQuestionAdapter(val context: Context):RecyclerView.Adapter<RecentQuestionAdapter.QuestionViewHolder>() {

    private val questionList = ArrayList<Question>()

    // 외부 작업을 위한 클릭 인터페이스 정의
    interface QuestionClickListener {
        fun onItemClick(question: Question)
    }
    // 리스너 객체를 전달받는 함수랑 리스너 객체를 저장할 변수
    private lateinit var questionClickListener : QuestionClickListener

    fun setQuestionClickListener(questionClickListener: QuestionClickListener){
        this.questionClickListener = questionClickListener
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): QuestionViewHolder {
        val binding:RecentQuestionListItemBinding = RecentQuestionListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return QuestionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.bind(questionList[position])
        // recyclerview 의 각 아이템을 클릭할때
        holder.itemView.setOnClickListener {
            questionClickListener.onItemClick(questionList[position])
        }
        holder.binding.itemLikeIv.setOnClickListener {
            // TODO 좋아요 이미지를 눌렀을때 -> 하트 색 바뀌고 좋아요 1증가 , 서버에 정보 전달.
        }
    }

    override fun getItemCount(): Int {
        return questionList.size
    }





    @SuppressLint("NotifyDataSetChanged")
    fun addQuestions(questions:ArrayList<Question> ){
        this.questionList.clear()
        this.questionList.addAll(questions)

        notifyDataSetChanged()
    }


    inner class QuestionViewHolder(val binding: RecentQuestionListItemBinding):RecyclerView.ViewHolder(binding.root){


        fun bind(question: Question){

            Glide.with(context).load(question.profileImgUrl).into(binding.itemIconIv) // 프로필 이미지
            binding.itemNameTv.text = question.nickname // 닉네임
            binding.itemDateTv.text = question.createdAt // 질문한 날짜
            binding.itemTitleTv.text = question.title // 질문 제목
            binding.itemCommentTv.text = question.replyCount.toString() // 답변 갯수
            binding.itemLikeTv.text  = question.likeCount.toString() // 좋아요 갯수


            binding.itemBigCategoryTv.text = "#"+question.bigCategoryName

            if(question.smallCategoryName!=null){
                binding.itemSmallCategoryTv.text = "#"+question.smallCategoryName
            }




        }

    }
}