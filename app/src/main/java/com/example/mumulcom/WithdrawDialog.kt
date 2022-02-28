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
import androidx.fragment.app.DialogFragment
import com.example.mumulcom.databinding.DialogBinding
import com.kakao.sdk.user.UserApiClient

class WithdrawDialog: DialogFragment(), WithdrawView {
    private var _binding: DialogBinding? = null
    private val binding get() = _binding!!

    private var jwt: String = ""
    private var userIdx: Long = 0

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogBinding.inflate(inflater, container, false)

        jwt = getJwt(requireContext())
        userIdx = getUserIdx(requireContext())

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)

        binding.dialogTitleTv.text = "정말 탈퇴 하시겠습니까?"
        binding.dialogContentTv.text = "탈퇴 시 정보가 모두 삭제되며,\n삭제된 데이터는 복구되지 않습니다."

        // 취소 버튼 클릭
        binding.dialogCancelBtn.setOnClickListener {
            onDestroyView() // 다이얼로그 창 삭제
        }

        // 확인 버튼 클릭 -> 회원탈퇴 실행
        binding.dialogApproveBtn.setOnClickListener {
            UserApiClient.instance.unlink { error ->
                if (error != null) {
                    Log.d(ContentValues.TAG, "회원탈퇴 실패")
                }else {
                    withdraw(jwt, userIdx)  // 회원 탈퇴 API
                }
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun withdraw(jwt: String, userIdx: Long) {
        val withdrawService = ProfileService()
        withdrawService.setWithdrawService(this)

        withdrawService.withdraw(jwt, userIdx)
    }

    override fun onWithdrawLoading() {
        Log.d("Withdraw/API","회원탈퇴 로딩중...")
    }

    override fun onWithdrawSuccess(profile: Profile) {
        Log.d("Withdraw/API","성공")

        // jwt값은 null로
        // 닉네임과 프로필 변경
        saveJwt(requireContext(), "")
        saveNickname(requireContext(), profile.nickname)
        saveProfileImgUrl(requireContext(), profile.profileImgUrl)

        Toast.makeText(requireContext(), "회원탈퇴 되었습니다.", Toast.LENGTH_SHORT).show()
        val intent = Intent(requireContext(), SplashActivity::class.java)
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
    }

    override fun onWithdrawFailure(code: Int, message: String) {
        when (code) {
            400 -> Log.d("Profile/API", message)
        }
    }
}