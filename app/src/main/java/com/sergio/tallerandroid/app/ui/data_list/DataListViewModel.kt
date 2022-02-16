package com.sergio.tallerandroid.app.ui.data_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sergio.tallerandroid.data.repository.AnimalRepository
import com.sergio.tallerandroid.model.AnimalData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DataListViewModel @Inject constructor(private val repository: AnimalRepository) : ViewModel() {
    var animalList = extractAnimals()

    private val eventChannel = Channel<DataListEvents>(Channel.RENDEZVOUS)
    val eventsFlow = eventChannel.receiveAsFlow()

    private fun extractAnimals(): LiveData<List<AnimalData>>? {
        val animalList = repository.getAnimalList().animalList
        if (animalList == null) {
            errorDataExtraction()
        }
        return animalList
    }

    private fun errorDataExtraction() = viewModelScope.launch {
        eventChannel.send(DataListEvents.ErrorDatabaseExtract)
    }

    fun addAnimalSpecimen(animal: AnimalData) =
        viewModelScope.launch {
            repository.updateAnimal(
                AnimalData(
                    id = animal.id,
                    species = animal.species,
                    specimens = animal.specimens + 1,
                    family = animal.family
                )
            )
        }

    fun subtractAnimalSpecimen(animal: AnimalData) =
        viewModelScope.launch {
            if(animal.specimens > 1) {
                repository.updateAnimal(
                    AnimalData(
                        id = animal.id,
                        species = animal.species,
                        specimens = animal.specimens - 1,
                        family = animal.family
                    )
                )
            } else {
                repository.deleteAnimal(animal)
            }
        }


    sealed class DataListEvents {
        object ErrorDatabaseExtract : DataListEvents()
    }
}