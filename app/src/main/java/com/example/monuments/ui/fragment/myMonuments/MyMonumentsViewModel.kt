package com.example.monuments.ui.fragment.myMonuments

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.monuments.domain.MonumentBO
import com.example.monuments.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyMonumentsViewModel @Inject constructor(private val mainRepository: MainRepository): ViewModel() {
    fun getMyMonuments(): LiveData<List<MonumentBO>>? {
        return mainRepository.getMyMonuments()
    }

}