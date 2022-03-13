package com.example.test //패키지명

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mumulcom.ImageActivity
import com.example.mumulcom.Photo
import com.example.mumulcom.R


//코딩질문 개념질문 답변하기 작성자 어댑터
class ViewPagerAdapter(var context: Context, var photoList: ArrayList<Photo>) : RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>(){

    var onItemClickListener:OnItemClickListener?=null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val imageIv: ImageView =itemView.findViewById<ImageView?>(R.id.imageSlider)

        fun bind(photo:Photo){
            Glide.with(context).load(photo.imageUrl).into(imageIv)
            imageIv.setOnClickListener {
                if(onItemClickListener!=null)
                    onItemClickListener?.onItemClick(photo)
            }
            imageIv
                .bringToFront()
        }

    }

    interface OnItemClickListener{
        fun onItemClick(photo:Photo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(context).inflate(R.layout.item_slider,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return photoList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var photo= photoList.get(position)
        holder.bind(photo)

    }

    @SuppressLint("NotifyDataSetChanged")
    fun addlist(photoList: ArrayList<Photo>){
//        this.photoList.clear()
        this.photoList.add(Photo())
        notifyDataSetChanged()
    }

}