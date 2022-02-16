package com.sergio.tallerandroid.app.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sergio.tallerandroid.data.repository.AnimalRepository
import com.sergio.tallerandroid.model.AnimalData
import com.sergio.tallerandroid.model.FamilyData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: AnimalRepository) : ViewModel(){

    private val eventChannel = Channel<HomeEvents>(Channel.RENDEZVOUS)
    val eventsFlow = eventChannel.receiveAsFlow()

    private val errorEventChannel = Channel<List<FormErrorEvents>>(Channel.RENDEZVOUS)
    val errorEventFlow = errorEventChannel.receiveAsFlow()

    private val _speciesNumber = MutableLiveData<Int>()
    val speciesNumber: LiveData<Int>
        get() = _speciesNumber

    private val _specimensNumber = MutableLiveData<Int>()
    val specimensNumber: LiveData<Int>
        get() = _specimensNumber

    fun setStatisticsData() = viewModelScope.launch {
        _speciesNumber.value = repository.checkSpeciesNumber() ?: 0
        _specimensNumber.value = repository.checkSpecimensNumber() ?: 0
    }

    fun onAddAnimalClick() = viewModelScope.launch {
        eventChannel.send(HomeEvents.AddAnimalDialogEvent)
    }

    fun onViewDataClick() = viewModelScope.launch {
        eventChannel.send(HomeEvents.OnViewDataClickEvent)
    }

    fun onDeleteDataClick() = viewModelScope.launch {
        eventChannel.send(HomeEvents.OnDeleteDataClickEvent)
    }

    fun validateForm(species: String?, family: FamilyData?) {
        val formErrorEvents = mutableListOf<FormErrorEvents>()
        var valid = true

        if(species.isNullOrEmpty()) {
            valid = false
            formErrorEvents.add(FormErrorEvents.EmptySpeciesError)
        }
        if(family == null) {
            valid = false
            formErrorEvents.add(FormErrorEvents.EmptyFamilyError)
        }

        setInvalidForm(formErrorEvents)

        if(valid) {
            species?.let{ speciesUnwrapped ->
                family?.let { familyUnwrapped ->
                    viewModelScope.launch {
                        val existingAnimal = findAnimalInList(speciesUnwrapped)

                        if (existingAnimal == null) {
                            insertAnimal(speciesUnwrapped, familyUnwrapped)
                            eventChannel.send(HomeEvents.AnimalInserted)
                        } else {
                            addExistingAnimal(speciesUnwrapped, familyUnwrapped, existingAnimal)
                            eventChannel.send(HomeEvents.ExistingAnimalInserted)
                        }
                    }
                }
            }
        }
    }

    private suspend fun findAnimalInList(species: String) =
        repository.findAnimal(species).animal

    private fun addExistingAnimal(species: String, family: FamilyData, oldAnimal: AnimalData) =
        viewModelScope.launch {
            repository.updateAnimal(
                AnimalData(
                    id = oldAnimal.id,
                    species = species,
                    specimens = oldAnimal.specimens + 1,
                    family = family
                )
            )
        }

    private fun insertAnimal(species: String, family: FamilyData) =
        viewModelScope.launch {
                    repository.insertAnimal(
                        AnimalData(
                            species = species,
                            specimens = 1,
                            family = family
                        )
                    )
        }

    private fun setInvalidForm(errorEvents: List<FormErrorEvents>) = viewModelScope.launch {
        errorEventChannel.send(errorEvents)
    }

    fun deleteData() = viewModelScope.launch {
        repository.deleteAllData()
        eventChannel.send(HomeEvents.DeleteDataEvent)
    }

    sealed class HomeEvents{
        object AddAnimalDialogEvent: HomeEvents()
        object OnViewDataClickEvent: HomeEvents()
        object OnDeleteDataClickEvent: HomeEvents()
        object DeleteDataEvent: HomeEvents()
        object AnimalInserted: HomeEvents()
        object ExistingAnimalInserted: HomeEvents()
    }

    sealed class FormErrorEvents {
        object EmptySpeciesError : FormErrorEvents()
        object EmptyFamilyError : FormErrorEvents()
    }
}