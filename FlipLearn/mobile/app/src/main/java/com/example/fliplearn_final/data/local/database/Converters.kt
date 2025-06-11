package com.example.fliplearn_final.data.local.database

import androidx.room.TypeConverter
import java.time.Instant
import java.util.UUID

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Instant? =
        value?.let { Instant.ofEpochMilli(it) }

    @TypeConverter
    fun dateToTimestamp(instant: Instant?): Long? =
        instant?.toEpochMilli()

    @TypeConverter
    fun fromUUID(uuid: String?): UUID? =
        uuid?.let { UUID.fromString(it) }

    @TypeConverter
    fun uuidToString(uuid: UUID?): String? =
        uuid?.toString()

    @TypeConverter
    fun fromInstant(instant: Instant): Long = instant.toEpochMilli()

    @TypeConverter
    fun toInstant(millis: Long): Instant = Instant.ofEpochMilli(millis)
}