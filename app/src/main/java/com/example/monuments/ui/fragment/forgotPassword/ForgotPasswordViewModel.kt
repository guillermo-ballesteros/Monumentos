package com.example.monuments.ui.fragment.forgotPassword

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
class ForgotPasswordViewModel @Inject constructor(private val mainRepository: MainRepository): ViewModel() {
    private val forgotResultLiveData = MutableLiveData<Int>()
    val forgotResult : LiveData<Int>
        get() = forgotResultLiveData

    private val progressBarStatusLiveData = MutableLiveData<Boolean>()
    val progressBarStatus: LiveData<Boolean>
        get() = progressBarStatusLiveData

    fun resetPassword(email: String) {
        progressBarStatusLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            forgotResultLiveData.postValue(mainRepository.restorePassword(email))
            progressBarStatusLiveData.postValue(false)
        }
    }
}