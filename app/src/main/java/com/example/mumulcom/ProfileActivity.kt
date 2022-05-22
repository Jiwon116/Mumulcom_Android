package com.example.mumulcom

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.mumulcom.databinding.ActivityProfileBinding

// todo 안쓰는 파일
class ProfileActivity : AppCompatActivity() {

    private lateinit var binding:ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // 뒤로가기
//        binding.backIv.setOnClickListener {
//            finish()
//        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false) // 기본 제목 삭제
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 뒤로가기 추가
        binding.toolbar.title="사용자 프로필"


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_option,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_report->{
                val intent = Intent(this,ReportActivity::class.java)
                intent.putExtra("reporterUserIdx",-1) // 보고있는 프로필 유저 id만 넘김
                startActivity(intent)
                return true
            }
            android.R.id.home ->{
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}