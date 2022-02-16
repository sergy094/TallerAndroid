package com.sergio.tallerandroid.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sergio.tallerandroid.model.AnimalData

const val DATABASE_NAME = "animal_database"

@Database(entities = [AnimalData::class], version = 7, exportSchema = false)
abstract class AnimalDatabase: RoomDatabase() {
    companion object {

        private var instance: AnimalDatabase? = null

        fun getInstance(context: Context): AnimalDatabase {
            synchronized(this) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AnimalDatabase::class.java,
                        DATABASE_NAME
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }

                return instance as AnimalDatabase
            }
        }
    }


    abstract val animalDao: AnimalDAO
}