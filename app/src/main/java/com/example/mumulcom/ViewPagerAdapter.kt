package com.example.mumulcom //패키지명

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mumulcom.databinding.ItemSliderBinding


//코딩질문 개념질문 답변하기 작성자 어댑터
//  class ViewPagerAdapter(var context: Context, var photoList: ArrayList<Photo>, private var photoClickLister: PhotoClickLister) : RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>(){
//
//    var onItemClickListener:OnItemClickListener?=null
//
//
//    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
//        val imageIv: ImageView =itemView.findViewById<ImageView?>(R.id.imageSlider)
//        val plusIv: ImageView =itemView.findViewById<ImageView?>(R.id.view_Plus_Iv)
//        fun bind(photo:Photo){
//            Glide.with(context).load(photo.imageUrl).into(imageIv)
//            imageIv.setOnClickListener {
//                if(onItemClickListener!=null)
//                    onItemClickListener?.onItemClick(photo)
//            }
//        }
//
//    }
//
//
//    interface OnItemClickListener{
//        fun onItemClick(photo:Photo)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
//    : ViewHolder {
//        val view=LayoutInflater.from(context).inflate(R.layout.item_slider,parent,false)
//        return ViewHolder(view)
//
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//    var photo= photoList.get(position)
//    holder.bind(photo)
//        holder.imageIv.setOnClickListener { view ->
//            Toast.makeText(
//                view.context,
//                position.toString(),
//                Toast.LENGTH_SHORT
//            ).show()
//
//        }
//    if (position!=0){
//        holder.plusIv.visibility=View.VISIBLE
//    }
//        Log.d("size", photoList.size.toString())
//    holder.plusIv.setOnClickListener {
//
//    }
//        holder.plusIv.setOnClickListener { view ->
//            intArrayOf()
//        }
////        holder.bind(photoList[position])
////        holder.imageIv.setOnClickListener {
////            val intent=Intent(context, CodingCameraShootingActivity::class.java)
////        }
//
//    }
//
//
//    override fun getItemCount(): Int {
//        return photoList.size
//    }
//
//
//
//    @SuppressLint("NotifyDataSetChanged")
//    fun addlist(photoList: ArrayList<Photo>){
////        this.photoList.clear()
//        notifyDataSetChanged()
//    }
//
//}

class ViewPagerAdapter(var context: Context, var photoList: ArrayList<Photo>, private var photoClickLister: PhotoClickLister) :
    RecyclerView.Adapter<ViewPagerAdapter.PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PhotoViewHolder(
        ItemSliderBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        this.photoClickLister
    )


    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        var photo= photoList.get(position)
        holder.bindWithView(photo)

        if (photoList.size==2) {
            if (position == 1) {
                holder.photoplus.visibility = View.VISIBLE
            }
        }
        if (photoList.size==3) {
            if (position == 2) {
                holder.photoplus.visibility = View.VISIBLE
            }
        }
        if (photoList.size==4) {
            if (position == 3) {
                holder.photoplus.visibility = View.VISIBLE
            }
        }
        if (photoList.size==5) {
            if (position == 4) {
                holder.photoplus.visibility = View.VISIBLE
            }
        }
        if (photoList.size==6) {
            if (position == 5) {
                holder.photoplus.visibility = View.VISIBLE
                photo.imageUrl= ""
                if (photo.imageUrl=="") {
                    holder.photoImageView.setOnClickListener {
                    }
                }
            } else {
                holder.photoplus.visibility = View.INVISIBLE
            }
        }

        if (photo.imageUrl!="") {
            holder.photoImageView.setOnClickListener {
                    val intent = Intent(context, ImageActivity::class.java)
                    intent.putExtra("imgUrl", photo.imageUrl)
                    context.startActivity(intent)
            }
        }
        Log.d("urlurl", photo.imageUrl.toString())
        Log.d("urlurl/image", holder.photoImageView.toString())
    }

    inner class PhotoViewHolder(
        private val viewBinding: ItemSliderBinding,
        private var photoClickLister: PhotoClickLister
    ) :
        RecyclerView.ViewHolder(viewBinding.root) {

        val photoImageView = viewBinding.imageSlider
        val photoplus=viewBinding.viewPlusIv

        fun bindWithView(photo: Photo) {
            Glide
                .with(photoImageView.context)
                .load(photo.imageUrl)
                .into(photoImageView)

            if (photoList.size<=5) {
                photoplus.setOnClickListener {
                    this.photoClickLister.onPhotoClicked(photo)
                }
            }
            else{
                photoplus.setOnClickListener { view ->
                    Toast.makeText(view.context, "이미지는 최대 5개까지 넣을 수 있습니다", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return photoList.size
    }
}

