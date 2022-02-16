package com.sergio.tallerandroid.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
@Entity(tableName = "animal_table")
data class AnimalData (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "species")
    var species: String,

    @ColumnInfo(name = "specimens")
    var specimens: Int,

    @ColumnInfo(name = "family")
    var family: FamilyData,
): Parcelable {
}