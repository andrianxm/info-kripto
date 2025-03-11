package com.andrian.infokripto.model

import com.google.gson.annotations.SerializedName

data class MarketTicker(
    @SerializedName("description") val description: String,
    @SerializedName("high") val high: String,
    @SerializedName("low") val low: String,
    @SerializedName("last") val last: String,
    @SerializedName("buy") val buy: String,
    @SerializedName("sell") val sell: String,
    @SerializedName("server_time") val serverTime: Long
)