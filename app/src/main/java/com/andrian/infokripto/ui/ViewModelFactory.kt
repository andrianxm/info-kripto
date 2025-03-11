package com.andrian.infokripto.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.andrian.infokripto.data.MarketRepository
import com.andrian.infokripto.ui.screen.detail.DetailViewModel
import com.andrian.infokripto.ui.screen.favorite.FavoriteViewModel
import com.andrian.infokripto.ui.screen.home.HomeViewModel

class ViewModelFactory(private val repository: MarketRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            return FavoriteViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class: " + modelClass.name)
    }
}