package com.example.bai4.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bai4.R
import com.example.bai4.data.model.Job
import com.example.bai4.databinding.ItemListJobsBinding
import java.util.Calendar

class JobAdapter : ListAdapter<Job, JobAdapter.JobViewHolder>(DiffCallback) {
    class JobViewHolder(private val binding: ItemListJobsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(job: Job) {
            // Todo bind data for item list jobs
            binding.tvName.text = job.name
            val date = Calendar.getInstance()
            date.timeInMillis = job.date
            binding.tvDatetime.text =
                String.format(
                    "%02d/%02d/%04d",
                    date.get(Calendar.DAY_OF_MONTH),
                    date.get(Calendar.MONTH),
                    date.get(Calendar.YEAR)
                )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        return JobViewHolder(
            ItemListJobsBinding.bind(
                LayoutInflater.from(parent.context).inflate(R.layout.item_list_jobs, parent, false)
            )
        )
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Job>() {
            override fun areItemsTheSame(oldItem: Job, newItem: Job): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Job, newItem: Job): Boolean {
                return oldItem.name == newItem.name && oldItem.date == newItem.date
            }
        }
    }
}
