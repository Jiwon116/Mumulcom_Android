package com.example.mumulcom

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileService {
    private lateinit var profileView: ProfileView

    fun setProfileService(profileView: ProfileFragment) {
        this.profileView = profileView
    }

    // 서버에서 alarm 정보 가져오기
    fun getProfiles(jwt: String, userIdx: Long){
        val profileService = getRetrofit().create(ProfileRetrofitInterface::class.java)

        profileView.onGetProfileLoading()

        profileService.getProfiles(jwt, userIdx)
            .enqueue(object : Callback<ProfileResponse> {
                override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {

                    if(response.isSuccessful) {
                        val resp = response.body()!!

                        when(resp.code){
                            1000-> resp.result?.let { profileView.onGetProfileSuccess(it) }
                            else-> profileView.onGetProfileFailure(resp.code,resp.message)
                        }
                    }
                }

                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                    profileView.onGetProfileFailure(400,"네트워크 오류가 발생했습니다.")
                }

            })
    }
}