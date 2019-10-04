package com.example.epicchild.dataBase.dao

import androidx.room.*
import com.example.epicchild.dataBase.Task
import java.util.*

@Dao
interface TaskDao {

    @Query("""SELECT * FROM task WHERE epicId = :epicId""")
    abstract suspend fun getTaskByEpicId(epicId: UUID): List<Task>

    @Query("""SELECT * FROM task WHERE id = :taskId""")
    abstract suspend fun getTaskById(taskId: UUID): Task

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun insertTask(task: Task)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun updateTask(task: Task)

    @Delete
    abstract suspend fun deleteTask(task: Task)

    @Delete
    abstract suspend fun deleteTasks(taskList: List<Task>)
}