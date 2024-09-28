package com.example.assignment2.Data

import androidx.room.TypeConverter
import com.google.firebase.Timestamp


class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Timestamp? {
        return value?.let { Timestamp(it, 0) }
    }


    @TypeConverter
    fun dateToTimestamp(timestamp: Timestamp?): Long? {
        return timestamp?.seconds
    }
}
