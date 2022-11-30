package com.android.on_track.ui.dashboard

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.android.on_track.R
import com.google.android.material.textview.MaterialTextView

class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val name: MaterialTextView = itemView.findViewById(R.id.task_name)
    val description: MaterialTextView = itemView.findViewById(R.id.task_description)
    val progress: MaterialTextView = itemView.findViewById(R.id.task_progress)
}