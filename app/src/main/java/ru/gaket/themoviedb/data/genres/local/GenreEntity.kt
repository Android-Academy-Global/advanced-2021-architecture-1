package ru.gaket.themoviedb.data.genres.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * todo: handle column names as in [ru.gaket.themoviedb.data.review.local.MyReviewEntity]
 */
//todo: maybe use Long for id?
@Entity(tableName = "genres")
data class GenreEntity(

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "name")
    val name: String,
)