package com.example.cashflow.di

import android.app.Application
import com.example.cashflow.data.manager.LocalUserManagerImp
import com.example.cashflow.domain.manager.LocalUserManager
import com.example.cashflow.domain.useCases.AppEntryUseCases
import com.example.cashflow.domain.useCases.ReadAppEntry
import com.example.cashflow.domain.useCases.SaveAppEntry
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideLocalUserManager(application: Application): LocalUserManager =
        LocalUserManagerImp(application)

    @Provides
    @Singleton
    fun provideAppEntryUseCases(localUserManager: LocalUserManager) = AppEntryUseCases(
        readAppEntry = ReadAppEntry(localUserManager), saveAppEntry = SaveAppEntry(localUserManager)
    )
}
