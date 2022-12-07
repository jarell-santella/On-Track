package com.android.on_track.data.tasks

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.*

class TaskDateConverters {
    @TypeConverter
    fun stringToDate(string: String): Date? {
        return SimpleDateFormat("dd/MM/yyyy hh:mm").parse(string)
    }

    @TypeConverter
    fun dateToString(date: Date): String {
        return SimpleDateFormat("dd/MM/yyyy hh:mm").format(date)
    }
}