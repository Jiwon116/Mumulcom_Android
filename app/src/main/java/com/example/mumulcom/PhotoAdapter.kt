package com.example.mumulcom

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mumulcom.databinding.ItemPhotoBinding
import com.example.mumulcom.databinding.ItemSliderBinding

//답변하기 작성자가 넣는 이미지
class PhotoAdapter(var context: Context, private val photoList:ArrayList<Photo>) : RecyclerView.Adapter<PhotoAdapter.ViewHolder>(){

    var onItemClickListener:OnItemClickListener?=null

    inner class ViewHolder(val binding: ItemPhotoBinding) : RecyclerView.ViewHolder(binding.root){
        var imageIv: ImageView =itemView.findViewById(R.id.itemPhoto)

        fun bind(photo:Photo) {

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

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): PhotoAdapter.ViewHolder {
        val binding: ItemPhotoBinding= ItemPhotoBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int=photoList.size

    override fun onBindViewHolder(holder: PhotoAdapter.ViewHolder, position: Int) {
        var photo=photoList[position]
        holder.bind(photo)
    }
}