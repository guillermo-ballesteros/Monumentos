package com.example.monuments.ui.fragment.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.monuments.domain.MonumentBO
import com.example.monuments.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(private val mainRepository: MainRepository): ViewModel() {

    fun getFavoritesMonuments(): LiveData<List<MonumentBO>> {
        return mainRepository.getFavoritesMonuments()
    }
}