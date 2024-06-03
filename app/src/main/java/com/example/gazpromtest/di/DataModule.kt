package com.example.gazpromtest.di

import com.example.gazpromtest.data.network.KtorClient
import com.example.gazpromtest.data.repository.MainRepositoryImpl
import com.example.gazpromtest.domain.repository.MainRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import io.ktor.client.HttpClient

@Module
interface DataModule {

    @Binds
    @ApplicationScope
    fun bindMainRepository(impl: MainRepositoryImpl): MainRepository

    companion object {

        @Provides
        @ApplicationScope
        fun provideKtorClient(): HttpClient = KtorClient.client

    }

}