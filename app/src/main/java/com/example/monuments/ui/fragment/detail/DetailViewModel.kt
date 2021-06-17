package com.example.monuments.ui.fragment.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monuments.domain.CommentBO
import com.example.monuments.domain.MonumentBO
import com.example.monuments.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(private val mainRepository: MainRepository): ViewModel() {
    private val monumentLiveData = MutableLiveData<MonumentBO>()
    val monument: LiveData<MonumentBO>
        get() = monumentLiveData

    private val progressBarStatusLiveData = MutableLiveData<Boolean>()
    val progressBarStatus: LiveData<Boolean>
        get() = progressBarStatusLiveData

    fun getMonument(id: String) {
        progressBarStatusLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            monumentLiveData.postValue(mainRepository.getMonument(id))
            progressBarStatusLiveData.postValue(false)
        }

    }

    fun changeFavorite(id: String) {
        progressBarStatusLiveData.value = true

        viewModelScope.launch(Dispatchers.IO) {

            mainRepository.changeFavorite(id)

            monumentLiveData.postValue(monument.value?.id?.let {
                mainRepository.getMonument(it)
            })
            progressBarStatusLiveData.postValue(false)
        }
    }

    fun getComments(id: String): LiveData<List<CommentBO>> {
        return mainRepository.getComments(id)
    }
}