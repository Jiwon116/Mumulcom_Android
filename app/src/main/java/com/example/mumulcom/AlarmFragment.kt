package com.example.mumulcom

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mumulcom.databinding.FragmentAlarmBinding

class AlarmFragment : Fragment(), AlarmView {
    lateinit var binding: FragmentAlarmBinding

    private var jwt: String = ""    // jwt
    private var userIdx: Long = 0   // 유저 인덱스

    private lateinit var alarmAdapter: AlarmAdapter
    private val consolidatedList = mutableListOf<AlarmListItem>()

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlarmBinding.inflate(inflater, container, false)

        jwt = context?.let { getJwt(it) }.toString()
        userIdx = context?.let { getUserIdx(it) }!!

        binding.alarmItemListRv.setHasFixedSize(true)

        initRecyclerview()

        return binding.root
    }

    private fun initRecyclerview(){
        getAlarm(jwt, userIdx)
        alarmAdapter = AlarmAdapter(consolidatedList, context)
        alarmAdapter.setAlarmClickListener(object: AlarmAdapter.AlarmClickListener {
            override fun onItemClick(alarm: AlarmGeneralItem) {
                Log.d("bigCategoryName",  alarm.bigCategoryName)
                Log.d("questionIdx", alarm.questionIdx.toString())
                Log.d("type", alarm.type.toString())
                startQuestionDetailActivity(alarm)  // 질문 상세 보기 페이지로 이동
            }
        })
        binding.alarmItemListRv.adapter = alarmAdapter
        binding.alarmItemListRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    private fun startQuestionDetailActivity(alarm: AlarmGeneralItem){
        val intent = Intent(requireContext(), QuestionDetailActivity::class.java)
        intent.putExtra("bigCategoryName", alarm.bigCategoryName) // 상위 카테고리명 넘김
        intent.putExtra("questionIdx", alarm.questionIdx) // 질문 고유 번호 넘김
        intent.putExtra("type", alarm.type)  // 코딩 질문
        startActivity(intent)
    }

    private fun getAlarm(jwt: String, userIdx: Long){
        val alarmService = AlarmService()
        alarmService.setAlarmService(this)

        alarmService.getAlarms(jwt, userIdx)
    }

    override fun onGetAlarmLoading() {
        binding.alarmLoadingPb.visibility = View.VISIBLE
        Log.d("Alarm/API","로딩중...")
    }

    override fun onGetAlarmSuccess(result: ArrayList<Alarm>?) {
        binding.alarmLoadingPb.visibility = View.GONE
        Log.d("Alarm/API","성공")
        if (result != null) {   // 알림이 있을 때
            groupDiffTime(result)   // diffTime으로 그룹핑하기
            alarmAdapter.addAlarm(result) // 어댑터에 추가
        }
    }

    override fun onGetAlarmFailure(code: Int, message: String) {
        binding.alarmLoadingPb.visibility = View.GONE
        when(code){
            400-> Log.d("Alarm/API", message)
        }
    }

    private fun groupDiffTime(alarm: ArrayList<Alarm>?) {
        // diffTime으로 그룹핑 -> key값 = diffTime
        val groupDiffTime: Map<String, ArrayList<Alarm>> = alarm?.groupBy { it.diffTime } as Map<String, ArrayList<Alarm>>

        for (diffTime:String in groupDiffTime.keys){
            consolidatedList.add(AlarmDateItem(diffTime))

            // diffTime이 같은 alarm들끼리 묶어서 넣기
            val groupItems: ArrayList<Alarm>? = groupDiffTime[diffTime]
            groupItems?.forEach {
                consolidatedList.add(AlarmGeneralItem(it.questionIdx, it.noticeContent, it.title, it.bigCategoryName, it.type, it.noticeCategoryIdx))
            }
        }
    }
}