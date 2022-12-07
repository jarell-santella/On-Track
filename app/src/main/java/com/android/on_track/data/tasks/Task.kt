package com.android.on_track.data.tasks

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "task_table")
data class Task(
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "appName") val appName: String,
    @ColumnInfo(name = "increment") val increment: String, // "Positive" or "Negative"
    @ColumnInfo(name = "progress") val progress: Int,
    @ColumnInfo(name = "goal_units") val goalUnits: String, // "Minutes" or "Hours"
    @ColumnInfo(name = "goal") val goal: Int,
    @ColumnInfo(name = "start_date") val startDate: Date,
    @ColumnInfo(name = "duration_units") val durationUnits: String, // "Hours", "Days", or "Weeks"
    @ColumnInfo(name = "duration") val duration: Int, // Quantity of units specified by duration_units
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)