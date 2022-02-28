package com.example.mumulcom

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mumulcom.databinding.FragmentProfileBinding


class ProfileFragment : Fragment(), ProfileView {
    lateinit var binding: FragmentProfileBinding

    private var jwt: String = ""    // jwt
    private var userIdx: Long = 0   // 유저 인덱스
    private var email: String = ""  // 유저 이메일
    private var nickname: String = ""   // 유저 닉네임
    private var profileImgUrl: String = ""  // 유저 프로필 이미지


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        // SharedPreference에 저장한 정보 불러오기
        jwt = context?.let { getJwt(it) }.toString()
        userIdx = context?.let { getUserIdx(it) }!!

        getProfile()

        binding.profileSettingIv.setOnClickListener {
            startActivity(Intent(requireContext(), ProfileModifyActivity::class.java))
        }

        // 로그아웃
        binding.profileLogoutArrowIv.setOnClickListener {
            logoutDialogPopup() // 확인창 띄우기
        }

        // 회원탈퇴
        binding.profileWithdrawArrowIv.setOnClickListener {
            withdrawDialogPopup()   // 확인창 띄우기
        }

        return binding.root
    }

    @SuppressLint("ResourceAsColor")
    private fun initView() {
        // 유저 닉네임 & 이메일
        binding.profileNicknameTv.text = nickname
        binding.profileEmailTv.text = email

        // 유저 프로필 이미지
        when {
            profileImgUrl == "https://mumulcom-bucket.s3.ap-northeast-2.amazonaws.com/character/1.png" -> {
                binding.profileBackgroundLy.setBackgroundResource(R.drawable.ic_profile_bg_1)
            }
            profileImgUrl == "https://mumulcom-bucket.s3.ap-northeast-2.amazonaws.com/character/2.png" -> {
                binding.profileBackgroundLy.setBackgroundResource(R.drawable.ic_profile_bg_2)
            }
            profileImgUrl == "https://mumulcom-bucket.s3.ap-northeast-2.amazonaws.com/character/3.png" -> {
                binding.profileBackgroundLy.setBackgroundResource(R.drawable.ic_profile_bg_3)
            }
            profileImgUrl == "https://mumulcom-bucket.s3.ap-northeast-2.amazonaws.com/character/4.png" -> {
                binding.profileBackgroundLy.setBackgroundResource(R.drawable.ic_profile_bg_4)
            }
            profileImgUrl == "https://mumulcom-bucket.s3.ap-northeast-2.amazonaws.com/character/5.png" -> {
                binding.profileBackgroundLy.setBackgroundResource(R.drawable.ic_profile_bg_5)
            }
            profileImgUrl == "https://mumulcom-bucket.s3.ap-northeast-2.amazonaws.com/character/6.png" -> {
                binding.profileBackgroundLy.setBackgroundResource(R.drawable.ic_profile_bg_6)
            }
            else -> {
                binding.profileBackgroundLy.setBackgroundResource(R.drawable.bg_dialog_outline)
                binding.profileEmailTv.setTextColor(R.color.black)
                binding.profileEmailTv.text = "사용할 수 없는 회원정보입니다."
                binding.profileSettingIv.visibility = View.GONE
            }
        }
    }

    private fun getProfile(){
        val profileService = ProfileService()
        profileService.setProfileService(this)

        profileService.getProfiles(jwt, userIdx)
    }

    override fun onGetProfileLoading() {
        binding.profileRv.visibility = View.GONE
        binding.profileLoadingPb.visibility = View.VISIBLE
        Log.d("Profile/API","로딩중...")
    }

    override fun onGetProfileSuccess(profile: Profile) {
        binding.profileRv.visibility = View.VISIBLE
        binding.profileLoadingPb.visibility = View.GONE

        email = profile.email
        nickname = profile.nickname
        profileImgUrl = profile.profileImgUrl

        Log.d("nickname", nickname)
        Log.d("profileImgUrl", profileImgUrl)

        initView()
        Log.d("Profile/API","성공")
    }

    override fun onGetProfileFailure(code: Int, message: String) {
        binding.profileRv.visibility = View.VISIBLE
        binding.profileLoadingPb.visibility = View.GONE
        when (code) {
            400 -> Log.d("Profile/API", message)
        }
    }

    private fun logoutDialogPopup() {
        val customDialog = LogoutDialog()
        customDialog.show(childFragmentManager, "Logout Dialog")
    }

    private fun withdrawDialogPopup() {
        val customDialog = WithdrawDialog()
        customDialog.show(childFragmentManager, "Withdraw Dialog")
    }
}
