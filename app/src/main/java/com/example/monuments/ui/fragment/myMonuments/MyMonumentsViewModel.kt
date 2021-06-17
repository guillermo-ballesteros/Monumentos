package com.example.monuments.ui.fragment.myMonuments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monuments.domain.MonumentBO
import com.example.monuments.repository.MainRepository
import com.example.monuments.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyMonumentsViewModel @Inject constructor(private val mainRepository: MainRepository): ViewModel() {

    private val progressBarLiveData = MutableLiveData<Boolean>()
    val progressBar: LiveData<Boolean>
        get() = progressBarLiveData

    private val resultLiveData = MutableLiveData<Int>()
    val result: LiveData<Int>
        get() = resultLiveData

    fun getMyMonuments(): LiveData<List<MonumentBO>>? {
        return mainRepository.getMyMonuments()
    }

    fun removeMonument(id: String) {
        progressBarLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            resultLiveData.postValue(mainRepository.removeMonument(id))

            progressBarLiveData.postValue(false)
        }
    }

    fun notificationDone() {
        resultLiveData.value = Constants.DELETED_NOTIFY_DONE_CODE
    }

}