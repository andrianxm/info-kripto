package com.andrian.infokripto.model

import com.google.gson.annotations.SerializedName

data class Market(
    @SerializedName("id") val id: String,
    @SerializedName("symbol") val symbol: String,
    @SerializedName("description") val description: String,
    @SerializedName("url_logo") val logoUrl: String,
    @SerializedName("ticker_id") val tickerId: String,
    @SerializedName("last") val last: String,
    @SerializedName("buy") val buy: String,
    @SerializedName("sell") val sell: String,
)