package com.example.mumulcom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import com.example.mumulcom.databinding.FragmentQuestionBinding
import com.google.android.material.tabs.TabLayoutMediator

class QuestionFragment : Fragment() {

    lateinit var binding: FragmentQuestionBinding
    private var tabTitle = arrayOf("내가 한 질문", "내가 단 답변")
    private var tabIcon = arrayOf(R.drawable.tab_question_icon_selector, R.drawable.tab_answer_icon_selector)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQuestionBinding.inflate(inflater, container, false)

        val tabAdapter = TabAdapter(this)
        binding.questionTabVp.adapter = tabAdapter
        TabLayoutMediator(binding.questionTabTl, binding.questionTabVp) {
                tab, position ->
            tab.text = tabTitle[position]
            tab.setIcon(tabIcon[position])
        }.attach()

        return binding.root
    }
}
