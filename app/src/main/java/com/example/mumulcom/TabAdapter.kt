package com.example.mumulcom

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class TabAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> TabQuestionFragment()  // 내가 한 질문
            else -> TabAnswerFragment() // 내가 단 답변
        }
    }

}