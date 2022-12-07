package com.android.on_track.ui.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.on_track.R
import com.android.on_track.data.tasks.Task
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(private val taskList: List<Task>) : RecyclerView.Adapter<TaskViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val name = taskList[position].name
        val appName = taskList[position].appName
        val increment = taskList[position].increment
        val progress = taskList[position].progress
        val goalUnits = taskList[position].goalUnits
        val goal = taskList[position].goal
        val startDate = taskList[position].startDate
        val durationUnits = taskList[position].durationUnits
        val duration = taskList[position].duration

        val timeDiff = Calendar.getInstance().time.time - startDate.time
        val seconds = timeDiff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        val weeks = days / 7

        val calendar = Calendar.getInstance()
        calendar.time = startDate

        when (durationUnits) {
            "Hours" -> calendar.add(Calendar.HOUR_OF_DAY, duration)
            "Days" -> calendar.add(Calendar.DAY_OF_MONTH, duration)
            "Weeks" -> calendar.add(Calendar.WEEK_OF_YEAR, duration)
        }

        val endDate = calendar.time

        val timeRemaining = when (durationUnits) {
            "Hours" -> duration - hours
            "Days" -> duration - days
            else -> duration - weeks // if durationUnits == "Weeks"
        }
        holder.name.text = name
        holder.description.text = if (increment == "Positive") {
            String.format("Use %s for %d %s for %d %s (ends %s)", appName, goal, goalUnits.lowercase(), duration, durationUnits.lowercase(), SimpleDateFormat("dd/MM/yyyy HH:mm").format(endDate))
        } else { // if increment == "Negative"
            String.format("Limit usage of %s for %d %s for %d %s (ends %s)", appName, goal, goalUnits.lowercase(), duration, durationUnits.lowercase(), SimpleDateFormat("dd/MM/yyyy HH:mm").format(endDate))
        }
        holder.progress.text = String.format("%d/%d %s", progress, goal, goalUnits.lowercase())
        holder.startDate.text = String.format("Started %s", SimpleDateFormat("dd/MM/yyyy HH:mm").format(startDate))

        if (progress >= goal) {
            if (increment == "Positive") {
                holder.timeRemaining.text = String.format("Success")
                holder.card.setCardBackgroundColor(ContextCompat.getColor(holder.card.context, R.color.pastel_green))
            } else if (increment == "Negative") {
                holder.timeRemaining.text = String.format("Failure")
                holder.card.setCardBackgroundColor(ContextCompat.getColor(holder.card.context, R.color.pastel_red))
            }
        } else if (timeRemaining > 0) { // if progress < goal
            holder.timeRemaining.text = String.format("%d %s remaining", timeRemaining, durationUnits.lowercase())
            holder.card.setCardBackgroundColor(ContextCompat.getColor(holder.card.context, R.color.pastel_yellow))
        } else { // if progress < goal and timeRemaining =< 0
            if (increment == "Positive") {
                holder.timeRemaining.text = String.format("Failure")
                holder.card.setCardBackgroundColor(ContextCompat.getColor(holder.card.context, R.color.pastel_red))
            } else if (increment == "Negative") {
                holder.timeRemaining.text = String.format("Success")
                holder.card.setCardBackgroundColor(ContextCompat.getColor(holder.card.context, R.color.pastel_green))
            }
        }
    }

    override fun getItemCount(): Int {
        return taskList.size
    }
}