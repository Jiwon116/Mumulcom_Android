package com.example.mumulcom

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mumulcom.databinding.FragmentBottomReportSheetBinding
import com.example.mumulcom.databinding.FragmentBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ReportDialogFragment(var profileId:Long) : BottomSheetDialogFragment() {
    lateinit var binding: FragmentBottomReportSheetBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomReportSheetBinding.inflate(inflater, container, false)

        Log.d("abctest2:",profileId.toString())

        binding.goToReportPage.setOnClickListener {
            val intent= Intent(requireContext(),ReportActivity::class.java)
            intent.putExtra("profileId",profileId)
            startActivity(intent)
        }

        return binding.root
    }

    companion object {
        const val TAG = "BottomReportSheetFragment"
    }



}