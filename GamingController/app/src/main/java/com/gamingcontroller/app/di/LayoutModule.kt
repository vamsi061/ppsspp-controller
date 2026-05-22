package com.gamingcontroller.app.di

import android.content.Context
import com.gamingcontroller.app.data.LayoutRepository
import com.gamingcontroller.app.data.LayoutRepositoryImpl
import com.gamingcontroller.app.data.SettingsRepository
import com.gamingcontroller.app.data.SettingsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LayoutModule {

    @Provides
    @Singleton
    fun provideLayoutRepository(
        @ApplicationContext context: Context
    ): LayoutRepository {
        return LayoutRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(
        @ApplicationContext context: Context
    ): SettingsRepository {
        return SettingsRepositoryImpl(context)
    }
}