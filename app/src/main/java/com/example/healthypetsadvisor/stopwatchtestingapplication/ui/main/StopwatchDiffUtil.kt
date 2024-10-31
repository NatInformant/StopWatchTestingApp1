package com.example.healthypetsadvisor.stopwatchtestingapplication.ui.main

import androidx.recyclerview.widget.DiffUtil

class StopwatchDiffUtil : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return true
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}
