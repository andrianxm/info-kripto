package com.andrian.infokripto.model

import com.google.gson.annotations.SerializedName

data class MarketSummaryResponse(
    @SerializedName("tickers") val tickers: Map<String, MarketTicker>,
    @SerializedName("prices_24h") val prices_24h: Map<String, String>
)