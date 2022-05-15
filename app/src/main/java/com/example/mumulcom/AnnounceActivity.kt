package com.example.mumulcom

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mumulcom.databinding.ActivityAnnounceBinding

class AnnounceActivity : AppCompatActivity(), AnnounceView {
    lateinit var binding: ActivityAnnounceBinding

    private lateinit var announceAdapter: AnnounceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnnounceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerview()
    }

    private fun initRecyclerview() {
        getAnnounce()
        announceAdapter = AnnounceAdapter(this)
        announceAdapter.setAnnounceClickListener(object: AnnounceAdapter.AnnounceClickListener{
            override fun onItemClick(announce: Announce) {
                startAnnounceActivity(announce)
            }
        })
        binding.announceRv.adapter = announceAdapter
        binding.announceRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
    }

    private fun startAnnounceActivity(announce: Announce){
        val intent = Intent(this, AnnounceDetailActivity::class.java)
        saveAnnounceIdx(this, announce.id)
        startActivity(intent)
    }

    private fun getAnnounce(){
        val announceService = AnnounceService()
        announceService.setAnnounceService(this)

        announceService.getAnnounces()
    }

    override fun onGetAnnounceLoading() {
        binding.announcePb.visibility = View.VISIBLE
        Log.d("AnnounceActivity","공지사항 로딩 중...")
    }

    override fun onGetAnnounceSuccess(result: ArrayList<Announce>?) {
        binding.announcePb.visibility = View.GONE
        if (result != null) {
            announceAdapter.addAnnounce(result)
        }
    }

    override fun onGetAnnounceFailure(code: Int, message: String) {
        binding.announcePb.visibility = View.GONE
        when(code){
            400-> Log.d("AnnounceActivity", message)
        }
    }
}

