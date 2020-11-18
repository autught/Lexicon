package com.aut.lexicon.util

import android.content.ComponentName
import android.content.Context
import com.aut.lexicon.library.audio.AudioService
import com.aut.lexicon.library.audio.AudioServiceConnection
import com.aut.lexicon.viewmodel.Factory

object InjectorUtils {
    private fun provideAudioServiceConnection(context: Context): AudioServiceConnection {
        return AudioServiceConnection.getInstance(
            context,
            ComponentName(context, AudioService::class.java)
        )
    }

    fun provideAudioServiceViewModel(context: Context): Factory {
        val applicationContext = context.applicationContext
        val musicServiceConnection = provideAudioServiceConnection(applicationContext)
        return Factory(musicServiceConnection)
    }

//
//    fun provideMediaItemFragmentViewModel(context: Context, mediaId: String)
//            : MediaItemFragmentViewModel.Factory {
//        val applicationContext = context.applicationContext
//        val musicServiceConnection = provideMusicServiceConnection(applicationContext)
//        return MediaItemFragmentViewModel.Factory(mediaId, musicServiceConnection)
//    }
//
//    fun provideNowPlayingFragmentViewModel(context: Context)
//            : NowPlayingFragmentViewModel.Factory {
//        val applicationContext = context.applicationContext
//        val musicServiceConnection = provideMusicServiceConnection(applicationContext)
//        return NowPlayingFragmentViewModel.Factory(
//            applicationContext as Application, musicServiceConnection
//        )
//    }
}