package com.aut.lexicon.viewmodel

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aut.lexicon.util.Event

/**
 * @description:
 * @author:  79120
 * @date :   2020/11/10 10:07
 */
class LocalDataViewModel : ViewModel() {

    val navigateToRadio: LiveData<Event<Int>> get() = _navigateToRadio
    private val _navigateToRadio = MutableLiveData<Event<Int>>()

    val navigateToPage: LiveData<Event<Int>> get() = _navigateToPage
    private val _navigateToPage = MutableLiveData<Event<Int>>()

    val permissionState: LiveData<Boolean> get() = _permissionState
    private val _permissionState = MutableLiveData<Boolean>()

    val singleData: LiveData<Cursor?> get() = _singleData
    private val _singleData = MutableLiveData<Cursor?>()

    fun pageChanged(position: Int) {
        _navigateToRadio.value = Event(position)
    }

    fun buttonChecked(id: Int) {
        _navigateToPage.value = Event(id)
    }

    fun checkPermission(grant: Boolean) {
        _permissionState.value = grant
    }

    fun swipeCursor(cursor: Cursor?) {
        _singleData.value = cursor
    }

}