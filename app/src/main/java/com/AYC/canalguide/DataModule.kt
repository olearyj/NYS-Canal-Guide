package com.AYC.canalguide

import android.content.Context
import com.AYC.canalguide.data.AppRoomDatabase
import com.AYC.canalguide.network.CanalsApiService
import com.AYC.canalguide.network.ConnectivityInterceptor
import com.AYC.canalguide.network.ConnectivityInterceptorImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * https://medium.com/androiddevelopers/dependency-injection-on-android-with-hilt-67b6031e62d
 */
@Module
@InstallIn(ApplicationComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideAppDB(@ApplicationContext context: Context) = AppRoomDatabase.invoke(context)

    @Provides
    @Singleton
    fun providesConnectivityInceptor(@ApplicationContext context: Context): ConnectivityInterceptor = ConnectivityInterceptorImpl(context)

    @Provides
    @Singleton
    fun providesCanalsAPiService(connectivityInterceptor: ConnectivityInterceptor) = CanalsApiService.invoke(connectivityInterceptor)

}