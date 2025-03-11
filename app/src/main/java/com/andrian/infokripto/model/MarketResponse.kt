package com.andrian.infokripto.model

import com.google.gson.annotations.SerializedName

data class MarketResponse(
    @SerializedName("ticker") val ticker: MarketTicker
)
