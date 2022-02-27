package com.example.test //패키지명

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mumulcom.Album
import com.example.mumulcom.Photo
import com.example.mumulcom.R

//코딩질문 개념질문 답변하기 작성자 어댑터
class AnswerQuestionVPAdater(var context: Context, var answerList: ArrayList<Album>) : RecyclerView.Adapter<AnswerQuestionVPAdater.ViewHolder>(){

    var onItemClickListener:OnItemClickListener?=null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var imageIv: ImageView =itemView.findViewById(R.id.imageSlider)

        fun bind(album: Album){
            Glide.with(context).load(album.imageUrl).into(imageIv)
            imageIv.setOnClickListener {
                if(onItemClickListener!=null)
                    onItemClickListener?.onItemClick(album)
            }
        }
    }

    interface OnItemClickListener{
        fun onItemClick(album: Album)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerQuestionVPAdater.ViewHolder {
        var view=LayoutInflater.from(context).inflate(R.layout.item_slider,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return answerList.size
    }
    override fun onBindViewHolder(holder: AnswerQuestionVPAdater.ViewHolder, position: Int) {
        var album= answerList.get(position)
        holder.bind(album)
    }
}