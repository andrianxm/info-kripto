package com.andrian.infokripto.data.remote

import com.andrian.infokripto.model.Market
import com.andrian.infokripto.model.MarketItemData
import com.andrian.infokripto.model.MarketResponse
import com.andrian.infokripto.model.MarketSummaryResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("pairs")
    suspend fun getMarkets(): List<Market>

    @GET("ticker/{pair_id}")
    suspend fun getMarketById(@Path("pair_id") pairId: String): Response<MarketResponse>

    @GET("summaries")
    suspend fun getMarketSummaries(): MarketSummaryResponse
}

