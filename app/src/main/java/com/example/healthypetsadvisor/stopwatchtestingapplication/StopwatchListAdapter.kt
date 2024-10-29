package com.example.healthypetsadvisor.stopwatchtestingapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.healthypetsadvisor.stopwatchtestingapplication.databinding.StopwatchListElementBinding

class StopwatchListAdapter :
    ListAdapter<String, StopwatchListAdapter.StopwatchListViewHolder>(StopwatchDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopwatchListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return StopwatchListViewHolder(
            StopwatchListElementBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: StopwatchListViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    class StopwatchListViewHolder(private val binding: StopwatchListElementBinding) :
        ViewHolder(binding.root) {
        fun onBind(stopWatchText: String) {
            binding.textViewStopWatch.text = stopWatchText
        }
    }

    class StopwatchDiffUtil() : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

    }
}