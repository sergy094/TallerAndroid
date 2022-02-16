package com.sergio.tallerandroid.data.di

import android.content.Context
import com.sergio.tallerandroid.data.database.AnimalDAO
import com.sergio.tallerandroid.data.database.AnimalDatabase
import com.sergio.tallerandroid.data.repository.AnimalRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun animalDao(animalDatabase: AnimalDatabase): AnimalDAO =
        animalDatabase.animalDao

    @Provides
    @Singleton
    fun exerciseDatabase(@ApplicationContext applicationContext: Context): AnimalDatabase =
        AnimalDatabase.getInstance(applicationContext)

    @Provides
    @Singleton
    fun animalRepository(animalDao: AnimalDAO): AnimalRepository =
        AnimalRepository(animalDao)
}