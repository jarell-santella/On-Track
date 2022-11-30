package com.android.on_track.data.geofenceDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [GeofenceEntry::class], version = 1)
@TypeConverters(Converters::class)
abstract class GeofenceDatabase : RoomDatabase() {
    abstract val geofenceDatabaseDao: GeofenceDatabaseDao

    companion object{
        @Volatile
        private var INSTANCE: GeofenceDatabase? = null

        fun getInstance(context: Context) : GeofenceDatabase {
            synchronized(this){
                var instance = INSTANCE
                if(instance == null){
                    instance = Room.databaseBuilder(context.applicationContext,
                        GeofenceDatabase::class.java, "geofence_table").build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}