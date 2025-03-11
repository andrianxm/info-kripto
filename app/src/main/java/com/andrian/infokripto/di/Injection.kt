package com.andrian.infokripto.di

import android.content.Context
import com.andrian.infokripto.data.MarketRepository
import com.andrian.infokripto.data.local.AppDatabase
import com.andrian.infokripto.data.remote.ApiConfig

object Injection {
    fun provideRepository(context: Context): MarketRepository {
        val apiService = ApiConfig.getApiService()
        val database = AppDatabase.getDatabase(context)
        val dao = database.favoriteDao()
        return MarketRepository(apiService, dao)
    }
}

