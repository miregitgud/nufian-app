package com.example.nufianapp.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    // This module can be used for other app-wide dependencies
    // NewsRemoteDataSource is injected directly through its @Inject constructor
    // NewsRepository is bound through RepositoryModule
}