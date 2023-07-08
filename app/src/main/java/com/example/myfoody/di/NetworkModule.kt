package com.example.myfoody.di

import com.example.myfoody.util.Constants.Companion.BASE_URL
import com.example.myfoody.date.network.FoodRecipesApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

// NetworkModule is a Dagger module that provides networking-related objects for dependency injection.
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // Provides a singleton OkHttpClient with specific timeouts for read and connection.
    @Singleton
    @Provides
    fun provideHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(15, TimeUnit.SECONDS) // Set the read timeout to 15 seconds.
            .connectTimeout(15, TimeUnit.SECONDS) // Set the connection timeout to 15 seconds.
            .build()
    }

    // Provides a singleton GsonConverterFactory for converting JSON to objects using Gson.
    @Singleton
    @Provides
    fun provideConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    // Provides a singleton Retrofit instance with the base URL, OkHttpClient, and GsonConverterFactory.
    @Singleton
    @Provides
    fun provideRetrofitInstance(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL) // Set the base URL for API requests.
            .client(okHttpClient) // Use the custom OkHttpClient.
            .addConverterFactory(gsonConverterFactory) // Use the GsonConverterFactory for JSON conversion.
            .build()
    }

    // Provides a singleton FoodRecipesApi service for making API requests.
    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): FoodRecipesApi {
        return retrofit.create(FoodRecipesApi::class.java)
    }
}