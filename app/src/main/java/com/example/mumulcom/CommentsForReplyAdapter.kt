package com.example.mumulcom

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mumulcom.databinding.ReplyCommentItemBinding

class CommentsForReplyAdapter(val context: Context): RecyclerView.Adapter<CommentsForReplyAdapter.ViewHolder>() {


    private val commentList = ArrayList<Comment>()


    @SuppressLint("NotifyDataSetChanged")
    fun addComments(comments:ArrayList<Comment> ){
        this.commentList.clear()
        this.commentList.addAll(comments)
        notifyDataSetChanged()
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding:ReplyCommentItemBinding = ReplyCommentItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(commentList[position])

        holder.binding.replyImgIv.setOnClickListener {
            val intent = Intent(context,ImageActivity::class.java)
            intent.putExtra("imgUrl",commentList[position].imageUrl)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    inner class ViewHolder(val binding:ReplyCommentItemBinding):RecyclerView.ViewHolder(binding.root){

        fun bind(comment: Comment){

            binding.nickNameTv.text = comment.nickname // 닉네임
            Glide.with(context).load(comment.profileUrl).into(binding.profileIv)
            binding.createdAtTv.text = comment.createdAt
            binding.contentTv.text = comment.content
            if(comment.imageUrl!==null){
                Glide.with(context).load(comment.imageUrl).into(binding.replyImgIv)
            }else{
                binding.replyImgIv.visibility = View.GONE
            }

        }

    }
}