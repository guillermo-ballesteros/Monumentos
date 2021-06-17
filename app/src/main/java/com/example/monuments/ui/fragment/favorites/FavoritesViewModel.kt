package com.example.monuments.ui.fragment.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monuments.domain.MonumentBO
import com.example.monuments.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(private val mainRepository: MainRepository): ViewModel() {

    private val progressBarLiveData = MutableLiveData<Boolean>()
    val progressBar: LiveData<Boolean>
        get() = progressBarLiveData


    fun getFavoritesMonuments(): LiveData<List<MonumentBO>> {
        return mainRepository.getFavoritesMonuments()
    }

    fun changeFavorite(id: String) {
        progressBarLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            mainRepository.changeFavorite(id)

            progressBarLiveData.postValue(false)
        }

    }
}