package com.example.mumulcom

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
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

        return binding.root
    }

    private fun initView() {
        // 유저 닉네임 & 이메일
        binding.profileNicknameTv.text = nickname
        binding.profileEmailTv.text = email

        // 유저 프로필 이미지
        when {
            profileImgUrl.contains("1.png") -> {
                binding.profileBackgroundLy.setBackgroundResource(R.drawable.ic_profile_bg_1)
            }
            profileImgUrl.contains("2.png") -> {
                binding.profileBackgroundLy.setBackgroundResource(R.drawable.ic_profile_bg_2)
            }
            profileImgUrl.contains("3.png") -> {
                binding.profileBackgroundLy.setBackgroundResource(R.drawable.ic_profile_bg_3)
            }
            profileImgUrl.contains("4.png") -> {
                binding.profileBackgroundLy.setBackgroundResource(R.drawable.ic_profile_bg_4)
            }
            profileImgUrl.contains("5.png") -> {
                binding.profileBackgroundLy.setBackgroundResource(R.drawable.ic_profile_bg_5)
            }
            profileImgUrl.contains("6.png") -> {
                binding.profileBackgroundLy.setBackgroundResource(R.drawable.ic_profile_bg_6)
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
        if (profile != null) {   // 회원 정보가 있으면
            email = context?.let { getEmail(it) }.toString()
            nickname = context?.let { getNickname(it) }.toString()
            profileImgUrl = context?.let { getProfileImgUrl(it) }.toString()

            initView()
            Log.d("Profile/API","성공")
        }
    }

    override fun onGetProfileFailure(code: Int, message: String) {
        binding.profileRv.visibility = View.VISIBLE
        binding.profileLoadingPb.visibility = View.GONE
        when (code) {
            400 -> Log.d("Profile/API", message)
        }
    }
}
