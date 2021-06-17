package com.example.monuments.ui.fragment.monumentsMaps

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.monuments.domain.MonumentBO
import com.example.monuments.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


private const val FILTER_5000 = "5.000 km"

private const val FILTER_1000 = "1.000 km"

private const val FILTER_500 = "500 km"

private const val FILTER_100 = "100 km"

private const val FILTER_50 = "50 km"

private const val FILTER_10 = "10 km"

private const val FILTER_5 = "5 km"

private const val FILTER_1 = "1 km"

private const val PROVIDER = "Monument"

@HiltViewModel
class MonumentsMapViewModel @Inject constructor(private val mainRepository: MainRepository): ViewModel() {

    fun getMonument(title: String): MonumentBO? {
        val list = mainRepository.monuments.value
        return list?.filter {
            it.name == title
        }?.get(0)
    }

    fun getListFiltered(item: String?, location: Location?): List<MonumentBO> {
        return when (item) {
            FILTER_5000 -> {
                filterList(5000, location)
            }
            FILTER_1000 -> {
                filterList(1000, location)
            }
            FILTER_500 -> {
                filterList(500, location)
            }
            FILTER_100 -> {
                filterList(100, location)
            }
            FILTER_50 -> {
                filterList(50, location)
            }
            FILTER_10 -> {
                filterList(10, location)
            }
            FILTER_5 -> {
                filterList(5, location)
            }
            FILTER_1 -> {
                filterList(1, location)
            }
            else -> {
                mainRepository.monuments.value?: emptyList()
            }
        }
    }

    private fun filterList(range: Int, location: Location?): List<MonumentBO> {
        val monuments = mainRepository.monuments.value
        val finalListMonuments = mutableListOf<MonumentBO>()
        if (location != null) {
            if (monuments?.isNotEmpty() == true) {
                for (monument in monuments) {
                    val monumentLocation = Location(PROVIDER)
                    monumentLocation.latitude = monument.location.latitude
                    monumentLocation.longitude = monument.location.longitude
                    if ((monumentLocation.distanceTo(location)/1000) <= range) {
                        finalListMonuments.add(monument)
                    }
                }
                return finalListMonuments
            }

        } else {
            return mainRepository.monuments.value?: emptyList()
        }
        return emptyList()
    }

    fun getMonuments(): LiveData<List<MonumentBO>> {
        return mainRepository.monuments
    }
}