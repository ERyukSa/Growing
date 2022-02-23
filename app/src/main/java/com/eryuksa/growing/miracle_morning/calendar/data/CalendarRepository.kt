package com.eryuksa.growing.miracle_morning.calendar.data

import android.content.Context
import androidx.room.Room
import com.eryuksa.growing.miracle_morning.model.MiracleStamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val CALENDAR_DB_NAME = "MiracleCalendarDB"

class CalendarRepository private constructor(context: Context) {

    private val database: CalendarDatabase = Room.databaseBuilder(
        context.applicationContext,
        CalendarDatabase::class.java,
        CALENDAR_DB_NAME
    ).build()

    private val calendarDao = database.calendarDao()

    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    suspend fun getMonthStamps(monthMillis: Long): List<MiracleStamp> {
        return  withContext(coroutineScope.coroutineContext) {
            return@withContext calendarDao.getMonthStamps(monthMillis)
        }
    }

    suspend fun getStamps(monthMillis: Long, startDay: Int, endDay: Int): List<MiracleStamp> {
        return withContext(coroutineScope.coroutineContext){
            return@withContext calendarDao.getStamps(monthMillis, startDay, endDay)
        }
    }

    fun insertStamp(stamp: MiracleStamp) {
        coroutineScope.launch {
            calendarDao.insertStamp(stamp)
        }
    }

    fun updateStamp(stampList: List<MiracleStamp>) {
        coroutineScope.launch {
            calendarDao.insertOrUpdateStamps(stampList)
        }
    }

    companion object {
        private var INSTANCE: CalendarRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = CalendarRepository(context)
            }
        }

        fun get(): CalendarRepository {
            return INSTANCE ?: throw IllegalStateException("CalendarRepository must be initialized")
        }
    }
}