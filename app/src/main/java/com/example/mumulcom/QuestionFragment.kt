package com.example.mumulcom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mumulcom.databinding.FragmentQuestionBinding


class QuestionFragment : Fragment() {

    lateinit var binding: FragmentQuestionBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuestionBinding.inflate(inflater, container, false)
        return binding.root
    }


}