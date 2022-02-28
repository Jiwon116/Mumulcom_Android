package com.example.mumulcom

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.pm.PermissionInfoCompat
import androidx.fragment.app.DialogFragment
import com.example.mumulcom.databinding.DialogBinding
import com.kakao.sdk.user.UserApiClient
import io.reactivex.internal.util.BackpressureHelper
import kotlin.concurrent.fixedRateTimer

class LogoutDialog: DialogFragment() {
    private var _binding: DialogBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogBinding.inflate(inflater, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)

        binding.dialogTitleTv.text = "정말 로그아웃 하시겠습니까?"
        binding.dialogContentTv.text = "확인을 누르면 로그아웃 됩니다."

        // 취소 버튼 클릭
        binding.dialogCancelBtn.setOnClickListener {
            onDestroyView() // 다이얼로그 창 삭제
        }

        // 확인 버튼 클릭 -> 로그아웃 실행
        binding.dialogApproveBtn.setOnClickListener {
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Log.d(ContentValues.TAG, "로그아웃 실패")
                }else {
                    Log.d(ContentValues.TAG, "로그아웃 성공")
                    Toast.makeText(requireContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                }
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()  // 새로 생긴 activity finish 함수
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}