package ru.gidevent.androidapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.gidevent.androidapp.data.repository.AdvertisementRepository
import ru.gidevent.androidapp.data.repository.UserRepository
import ru.gidevent.androidapp.ui.state.UIStateAdvertList
import javax.inject.Inject


@HiltViewModel
class SharedViewModel @Inject constructor(
    private val repository: UserRepository,
    private val advertRepository: AdvertisementRepository
) : ViewModel() {
    private val _progressBar = MutableLiveData<Boolean>(true)
    val progressBar: LiveData<Boolean>
        get() = _progressBar

    fun showProgressIndicator(isShown: Boolean){
        _progressBar.postValue(isShown)
    }
}