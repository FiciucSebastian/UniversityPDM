package com.example.exam.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat

@Parcelize
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "tag") var tag: String,
    @ColumnInfo(name = "text") var text: String,
    @ColumnInfo(name = "version") var version: Int,
    @ColumnInfo(name = "action") var action: String?,
    @ColumnInfo(name = "attemptUpdateAt") var attemptUpdateAt: Long
) : Parcelable {
    override fun toString(): String = "$tag $text $version"
}

data class TaskDTO(
    var tag: String,
    var text: String,
    var version: Int
)