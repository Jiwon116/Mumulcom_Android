package com.example.mumulcom

import androidx.recyclerview.widget.DiffUtil

class UserDiffItemCallback: DiffUtil.ItemCallback<Reply>() {
    override fun areItemsTheSame(oldItem: Reply, newItem: Reply): Boolean {
        return oldItem.replyIdx == newItem.replyIdx
    }

    override fun areContentsTheSame(oldItem: Reply, newItem: Reply): Boolean {
       return oldItem == newItem
    }

}