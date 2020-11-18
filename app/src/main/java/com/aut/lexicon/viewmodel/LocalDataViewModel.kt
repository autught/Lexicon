package com.aut.lexicon.viewmodel

import android.database.Cursor
import android.net.Uri
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.aut.lexicon.library.audio.AudioServiceConnection
import com.aut.lexicon.library.audio.EMPTY_PLAYBACK_STATE
import com.aut.lexicon.library.audio.NOTHING_PLAYING
import com.aut.lexicon.util.Event

/**
 * @description:
 * @author:  79120
 * @date :   2020/11/10 10:07
 */
class LocalDataViewModel(private val connection: AudioServiceConnection) : ViewModel() {

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

    fun playMedia(uri: Uri) {
        connection.transportControls.playFromUri(uri, null)
    }


    /**
     * Pass the status of the [MusicServiceConnection.networkFailure] through.
     */
    val networkError = Transformations.map(connection.networkFailure) { it }

    private val subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(parentId: String, children: List<MediaBrowserCompat.MediaItem>) {
        }
    }

    private val playbackStateObserver = Observer<PlaybackStateCompat> {
        val playbackState = it ?: EMPTY_PLAYBACK_STATE
        val metadata = connection.nowPlaying.value ?: NOTHING_PLAYING
//        if (metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID) != null) {
//            _mediaItems.postValue(updateState(playbackState, metadata))
//        }
    }

    private val mediaMetadataObserver = Observer<MediaMetadataCompat> {
        val playbackState = musicServiceConnection.playbackState.value ?: EMPTY_PLAYBACK_STATE
        val metadata = it ?: NOTHING_PLAYING
//        if (metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID) != null) {
//            _mediaItems.postValue(updateState(playbackState, metadata))
//        }
    }

    private val musicServiceConnection = connection.also {
        it.subscribe(mediaId, subscriptionCallback)

        it.playbackState.observeForever(playbackStateObserver)
        it.nowPlaying.observeForever(mediaMetadataObserver)
    }

    /**
     * Since we use [LiveData.observeForever] above (in [musicServiceConnection]), we want
     * to call [LiveData.removeObserver] here to prevent leaking resources when the [ViewModel]
     * is not longer in use.
     *
     * For more details, see the kdoc on [musicServiceConnection] above.
     */
    override fun onCleared() {
        super.onCleared()

        // Remove the permanent observers from the MusicServiceConnection.
        musicServiceConnection.playbackState.removeObserver(playbackStateObserver)
        musicServiceConnection.nowPlaying.removeObserver(mediaMetadataObserver)

        // And then, finally, unsubscribe the media ID that was being watched.
        musicServiceConnection.unsubscribe(mediaId, subscriptionCallback)
    }

    private fun getResourceForMediaId(mediaId: String): Int {
        val isActive = mediaId == musicServiceConnection.nowPlaying.value?.id
        val isPlaying = musicServiceConnection.playbackState.value?.isPlaying ?: false
        return when {
            !isActive -> NO_RES
            isPlaying -> R.drawable.ic_pause_black_24dp
            else -> R.drawable.ic_play_arrow_black_24dp
        }
    }

    private fun updateState(
        playbackState: PlaybackStateCompat,
        mediaMetadata: MediaMetadataCompat
    ): List<MediaItemData> {

        val newResId = when (playbackState.isPlaying) {
            true -> R.drawable.ic_pause_black_24dp
            else -> R.drawable.ic_play_arrow_black_24dp
        }

        return mediaItems.value?.map {
            val useResId = if (it.mediaId == mediaMetadata.id) newResId else NO_RES
            it.copy(playbackRes = useResId)
        } ?: emptyList()
    }

}