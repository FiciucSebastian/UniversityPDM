package com.example.ficiapp.components.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat

@Parcelize
@Entity(tableName = "components")
data class Component(
    @PrimaryKey @ColumnInfo(name = "_id") val _id: String,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "quantity") var quantity: Int,
    @ColumnInfo(name = "releaseDate") var releaseDate: String,
    @ColumnInfo(name = "inStock") var inStock: Boolean,
    @ColumnInfo(name = "owner") var owner: String?,
    @ColumnInfo(name = "action") var action: String?,
    @ColumnInfo(name = "attemptUpdateAt") var attemptUpdateAt: Long,
    @ColumnInfo(name = "picturePath") var picturePath: String?,
    @ColumnInfo(name = "latitude") var latitude: Float?,
    @ColumnInfo(name = "longitude") var longitude: Float?
) : Parcelable {
    override fun toString(): String = "$name $quantity $releaseDate $inStock"

    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("dd.MM.yyyy")
        val netDate = kotlin.math.floor(releaseDate.toDouble())
        return sdf.format(netDate)
    }
}

data class ComponentDTO(
    var name: String,
    var quantity: Int,
    var releaseDate: String,
    var inStock: Boolean,
    var owner: String?
)