package com.example.mumulcom

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mumulcom.databinding.ItemSliderBinding


class ViewPagerAdapter(private val albumList:ArrayList<Album>):RecyclerView.Adapter<ViewPagerAdapter.PagerViewHolder>() {

    inner class PagerViewHolder(val binding: ItemSliderBinding):RecyclerView.ViewHolder(binding.root){

        fun bind(album: Album){
            binding.imageSlider.setImageResource(album.albumImg!!)
        }

    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewPagerAdapter.PagerViewHolder {
        val binding: ItemSliderBinding = ItemSliderBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PagerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewPagerAdapter.PagerViewHolder, position: Int) {
        holder.bind(albumList[position])
    }

    override fun getItemCount(): Int {
        return albumList.size
    }
}