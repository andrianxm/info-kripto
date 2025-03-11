package com.andrian.infokripto.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrian.infokripto.data.MarketRepository
import com.andrian.infokripto.model.MarketItemData
import com.andrian.infokripto.ui.common.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: MarketRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<List<MarketItemData>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<MarketItemData>>> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedSort = MutableStateFlow("default")
    val selectedSort: StateFlow<String> = _selectedSort.asStateFlow()

    init {
        startPollingMarketData()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedSort(sort: String) {
        _selectedSort.value = sort
        fetchMarketData()
    }


    private fun fetchMarketData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            repository.getMarketList(id = String()).catch { e ->
                _uiState.value = UiState.Error(e.message ?: "Terjadi kesalahan")
            }.collect { markets ->
                if (markets.isNotEmpty()) {
                    _uiState.value = UiState.Success(markets.sortedBy { market ->
                        when (_selectedSort.value) {
                            "highest" -> -(market.last.toDoubleOrNull() ?: Double.MIN_VALUE)
                            "lowest" -> market.last.toDoubleOrNull() ?: Double.MAX_VALUE
                            else -> 0.0
                        }
                    }.filter {
                        it.base_currency.contains(
                            _searchQuery.value, ignoreCase = true
                        )
                    })

                    startPollingMarketData()
                } else {
                    _uiState.value = UiState.Error("Data kosong")
                }
            }
        }
    }

    private fun startPollingMarketData() {
        viewModelScope.launch {
            while (true) {
                repository.getMarketList(id = String()).catch { _ -> }.collect { markets ->
                    if (markets.isNotEmpty()) {
                        _uiState.value = UiState.Success(markets.sortedBy { market ->
                            when (_selectedSort.value) {
                                "highest" -> -(market.last.toDoubleOrNull() ?: Double.MIN_VALUE)

                                "lowest" -> market.last.toDoubleOrNull() ?: Double.MAX_VALUE
                                else -> 0.0
                            }
                        }.filter {
                            it.base_currency.contains(
                                _searchQuery.value, ignoreCase = true
                            )
                        })
                    }
                }

                delay(5000)
            }
        }
    }
}


