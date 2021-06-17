package com.example.monuments.ui.fragment.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monuments.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val mainRepository: MainRepository): ViewModel() {
    private val registerResultLiveData = MutableLiveData<Int>()
    val registerResult : LiveData<Int>
        get() = registerResultLiveData

    private val progressBarStatusMutable = MutableLiveData<Boolean>()
    val progressBarStatus: LiveData<Boolean>
        get() = progressBarStatusMutable

    fun createNewAccount(name: String, lastName: String, email: String, password1: String, password2: String) {
        progressBarStatusMutable.value = true
        viewModelScope.launch(Dispatchers.IO) {
            registerResultLiveData.postValue(mainRepository.createNewAccount(name, lastName, email, password1, password2))
            progressBarStatusMutable.postValue(false)
        }
    }

}