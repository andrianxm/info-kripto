package com.andrian.infokripto.ui.screen.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrian.infokripto.data.MarketRepository
import com.andrian.infokripto.model.MarketItemData
import com.andrian.infokripto.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: MarketRepository) : ViewModel() {

    private val _marketState = MutableStateFlow<UiState<MarketItemData>>(UiState.Loading)
    val marketState: StateFlow<UiState<MarketItemData>> = _marketState

    private fun updateState(state: UiState<MarketItemData>) {
        _marketState.value = state
    }

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    fun checkIfFavorite(marketId: String) {
        viewModelScope.launch {
            repository.isFavorite(marketId).collect { favorite ->
                _isFavorite.value = favorite
            }
        }
    }

    fun toggleFavorite(market: MarketItemData) {
        viewModelScope.launch {
            if (_isFavorite.value) {
                repository.removeFromFavorites(market)
            } else {
                repository.addToFavorites(market)
            }
            checkIfFavorite(market.id)
        }
    }

    fun getMarketDetail(marketId: String) {
        viewModelScope.launch {
            repository.getCryptoById(marketId).catch { e ->
                Log.e("VIEWMODEL_ERROR", "Error fetching market detail", e)
                updateState(UiState.Error(e.message ?: "Terjadi kesalahan"))
            }

                .collect { updateState(UiState.Success(it)) }
        }
    }

}


