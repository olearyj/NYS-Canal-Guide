package com.ayc.canalguide

import android.content.Context
import android.content.SharedPreferences
import androidx.core.os.ConfigurationCompat
import androidx.preference.PreferenceManager
import com.ayc.canalguide.data.AppRoomDatabase
import com.ayc.canalguide.data.CanalPreferences
import com.ayc.canalguide.network.CanalsApiService
import com.ayc.canalguide.network.ConnectivityInterceptor
import com.ayc.canalguide.network.ConnectivityInterceptorImpl
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.text.SimpleDateFormat
import javax.inject.Singleton

/**
 * https://medium.com/androiddevelopers/dependency-injection-on-android-with-hilt-67b6031e62d
 */
@Module
@InstallIn(SingletonComponent::class)
object DataModule {


    @Provides
    @Singleton
    fun provideFirebaseAnalytics(@ApplicationContext context: Context) = FirebaseAnalytics.getInstance(context)

    @Provides
    @Singleton
    fun provideAppDB(@ApplicationContext context: Context) = AppRoomDatabase.invoke(context)

    @Provides
    @Singleton
    fun providesConnectivityInceptor(@ApplicationContext context: Context): ConnectivityInterceptor = ConnectivityInterceptorImpl(context)

    @Provides
    @Singleton
    fun providesCanalsApiService(connectivityInterceptor: ConnectivityInterceptor) = CanalsApiService.invoke(connectivityInterceptor)

    @Provides
    @Singleton
    fun providesSharedPreferences(@ApplicationContext context: Context): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    @Provides
    @Singleton
    fun providesCanalPreferences(sharedPrefs: SharedPreferences, @ApplicationContext context: Context): CanalPreferences = CanalPreferences(sharedPrefs, context)

    @Provides
    @Singleton
    fun providesCanalDateFormat(@ApplicationContext context: Context): SimpleDateFormat = SimpleDateFormat("E, dd MMM yyyy hh:mm:ss zzz", ConfigurationCompat.getLocales(context.resources.configuration)[0])

}