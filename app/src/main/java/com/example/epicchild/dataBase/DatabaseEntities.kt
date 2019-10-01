package com.example.epicchild.dataBase

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime
import java.util.*

const val EPIC_TABLE_NAME = "epic"
const val TASK_TABLE_NAME = "task"

@Entity(tableName = EPIC_TABLE_NAME)
data class Epic(
    var name: String,
    var description: String?,
    var isFinished: Boolean = false,
    val dateOfCreating: OffsetDateTime = OffsetDateTime.now(),
    var dateOfChanging: OffsetDateTime = OffsetDateTime.now(),
    var numberOfTasks: Int = 0,
    var numberOfCompletedTasks: Int = 0,

    @PrimaryKey
    val id: UUID = UUID.randomUUID()
)

@Entity(tableName = TASK_TABLE_NAME)
data class Task(
    var name: String,
    val epicId: UUID,
    var isFinished: Boolean = false,
    val dateOfCreating: OffsetDateTime = OffsetDateTime.now(),

    @PrimaryKey
    val id: UUID = UUID.randomUUID()

)