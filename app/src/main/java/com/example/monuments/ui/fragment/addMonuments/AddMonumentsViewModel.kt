package com.example.monuments.ui.fragment.addMonuments

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monuments.domain.LocationBO
import com.example.monuments.repository.MainRepository
import com.example.monuments.utils.Constants
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddMonumentsViewModel @Inject constructor(private val mainRepository: MainRepository): ViewModel() {
    private val resultLiveData = MutableLiveData<Int>()

    val result: LiveData<Int>
        get() = resultLiveData

    private val imagesLiveData = MutableLiveData<MutableList<Uri>>(mutableListOf())

    val images: LiveData<MutableList<Uri>>
        get() = imagesLiveData

    private val progressBarLiveData = MutableLiveData<Boolean>()

    val progressBar: LiveData<Boolean>
        get() = progressBarLiveData

    fun addImage(image: Uri?) {
        val list = mutableListOf<Uri>()
        imagesLiveData.value?.let { list.addAll(it) }
        image?.let { list.add(it) }
        imagesLiveData.value = list
        Log.d(":::IMAGES", imagesLiveData.value?.size.toString())
    }

    fun deleteImage(position: Int) {
        val list = mutableListOf<Uri>()
        imagesLiveData.value?.let { list.addAll(it) }
        list.removeAt(position)
        imagesLiveData.value = list
    }


    fun addMonument(name: String, city: String, description: String, urlExtra: String?, location: LatLng) {
        progressBarLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            if (imagesLiveData.value?.isEmpty() == true|| name.isBlank() || city.isBlank() || description.isBlank()) {
                resultLiveData.postValue(Constants.NULL_DATA_CODE)

            } else {
                val imagesList = imagesLiveData.value?.let { mainRepository.addImages(it) }
                val finalLocation = LocationBO(location.latitude, location.longitude)
                if (imagesList != null) {
                    mainRepository.addData(name, city, urlExtra, description, finalLocation, imagesList)
                    resultLiveData.postValue(Constants.SUCCESS_CODE)
                }
            }
            progressBarLiveData.postValue(false)
        }
    }
}