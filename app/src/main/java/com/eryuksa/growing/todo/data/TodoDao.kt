package com.eryuksa.growing.todo.data

import androidx.room.*

@Dao
interface TodoDao {
    @Query("SELECT * from todo where dateTime = :milliSeconds ORDER BY position ASC")
    suspend fun getTodoByMillis(milliSeconds: Long): List<TodoItem.Todo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo: TodoItem.Todo)

    @Delete
    suspend fun delete(todo: TodoItem.Todo)
}