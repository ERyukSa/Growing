package com.eryuksa.growing.todo.data

import androidx.lifecycle.MutableLiveData
import androidx.room.TypeConverter
import org.joda.time.DateTime

class TodoConverters {
    @TypeConverter
    fun dateTimeToMillis(dateTime: DateTime): Long {
        return dateTime.millis
    }

    @TypeConverter
    fun millisToDateTime(millis: Long): DateTime {
        return DateTime(millis)
    }

    @TypeConverter
    fun liveDataToBoolean(liveData: MutableLiveData<Boolean>): Boolean {
        return liveData.value!!
    }

    @TypeConverter
    fun booleanToLiveData(done: Boolean): MutableLiveData<Boolean> {
        return MutableLiveData(done)
    }
}