package com.example.monuments.ui.fragment.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monuments.repository.MainRepository
import com.example.monuments.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(val mainRepository: MainRepository): ViewModel() {

    private val loginResultLiveData = MutableLiveData<Int>()
    val loginResult : LiveData<Int>
        get() = loginResultLiveData

    private val progressBarStatusLiveData = MutableLiveData<Boolean>()
    val progressBarStatus: LiveData<Boolean>
        get() = progressBarStatusLiveData

    fun login(email: String, password: String) {
        progressBarStatusLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            loginResultLiveData.postValue(mainRepository.login(email, password))
            progressBarStatusLiveData.postValue(false)
        }
    }

}