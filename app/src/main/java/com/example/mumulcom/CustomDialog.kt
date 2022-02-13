package com.example.mumulcom

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.mumulcom.databinding.DialogSignupBinding

class CustomDialog(val finishApp: () -> Unit): DialogFragment() {
    private var _binding: DialogSignupBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogSignupBinding.inflate(inflater, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)

        binding.dialogCancelBtn.setOnClickListener {
            finishApp()
        }
        binding.dialogApproveBtn.setOnClickListener {
            startSplashActivity()
        }
        return binding.root
    }

    private fun startSplashActivity() {
        val intent = Intent(requireContext(), SplashActivity::class.java)
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        finishApp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}