package com.example.monuments.ui.activity

import androidx.lifecycle.ViewModel
import com.example.monuments.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(private val mainRepository: MainRepository): ViewModel() {

    fun logOut() {
        mainRepository.logOut()
    }
}