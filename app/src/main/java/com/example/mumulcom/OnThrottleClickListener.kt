package com.example.mumulcom

import android.content.ContentValues.TAG
import android.util.Log
import android.view.View

//중복클릭 방지+좀더 만져야됌
class OnThrottleClickListener(
    private val clickListener: View.OnClickListener,
    private val interval: Long = 1000//1분
) :
    View.OnClickListener {

    private var clickable = true
    // clickable 플래그를 이 클래스가 아니라 더 상위 클래스에 두면
    // 여러 뷰에 대한 중복 클릭 방지할 수 있다.

    override fun onClick(v: View?) {
        if (clickable) {
            clickable = false
            v?.run {
                postDelayed({
                    clickable = true
                }, interval)
                clickListener.onClick(v)
            }
        } else {
            Log.d(TAG, "waiting for a while")
        }
    }
}