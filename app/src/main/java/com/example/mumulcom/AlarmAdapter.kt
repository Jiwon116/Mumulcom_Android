package com.example.mumulcom

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mumulcom.databinding.ItemAlarmBinding
import com.example.mumulcom.databinding.ItemAlarmHeaderBinding

class AlarmAdapter(private val items: List<AlarmListItem>, var context: Context?)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val alarmList = ArrayList<Alarm>()

    // 알람을 클릭하면 -> 해당 질문으로 가기
    interface AlarmClickListener{
        fun onItemClick(alarm: AlarmGeneralItem)
    }

    // 리스너 객체를 전달받는 함수랑 리스너 객체를 저장할 변수
    private lateinit var alarmClickListener : AlarmAdapter.AlarmClickListener

    fun setAlarmClickListener(alarmClickListener: AlarmAdapter.AlarmClickListener){
        this.alarmClickListener = alarmClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            AlarmListItem.TYPE_DATE -> AlarmDateViewHolder(ItemAlarmHeaderBinding.inflate(LayoutInflater.from(parent.context),parent,false))
            else -> AlarmViewHolder(ItemAlarmBinding.inflate(LayoutInflater.from(parent.context),parent,false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            AlarmListItem.TYPE_DATE -> (holder as AlarmDateViewHolder).bind(
                items[position] as AlarmDateItem
            )
            AlarmListItem.TYPE_GENERAL ->
            {
                (holder as AlarmViewHolder).bind(items[position] as AlarmGeneralItem)
                //recyclerView 의 각 아이템을 클릭할때
                holder.itemView.setOnClickListener {
                    alarmClickListener.onItemClick(items[position] as AlarmGeneralItem)
                }
            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].itemType
    }

    override fun getItemCount(): Int {
        return items.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addAlarm(alarm:ArrayList<Alarm>){
        this.alarmList.clear()
        this.alarmList.addAll(alarm)
        notifyDataSetChanged()
    }

    inner class AlarmDateViewHolder(val binding: ItemAlarmHeaderBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(alarmDate: AlarmDateItem) {
            binding.itemAlarmHeader.text = alarmDate.diffTime
        }
    }

    inner class AlarmViewHolder(val binding: ItemAlarmBinding):RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(alarmItem: AlarmGeneralItem) {
            //binding.root.setOnClickListener { alarmClickListener.onItemClick(alarmItem) }
            // 1 -> 내가 한 질문에 답변 / 3 -> 내가 한 스크랩에 답변 / 4 -> 내가 한 답변에 답변 (대댓글)
            if (alarmItem.noticeCategoryIdx == 1 || alarmItem.noticeCategoryIdx == 3 || alarmItem.noticeCategoryIdx == 4) {
                binding.itemAlarmIconIv.setImageResource(R.drawable.ic_message_select)
            }
            // 2 -> 내가 한 질문을 좋아요 / 5 -> 내가 한 답변을 좋아요
            else if (alarmItem.noticeCategoryIdx == 2 || alarmItem.noticeCategoryIdx == 5) {
                binding.itemAlarmIconIv.setImageResource(R.drawable.ic_alarm_like)
            }
            // 6 -> 답변 채택
            else {
                binding.itemAlarmIconIv.setImageResource(R.drawable.ic_answer_face)
                //context?.let { Glide.with(it).load(alarmItem.profileImgUrl).into(binding.itemAlarmIconIv)}   // 이외의 것은 내 프로필 사진 -> 임의로!!!
            }
            val content = alarmItem.noticeContent + " : " + alarmItem.title
            binding.itemAlarmContentTv.text = content
        }
    }
}