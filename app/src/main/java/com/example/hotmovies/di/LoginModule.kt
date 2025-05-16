package com.example.hotmovies.di

import com.example.hotmovies.appplication.login.interfaces.LoginRepositoryInterface
import com.example.hotmovies.infrastructure.LoginRepository
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class LoginModule {

    @Singleton
    @Binds
    abstract fun providesLoginRepository(repository: LoginRepository): LoginRepositoryInterface
}