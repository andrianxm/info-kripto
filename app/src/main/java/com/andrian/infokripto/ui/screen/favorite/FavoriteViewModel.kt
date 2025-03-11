package com.andrian.infokripto.ui.screen.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrian.infokripto.data.MarketRepository
import com.andrian.infokripto.model.MarketItemData
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class FavoriteViewModel(repository: MarketRepository) : ViewModel() {

    val favoriteMarkets: StateFlow<List<MarketItemData>> = repository.getAllFavorites()
        .map { favorites ->
            favorites.map { market ->
                MarketItemData(
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
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
