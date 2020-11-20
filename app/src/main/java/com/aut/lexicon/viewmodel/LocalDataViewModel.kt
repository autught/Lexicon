package com.aut.lexicon.viewmodel

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.aut.lexicon.library.audio.*
import com.aut.lexicon.util.Event
import timber.log.Timber

/**
 * @description:
 * @author:  79120
 * @date :   2020/11/10 10:07
 */
class LocalDataViewModel(private val connection: AudioServiceConnection) : ViewModel() {

    /**
     * Pass the status of the [AudioServiceConnection.networkFailure] through.
     */
    val networkError = Transformations.map(connection.networkFailure) { it }

    val navigateToRadio: LiveData<Event<Int>> get() = _navigateToRadio
    private val _navigateToRadio = MutableLiveData<Event<Int>>()

    val navigateToPage: LiveData<Event<Int>> get() = _navigateToPage
    private val _navigateToPage = MutableLiveData<Event<Int>>()

    val singleData: LiveData<Cursor?> get() = _singleData
    private val _singleData = MutableLiveData<Cursor?>()

    fun pageChanged(position: Int) {
        _navigateToRadio.value = Event(position)
    }

    fun buttonChecked(id: Int) {
        _navigateToPage.value = Event(id)
    }

    fun swipeCursor(cursor: Cursor?) {
        _singleData.value = cursor
    }

    fun playMedia(uri: Uri, bundle: Bundle) {
        val nowPlaying = connection.nowPlaying.value
        val transportControls = connection.transportControls

        val isPrepared = connection.playbackState.value?.isPrepared ?: false
        if (isPrepared && uri == nowPlaying?.mediaUri) {
            connection.playbackState.value?.let { playbackState ->
                when {
                    playbackState.isPlaying -> transportControls.pause()
                    playbackState.isPlayEnabled -> transportControls.play()
                    else ->
                        Timber.w("Playable item clicked but neither play nor pause are enabled!")
                }
            }
        } else {
            transportControls.playFromUri(uri, bundle)
        }
    }


}