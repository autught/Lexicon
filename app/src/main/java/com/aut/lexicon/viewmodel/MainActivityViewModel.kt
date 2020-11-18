package com.aut.lexicon.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aut.lexicon.library.audio.AudioServiceConnection
import com.aut.lexicon.library.bottomnavigation.BottomNavigationEntity
import com.aut.lexicon.util.Event

class MainActivityViewModel(private val connection: AudioServiceConnection) : ViewModel() {

    val navigationEntities: LiveData<Event<MutableList<BottomNavigationEntity>>> get() = _navigationEntities
    private val _navigationEntities = MutableLiveData<Event<MutableList<BottomNavigationEntity>>>()

    val navigateToItem: LiveData<Event<Int>> get() = _navigateToItem
    private val _navigateToItem = MutableLiveData<Event<Int>>()

    fun setNavigationData(data: MutableList<BottomNavigationEntity>) {
        _navigationEntities.value = Event(data)
    }

    fun setNavigationItem(position: Int) {
        _navigateToItem.value = Event(position)
    }

}