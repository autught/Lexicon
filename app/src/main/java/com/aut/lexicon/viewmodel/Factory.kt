package com.aut.lexicon.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aut.lexicon.library.audio.AudioServiceConnection

class Factory(private val connection: AudioServiceConnection) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(AudioServiceConnection::class.java)
            .newInstance(connection)
    }
}