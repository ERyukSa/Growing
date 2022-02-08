package com.eryuksa.growing.fragment.miracle_morning.calendar.database

import androidx.room.*
import com.eryuksa.growing.fragment.miracle_morning.calendar.model.MiracleStamp

@Dao
interface CalendarDao {
    @Query("SELECT * FROM MiracleStamp WHERE monthMillis=(:monthMillis)")
    fun getMonthStamps(monthMillis: Long): List<MiracleStamp>

    @Query("SELECT * FROM MiracleStamp WHERE monthMillis=(:monthMillis)" +
            " AND dayOfMonth BETWEEN :startDay AND :endDay")
    fun getStamps(monthMillis: Long, startDay: Int, endDay: Int): List<MiracleStamp>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertStamp(stamp: MiracleStamp): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertStamp(stamps: List<MiracleStamp>): List<Long>

    @Update
    fun updateStamp(stamp: MiracleStamp)

    @Update
    fun updateStamp(stamps: List<MiracleStamp>)

    @Transaction
    fun insertOrUpdateStamps(stampList: List<MiracleStamp>) {
        val addResult = insertStamp(stampList)
        val updateList = mutableListOf<MiracleStamp>()

        for (i in addResult.indices) {
            if (addResult[i] == -1L) updateList.add(stampList[i])
        }

        if (updateList.isNotEmpty()) updateStamp(updateList)
    }

    /*
    @Insert
    fun addStamp(monthMillis: Long, dayOfMonth: Int, wakeUpMinute: Int)

    @Update
    fun updateStamp(monthMillis: Long, dayOfMonth: Int, wakeUpMinute: Int)
    */
}