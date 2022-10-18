package com.example.mumulcom

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mumulcom.databinding.ItemSliderBinding

// 질문 상세 보기 페이지에서 이미지들 보여줄때 사용하는 adapter
class ImageViewPagerAdapter(val context: Context):RecyclerView.Adapter<ImageViewPagerAdapter.PagerViewHolder>() {


    private val imgUrlList = ArrayList<String>()


    inner class PagerViewHolder(val binding: ItemSliderBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(url: String){
            Glide.with(context).load(url).into(binding.imageSlider)
        }

    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PagerViewHolder {
        val binding: ItemSliderBinding = ItemSliderBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PagerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        Log.d("이미지test","어댑터로 이미지 들어옴 "+imgUrlList[position])
        holder.bind(imgUrlList[position])

        holder.binding.imageSlider.setOnClickListener {
            // 각 이미지를 클릭했을때  해당 이미지를 새 창에 띄워서 확대/축소 기능
            val intent = Intent(context,ImageActivity::class.java)
            intent.putExtra("imgUrl",imgUrlList[position])
            context.startActivity(intent)

        }
    }

    override fun getItemCount(): Int {
        return imgUrlList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addQuestions(imgUrl:ArrayList<String>){
        this.imgUrlList.clear()
        this.imgUrlList.addAll(imgUrl)

        notifyDataSetChanged()
    }
}