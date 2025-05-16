package com.example.hotmovies.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.hotmovies.appplication.login.interfaces.SettingsRepositoryInterface
import com.example.hotmovies.infrastructure.SettingsRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Module
class AppModule {

    @Singleton
    @Provides
    fun providesSettingsRepository(dataStore: DataStore<Preferences>): SettingsRepositoryInterface {
        return SettingsRepository(dataStore)
    }

    @Singleton
    @Provides
    fun providesDataStore(context: Context): DataStore<Preferences> {
        return context.dataStore
    }
}