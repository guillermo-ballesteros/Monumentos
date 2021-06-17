package com.example.monuments.ui.fragment.allMonuments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monuments.domain.MonumentBO
import com.example.monuments.repository.MainRepository
import com.example.monuments.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllMonumentsViewModel @Inject constructor(private val mainRepository: MainRepository) : ViewModel() {

    private val progressBarLiveData = MutableLiveData<Boolean>()
    val progressBar: LiveData<Boolean>
        get() = progressBarLiveData

    private val resultLiveData = MutableLiveData<Int>()
    val result: LiveData<Int>
        get() = resultLiveData

    fun refreshData() {
        progressBarLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            mainRepository.requestData()
            progressBarLiveData.postValue(false)
        }
    }

    fun changeFavorite(position: Int) {
        progressBarLiveData.value = true
        val monumentBO = mainRepository.monuments.value?.get(position)
        viewModelScope.launch(Dispatchers.IO) {
            if (monumentBO != null) {
                mainRepository.changeFavorite(monumentBO)
            }
            progressBarLiveData.postValue(false)
        }

    }

    fun getMonuments(): LiveData<List<MonumentBO>> {
        return mainRepository.monuments
    }

    fun removeMonument(position: Int) {
        progressBarLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            resultLiveData.postValue(mainRepository.removeMonument(position))
            progressBarLiveData.postValue(false)
        }
    }

    fun notificationDone() {
        resultLiveData.value = Constants.DELETED_NOTIFY_DONE_CODE
    }
}