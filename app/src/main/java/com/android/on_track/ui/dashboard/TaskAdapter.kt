package com.android.on_track.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.on_track.R
import com.android.on_track.data.Task

class TaskAdapter(private val taskList: ArrayList<Task>) : RecyclerView.Adapter<TaskViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.name.text = taskList[position].name.toString()
        holder.description.text = taskList[position].description.toString()
        holder.progress.text = taskList[position].progress.toString()
    }

    override fun getItemCount(): Int {
        return taskList.size
    }
}