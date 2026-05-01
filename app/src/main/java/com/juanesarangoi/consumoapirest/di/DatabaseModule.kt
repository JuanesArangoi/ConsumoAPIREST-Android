package com.juanesarangoi.consumoapirest.di

import android.content.Context
import androidx.room.Room
import com.juanesarangoi.consumoapirest.data.local.AppDatabase
import com.juanesarangoi.consumoapirest.data.local.dao.PostDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }

    @Provides
    fun providePostDao(database: AppDatabase): PostDao {
        return database.postDao()
    }

    @Provides
    fun providePostRemoteKeysDao(database: AppDatabase): PostRemoteKeysDao {
        return database.postRemoteKeysDao()
    }
}
