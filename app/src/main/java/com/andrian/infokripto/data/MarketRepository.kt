package com.andrian.infokripto.data

import android.util.Log
import com.andrian.infokripto.data.local.FavoriteDao
import com.andrian.infokripto.data.local.FavoriteEntity
import com.andrian.infokripto.data.remote.ApiService
import com.andrian.infokripto.model.MarketItemData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class MarketRepository(
    private val apiService: ApiService, private val favoriteDao: FavoriteDao
) {

    fun getAllFavorites(): Flow<List<FavoriteEntity>> = favoriteDao.getAllFavorites()

    fun isFavorite(id: String): Flow<Boolean> = favoriteDao.isFavorite(id)

    suspend fun addToFavorites(market: MarketItemData) {
        favoriteDao.addToFavorites(
            FavoriteEntity(
                id = market.id,
                symbol = market.symbol,
                logoUrl = market.logoUrl,
                base_currency = market.base_currency,
                last = market.last,
                buy = market.buy,
                sell = market.sell,
                high = market.high,
                low = market.low,
            )
        )
    }

    suspend fun removeFromFavorites(market: MarketItemData) {
        favoriteDao.removeFromFavorites(
            FavoriteEntity(
                id = market.id,
                symbol = market.symbol,
                logoUrl = market.logoUrl,
                base_currency = market.base_currency,
                last = market.last,
                buy = market.buy,
                sell = market.sell,
                high = market.high,
                low = market.low,
            )
        )
    }

    fun getMarketList(id: String): Flow<List<MarketItemData>> = flow {
        try {
            val markets = apiService.getMarkets()
            val response = apiService.getMarketById(id)
            val marketResponse = response.body() ?: throw Exception("Response kosong")
            val marketSummaries = apiService.getMarketSummaries()
            val marketItemList = markets.mapNotNull { market ->
                val ticker = marketSummaries.tickers[market.tickerId]
                ticker?.let {
                    MarketItemData(
                        id = market.id,
                        base_currency = market.description,
                        symbol = market.symbol,
                        last = it.last,
                        buy = it.buy,
                        sell = it.sell,
                        high = marketResponse.ticker.high,
                        low = marketResponse.ticker.low,
                        logoUrl = market.logoUrl
                    )
                }
            }
            emit(marketItemList)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }.catch { _ ->
        emit(emptyList())
    }

    fun getCryptoById(id: String): Flow<MarketItemData> = flow {
        val result = runCatching {
            val response = apiService.getMarketById(id)
            Log.d("API_RESPONSE", "Market Response: $response")
            val marketResponse = response.body() ?: throw Exception("Response kosong")
            val allMarkets = apiService.getMarkets()
            Log.d("API_RESPONSE", "All Markets: $allMarkets")
            val marketInfo =
                allMarkets.find { it.id == id } ?: throw Exception("Market info tidak ditemukan")
            val previousPrice = marketResponse.ticker.high.toDoubleOrNull() ?: 0.0
            val lastPrice = marketResponse.ticker.low.toDoubleOrNull() ?: 0.0
            val priceChange =
                if (previousPrice > 0) ((lastPrice - previousPrice) / previousPrice * 100).toInt() else 0

            MarketItemData(
                id = id,
                base_currency = marketInfo.description,
                symbol = marketInfo.symbol,
                last = marketResponse.ticker.last,
                buy = marketResponse.ticker.buy,
                sell = marketResponse.ticker.sell,
                logoUrl = marketInfo.logoUrl,
                high = marketResponse.ticker.high,
                low = marketResponse.ticker.low,
                priceChange = priceChange,
            )
        }
        result.onSuccess { emit(it) }
        result.onFailure { throw it }
    }.flowOn(Dispatchers.IO)

}
