package com.example.monuments.ui.fragment.comments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monuments.domain.CommentBO
import com.example.monuments.domain.MonumentBO
import com.example.monuments.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentsViewModel @Inject constructor(private val mainRepository: MainRepository): ViewModel() {

    private val monumentLiveData = MutableLiveData<MonumentBO>()
    val monument: LiveData<MonumentBO>
        get() = monumentLiveData

    private val progressBarStatusLiveData = MutableLiveData<Boolean>()
    val progressBarStatus: LiveData<Boolean>
        get() = progressBarStatusLiveData


    fun addComment(rate: Int, comment: String, id: String) {
        progressBarStatusLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            mainRepository.addComment(rate, comment, id)
            progressBarStatusLiveData.postValue(false)
        }
    }

    fun getComments(id: String): LiveData<List<CommentBO>> {
        return mainRepository.getComments(id)
    }

    fun sortComments(comments: List<CommentBO>): List<CommentBO> {
        return comments.sortedBy {
            it.createTime
        }
    }

    fun getData() {
        progressBarStatusLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            mainRepository.refreshComments()
            progressBarStatusLiveData.postValue(false)
        }
    }

    fun getEmail(): String {
        return mainRepository.auth.currentUser?.email?: ""
    }
}