package com.sergio.tallerandroid.data.repository

import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.lifecycle.LiveData
import com.sergio.tallerandroid.data.database.AnimalDAO
import com.sergio.tallerandroid.model.AnimalData
import javax.inject.Inject

class AnimalRepository @Inject constructor(private val animalDao: AnimalDAO) {

    suspend fun insertAnimal(newAnimal: AnimalData) : RepositoryExceptions.TransactionException {
        return try {
            animalDao.insert(animal = newAnimal)
            RepositoryExceptions.TransactionException(false)
        } catch (e: SQLiteException) {
            RepositoryExceptions.TransactionException(true)
        }
    }

    suspend fun checkSpeciesNumber() = animalDao.getSpeciesNumber()

    suspend fun checkSpecimensNumber() = animalDao.getSpecimensSum()

    suspend fun findAnimal(species: String) : RepositoryExceptions.SingleSpeciesExtraction {
        return try {
            val animal = animalDao.extractAnimalBySpecies(species)
            if(animal == null) {
                RepositoryExceptions.SingleSpeciesExtraction(null)
            } else {
                RepositoryExceptions.SingleSpeciesExtraction(animal)
            }
        } catch (e: SQLiteException) {
            RepositoryExceptions.SingleSpeciesExtraction(null)
        }
    }

    fun getAnimalList() : RepositoryExceptions.MultipleSpeciesExtraction {
        return try {
            val animalList = animalDao.extractAllAnimals()
            RepositoryExceptions.MultipleSpeciesExtraction(animalList)
        } catch (e: SQLiteException) {
            RepositoryExceptions.MultipleSpeciesExtraction(null)
        }
    }

    suspend fun deleteAllData() {
        animalDao.clear()
    }

    suspend fun updateAnimal(animal: AnimalData) {
        animalDao.updateAnimal(animal)
    }

    suspend fun deleteAnimal(animal: AnimalData) {
        animalDao.deleteAnimal(animal)
    }

    sealed class RepositoryExceptions {
        data class TransactionException(val error: Boolean) : RepositoryExceptions()
        data class SingleSpeciesExtraction(val animal: AnimalData?) : RepositoryExceptions()
        data class MultipleSpeciesExtraction(val animalList: LiveData<List<AnimalData>>?) : RepositoryExceptions()
    }
}