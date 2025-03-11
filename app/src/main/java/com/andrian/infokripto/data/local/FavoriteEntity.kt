package com.andrian.infokripto.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_markets")
data class FavoriteEntity(
    @PrimaryKey val id: String,
    val logoUrl: String,
    val base_currency: String,
    val symbol: String,
    val last: String,
    val buy: String,
    val sell: String,
    val high: String,
    val low: String,
)
