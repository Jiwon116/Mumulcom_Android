package com.example.mumulcom

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
import com.example.mumulcom.databinding.DialogQuestionBinding

class CustomDialogQuestion(val finishApp: () -> Unit): DialogFragment() {
    private var _binding: DialogQuestionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogQuestionBinding.inflate(inflater, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)

        binding.dialogCancelBtn.setOnClickListener {
            dismiss()
        }
        binding.dialogApproveBtn.setOnClickListener {
            startQuestionBoardActivity()
        }
        return binding.root
    }

    private fun startQuestionBoardActivity() {
        val intent = Intent(requireContext(), QuestionBoardActivity::class.java)
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        finishApp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}