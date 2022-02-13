package com.example.mumulcom

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mumulcom.databinding.QuestionAnswerItemBinding

class RepliesForQuestionAdapter(val context: Context):RecyclerView.Adapter<RepliesForQuestionAdapter.ViewHolder>() {


    private val replyList = ArrayList<Reply>()
    private var isLike : Boolean = false // 좋아요
    private var isSelect : Boolean = false // 체택하기
    private lateinit var imageViewPagerAdapter: ImageViewPagerAdapter


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: QuestionAnswerItemBinding =  QuestionAnswerItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(replyList[position])
        holder.binding.viewPager.setOnClickListener {
            // todo 이미지 확대
        }
        // 좋아요 처리
        holder.binding.itemLikeIv.setOnClickListener {
            isLike = !isLike
            if(isLike){
                holder.binding.itemLikeIv.setImageResource(R.drawable.ic_liked)
                // todo api 호출
            }else
                holder.binding.itemLikeIv.setImageResource(R.drawable.ic_like)
        }
        // 채택하기 처리
        holder.binding.selectAnswerTv.setOnClickListener {
            holder.binding.selectAnswerTv.visibility = View.GONE
            holder.binding.selectAnswerIv.visibility = View.VISIBLE
        }
        holder.binding.selectAnswerIv.setOnClickListener {
            holder.binding.selectAnswerIv.visibility = View.GONE
            holder.binding.selectAnswerTv.visibility = View.VISIBLE
        }

    }

    override fun getItemCount(): Int {
        return replyList.size
    }




    @SuppressLint("NotifyDataSetChanged")
    fun addQuestions(replies:ArrayList<Reply> ){
        this.replyList.clear()
        this.replyList.addAll(replies)

        notifyDataSetChanged()
    }



    inner class ViewHolder(val binding:QuestionAnswerItemBinding):RecyclerView.ViewHolder(binding.root){

        fun bind(reply: Reply){
            Glide.with(context).load(reply.profileImgUrl).into(binding.profileIv) // 프로필 이미지
            binding.nickNameTv.text = reply.nickname // 닉네임
            binding.createdAtTv.text = reply.createdAt // 작성 날짜

            if(reply.replyUrl==null){
//                binding.replyUrl.visibility = View.GONE
                binding.replyUrl.text = "참고 링크 : 없음."// 참고 링크
            }else{
                binding.replyUrl.text = "참고 링크 : "+reply.replyUrl // 참고 링크
            }

            binding.contentTv.text = reply.content // 답변 내용
            binding.contentTv.text = reply.reReplyCount.toString() // 대댓글수
            binding.itemLikeTv.text = reply.likeCount.toString() // 좋아요 수

            // 답변 이미지 viewpager 연결
            imageViewPagerAdapter = ImageViewPagerAdapter(context)
            if(reply.replyImgUrl.size==0){
                binding.itemL13.visibility = View.GONE
            }else{
                imageViewPagerAdapter.addQuestions(reply.replyImgUrl)
                binding.viewPager.adapter = imageViewPagerAdapter
                binding.indicator.setViewPager(binding.viewPager)
            }



        }

    }



}