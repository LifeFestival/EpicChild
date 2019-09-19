package com.example.epicchild.dataBase.dao

import androidx.room.*
import com.example.epicchild.dataBase.Epic
import java.util.*

@Dao
interface EpicDao {

    @Query("""SELECT * FROM epic""")
    abstract suspend fun getAllEpics(): List<Epic>

    @Query("""SELECT * FROM epic WHERE id = :id""")
    abstract suspend fun getEpicById(id: UUID): Epic

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun insertEpic(epic: Epic)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun updateEpic(epic: Epic)

    @Delete
    abstract suspend fun deleteEpic(epic: Epic)

    @Delete
    abstract suspend fun deleteEpics(epics: List<Epic>)

    @Query("""DELETE FROM epic""")
    abstract suspend fun deleteAllEpics()
}