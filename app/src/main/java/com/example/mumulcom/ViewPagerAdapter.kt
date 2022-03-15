package com.example.mumulcom //패키지명

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.lang.String
import kotlin.Int


//코딩질문 개념질문 답변하기 작성자 어댑터
  class ViewPagerAdapter(var context: Context, var photoList: ArrayList<Photo>) : RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>(){

    var onItemClickListener:OnItemClickListener?=null
    val NUM_PAGES = 5

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val imageIv: ImageView =itemView.findViewById<ImageView?>(R.id.imageSlider)
        val plusIv: ImageView =itemView.findViewById<ImageView?>(R.id.view_Plus_Iv)
        fun bind(photo:Photo){
            Glide.with(context).load(photo.imageUrl).into(imageIv)
            imageIv.setOnClickListener {
                if(onItemClickListener!=null)
                    onItemClickListener?.onItemClick(photo)
            }
        }

    }


    interface OnItemClickListener{
        fun onItemClick(photo:Photo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(context).inflate(R.layout.item_slider,parent,false)
        val plusIv=R.id.view_Plus_Iv
        return ViewHolder(view)

    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    var photo= photoList.get(position)
    holder.bind(photo)
        holder.imageIv.setOnClickListener { view ->
            Toast.makeText(
                view.context,
                position.toString(),
                Toast.LENGTH_SHORT
            ).show()
        }
    if (position!=0){
        holder.plusIv.visibility=View.VISIBLE
    }
        Log.d("size", photoList.size.toString())
    holder.plusIv.setOnClickListener {

    }
        holder.plusIv.setOnClickListener { view ->
            intArrayOf()
        }
//        holder.bind(photoList[position])
//        holder.imageIv.setOnClickListener {
//            val intent=Intent(context, CodingCameraShootingActivity::class.java)
//        }

    }


    override fun getItemCount(): Int {
        return photoList.size
    }



    @SuppressLint("NotifyDataSetChanged")
    fun addlist(photoList: ArrayList<Photo>){
//        this.photoList.clear()
        notifyDataSetChanged()
    }

}

