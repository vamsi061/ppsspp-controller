package com.gamingcontroller.app.di

import com.gamingcontroller.app.data.NetworkService
import com.gamingcontroller.app.domain.repository.ConnectionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindConnectionRepository(
        networkService: NetworkService
    ): ConnectionRepository
}