package com.sergio.tallerandroid.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sergio.tallerandroid.model.AnimalData
import java.time.LocalDateTime

@Dao
interface AnimalDAO {
    @Insert(entity = AnimalData::class)
    suspend fun insert(animal: AnimalData)

    @Query("DELETE FROM animal_table")
    suspend fun clear()

    @Query("SELECT COUNT(*) FROM animal_table")
    suspend fun getSpeciesNumber(): Int?

    @Query("SELECT SUM(specimens) FROM animal_table")
    suspend fun getSpecimensSum(): Int?

    @Query("SELECT * FROM animal_table ORDER BY id")
    fun extractAllAnimals() : LiveData<List<AnimalData>>

    @Query("SELECT * FROM animal_table WHERE species = :speciesName")
    suspend fun extractAnimalBySpecies(speciesName: String) : AnimalData?

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAnimal(vararg animal: AnimalData)

    @Delete
    suspend fun deleteAnimal(animal: AnimalData)
}