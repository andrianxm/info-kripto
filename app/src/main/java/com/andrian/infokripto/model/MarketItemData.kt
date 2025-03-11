package com.andrian.infokripto.model

data class MarketItemData(
    val id: String,
    val base_currency: String,
    val symbol: String,
    val last: String,
    val buy: String,
    val sell: String,
    val logoUrl: String,
    val high: String,
    val low: String,
    val priceChange: Int = 0,
)