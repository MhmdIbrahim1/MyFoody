package com.example.myfoody.di

import com.example.myfoody.data.local.datasource.RecipesLocalDataSource
import com.example.myfoody.data.local.datasource.RecipesLocalDataSourceImpl
import com.example.myfoody.data.local.datastore.PreferencesDataSource
import com.example.myfoody.data.local.datastore.PreferencesDataSourceImpl
import com.example.myfoody.data.network.AndroidNetworkMonitor
import com.example.myfoody.data.remote.datasource.RecipesRemoteDataSource
import com.example.myfoody.data.remote.datasource.RecipesRemoteDataSourceImpl
import com.example.myfoody.data.repository.FavoritesRepositoryImpl
import com.example.myfoody.data.repository.FoodJokeRepositoryImpl
import com.example.myfoody.data.repository.RecipesRepositoryImpl
import com.example.myfoody.data.repository.UserPreferencesRepositoryImpl
import com.example.myfoody.domain.repository.FavoritesRepository
import com.example.myfoody.domain.repository.FoodJokeRepository
import com.example.myfoody.domain.repository.NetworkMonitor
import com.example.myfoody.domain.repository.RecipesRepository
import com.example.myfoody.domain.repository.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindNetworkMonitor(
        impl: AndroidNetworkMonitor
    ): NetworkMonitor

    @Binds
    @Singleton
    abstract fun bindRecipesRemoteDataSource(
        impl: RecipesRemoteDataSourceImpl
    ): RecipesRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindRecipesLocalDataSource(
        impl: RecipesLocalDataSourceImpl
    ): RecipesLocalDataSource

    @Binds
    @Singleton
    abstract fun bindPreferencesDataSource(
        impl: PreferencesDataSourceImpl
    ): PreferencesDataSource

    @Binds
    @Singleton
    abstract fun bindFoodJokeRepository(
        impl: FoodJokeRepositoryImpl
    ): FoodJokeRepository

    @Binds
    @Singleton
    abstract fun bindFavoritesRepository(
        impl: FavoritesRepositoryImpl
    ): FavoritesRepository

    @Binds
    @Singleton
    abstract fun bindRecipesRepository(
        impl: RecipesRepositoryImpl
    ): RecipesRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        impl: UserPreferencesRepositoryImpl
    ): UserPreferencesRepository
}
