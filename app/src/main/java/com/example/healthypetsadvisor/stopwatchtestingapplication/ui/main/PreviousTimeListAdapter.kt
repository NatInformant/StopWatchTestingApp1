package com.example.healthypetsadvisor.stopwatchtestingapplication.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.healthypetsadvisor.stopwatchtestingapplication.databinding.PreviousTimeListElementBinding
import com.example.healthypetsadvisor.stopwatchtestingapplication.ui.data.PreviousTimeElement

class PreviousTimeListAdapter :
    ListAdapter<PreviousTimeElement, PreviousTimeListAdapter.PreviousTimeElementListViewHolder>(
        PreviousTimeDiffUtil()
    ) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PreviousTimeElementListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PreviousTimeElementListViewHolder(
            parent.context,
            PreviousTimeListElementBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PreviousTimeElementListViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    class PreviousTimeElementListViewHolder(
        private val context: Context,
        private val binding: PreviousTimeListElementBinding
    ) :
        ViewHolder(binding.root) {
        fun onBind(previousTimeElement: PreviousTimeElement) {
            binding.textViewStopWatch.text = previousTimeElement.timeValue
            binding.textViewStopWatch.setTextColor(context.getColor(previousTimeElement.textViewColorSource))
        }
    }

    class PreviousTimeDiffUtil : DiffUtil.ItemCallback<PreviousTimeElement>() {
        override fun areItemsTheSame(
            oldItem: PreviousTimeElement,
            newItem: PreviousTimeElement
        ): Boolean {
            return oldItem.timeValue == newItem.timeValue
        }

        override fun areContentsTheSame(
            oldItem: PreviousTimeElement,
            newItem: PreviousTimeElement
        ): Boolean {
            return oldItem == newItem
        }
    }
}