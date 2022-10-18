package com.example.mumulcom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.mumulcom.databinding.ActivityReportContentBinding

class ReportContentActivity : AppCompatActivity(),ReportUserView {

    lateinit var binding:ActivityReportContentBinding
    private var reportTypeIdx:Long=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityReportContentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backIv.setOnClickListener {
            finish()
        }
        val token = getJwt(this)
        val reporterUserIdx= getUserIdx(this)
        val profileId = intent.getLongExtra("profileId",0)
        reportTypeIdx=intent.getLongExtra("reportTypeIdx",0)



        // 보내기
        binding.sendReportBtn.setOnClickListener {

            val content=binding.reportContentEt.text.toString()
            if(content==""){
                Toast.makeText(this,"신고할 내용을 적어주세요.",Toast.LENGTH_SHORT).show()
            }else{
//                Log.d("report: ",token)
//                Log.d("report: ",reporterUserIdx.toString())
//                Log.d("report: ",profileId.toString())
//                Log.d("report: ",reportTypeIdx.toString())
//                Log.d("report: ",content)

                val reportUserService=ReportUserService()
                reportUserService.setReportUserView(this)
                reportUserService.postReportUser(token,PostUserReq(reporterUserIdx,profileId,reportTypeIdx,content))
                finish()
            }



        }
    }

    override fun onReportUserLoading() {
        Log.d("사용자 신고/API","로딩중...")
    }

    override fun onReportUserSuccess(result: ReportUserId) {
        Log.d("사용자 신고/API","성공")
        Toast.makeText(this,"해당 사용자를 신고했습니다.",Toast.LENGTH_SHORT).show()
    }

    override fun onReportUserFailure(code: Int, message: String) {
        when(code){
            400-> Log.d("사용자 신고/API",message)
        }
    }
}