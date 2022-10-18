package com.example.mumulcom

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.mumulcom.databinding.ActivityAnnounceDetailBinding

class AnnounceDetailActivity : AppCompatActivity(), AnnounceDetailView {
    lateinit var binding: ActivityAnnounceDetailBinding
    private var announceIdx: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnnounceDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        announceIdx = getAnnounceIdx(this)

        getAnnouncesDetail(announceIdx)
    }

    private fun getAnnouncesDetail(announceIdx: Long){
        val announceDetailService = AnnounceDetailService()
        announceDetailService.setAnnounceDetailService(this)

        announceDetailService.getAnnouncesDetail(announceIdx)
    }

    override fun onGetAnnounceDetailLoading() {
        binding.announceDetailPb.visibility = View.VISIBLE
        Log.d("AnnounceDetailActivity","공지사항 로딩 중...")
    }

    override fun onGetAnnounceDetailSuccess(result: AnnounceDetail?) {
        binding.announceDetailPb.visibility = View.GONE
        if (result != null) {
            binding.notificationDetailTitleTv.text = result.title
            binding.notificationDetailDateTv.text = result.createdAt
            binding.notificationDetailContentTv.text = result.content
        }
    }

    override fun onGetAnnounceDetailFailure(code: Int, message: String) {
        binding.announceDetailPb.visibility = View.GONE
        when(code){
            400-> Log.d("AnnounceDetailActivity", message)
        }
    }
}

