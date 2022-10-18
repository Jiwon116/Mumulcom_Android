package com.example.mumulcom

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mumulcom.databinding.ItemAnnounceBinding

class AnnounceAdapter(val context: Context) : RecyclerView.Adapter<AnnounceAdapter.AnnounceViewHolder>() {


    private val announceList = ArrayList<Announce>()

    // 클릭 인터페이스 정의
    interface AnnounceClickListener{
        fun onItemClick(announce: Announce)
    }

    // 리스너 객체를 전달받는 함수랑 리스너 객체를 저장할 변수
    private lateinit var announceClickListener : AnnounceClickListener

    fun setAnnounceClickListener(announceClickListener: AnnounceClickListener){
        this.announceClickListener = announceClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnnounceViewHolder {
        val binding: ItemAnnounceBinding = ItemAnnounceBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AnnounceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnnounceAdapter.AnnounceViewHolder, position: Int) {
        holder.bind(announceList[position])
        //recyclerView 의 각 아이템을 클릭할때
        holder.itemView.setOnClickListener {
            announceClickListener.onItemClick(announceList[position])
        }
    }

    override fun getItemCount(): Int {
        return announceList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addAnnounce(announce: ArrayList<Announce>){
        this.announceList.clear()
        this.announceList.addAll(announce)

        notifyDataSetChanged()
    }

    inner class AnnounceViewHolder(val binding: ItemAnnounceBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(announce: Announce) {
            binding.itemAnnounceTitleTv.text = announce.title
            binding.itemAnnounceCreatedAtTv.text = announce.createdAt
        }

    }
}