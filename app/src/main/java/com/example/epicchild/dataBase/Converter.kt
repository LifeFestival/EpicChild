package com.example.epicchild.dataBase

import androidx.room.TypeConverter
import org.threeten.bp.OffsetDateTime
import java.util.*

class Converter {

    @TypeConverter
    fun stringToUUID(value: String?): UUID? = if (value == null) null else UUID.fromString(value)


    @TypeConverter
    fun uuidToString(value: UUID?): String? = value?.toString()

    @TypeConverter
    fun stringToOffsetDateTime(value: String?): OffsetDateTime? {
        return if (value == null) null else OffsetDateTime.parse(value)
    }

    @TypeConverter
    fun offsetDateTimeToString(value: OffsetDateTime?): String? = value?.toString()

}