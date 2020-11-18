package com.aut.lexicon.library.audio

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import com.aut.lexicon.BuildConfig
import com.aut.lexicon.R
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.RepeatModeActionProvider
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import timber.log.Timber


open class AudioService : MediaBrowserServiceCompat() {
    private lateinit var notificationManager: NotificationManager
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector
    private var currentPlaylistItems: List<MediaMetadataCompat> = emptyList()
    private val dataSourceFactory: DefaultDataSourceFactory by lazy {
        DefaultDataSourceFactory(
            this,
            Util.getUserAgent(this, LEXICON_USER_AGENT),
            null
        )
    }
    private var isForegroundService = false
    private val mAudioAttributes = AudioAttributes.Builder()
        .setContentType(C.CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()
    private val playerListener = PlayerEventListener()

    private val exoPlayer: ExoPlayer by lazy {
        SimpleExoPlayer.Builder(this).build().apply {
            setAudioAttributes(mAudioAttributes, true)
            setHandleAudioBecomingNoisy(true)
            addListener(playerListener)
        }
    }

    override fun onCreate() {
        super.onCreate()
        val sessionActivityPendingIntent =
            packageManager?.getLaunchIntentForPackage(packageName)?.let { sessionIntent ->
                PendingIntent.getActivity(this, 0, sessionIntent, 0)
            }

        mediaSession = MediaSessionCompat(this, TAG)
            .apply {
                setSessionActivity(sessionActivityPendingIntent)
                isActive = true
            }

        sessionToken = mediaSession.sessionToken

        notificationManager = NotificationManager(
            this,
            mediaSession.sessionToken,
            PlayerNotificationListener()
        )
        // ExoPlayer will manage the MediaSession for us.
        mediaSessionConnector = MediaSessionConnector(mediaSession)
        mediaSessionConnector.setPlaybackPreparer(PlaybackPreparer())
        mediaSessionConnector.setCustomActionProviders(RepeatModeActionProvider(this),
            ActionProvider())
        mediaSessionConnector.setQueueNavigator(QueueNavigator(mediaSession))
        mediaSessionConnector.setPlayer(exoPlayer)
        notificationManager.showNotificationForPlayer(exoPlayer)
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        saveRecentSongToStorage()
        super.onTaskRemoved(rootIntent)
        exoPlayer.stop(/* reset= */true)
    }

    override fun onDestroy() {
        mediaSession.run {
            isActive = false
            release()
        }
        exoPlayer.removeListener(playerListener)
        exoPlayer.release()
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?,
    ): BrowserRoot? {
        return if (clientPackageName != packageName)
            if (BuildConfig.DEBUG) BrowserRoot(MEDIA_ID_EMPTY_ROOT, null)
            else null
        else BrowserRoot(MEDIA_ID_ROOT, null)
    }

    override fun onLoadChildren(
        parentMediaId: String,
        result: Result<List<MediaItem>>,
    ) {
        result.sendResult(null)
    }

    private fun preparePlaylist(
        metadataList: MutableList<MediaMetadataCompat>,
        itemToPlay: MediaMetadataCompat?,
        playWhenReady: Boolean,
        playbackStartPositionMs: Long,
    ) {
        val initialWindowIndex = itemToPlay?.run { metadataList.indexOf(this) } ?: 0
        currentPlaylistItems = metadataList

        exoPlayer.playWhenReady = playWhenReady
        exoPlayer.stop(/* reset= */ true)
        val mediaSource = metadataList.toMediaSource(dataSourceFactory)
        exoPlayer.prepare(mediaSource)
        exoPlayer.seekTo(initialWindowIndex, playbackStartPositionMs)
    }

    private fun saveRecentSongToStorage() {
        val description = currentPlaylistItems[exoPlayer.currentWindowIndex].description
        val position = exoPlayer.currentPosition
    }

    private inner class QueueNavigator(
        mediaSession: MediaSessionCompat,
    ) : TimelineQueueNavigator(mediaSession) {
        /**
         * 傳入現在播放歌曲的描述，讓 Notification 顯示使用
         */
        override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat =
            currentPlaylistItems[windowIndex].description
    }

    private inner class PlaybackPreparer : MediaSessionConnector.PlaybackPreparer {

        override fun getSupportedPrepareActions(): Long =
            PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID or
                    PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or
                    PlaybackStateCompat.ACTION_PREPARE_FROM_URI or
                    PlaybackStateCompat.ACTION_PLAY_FROM_URI

        override fun onPrepare(playWhenReady: Boolean) {
//            val recentSong = storage.loadRecentSong() ?: return
//            onPrepareFromMediaId(
//                recentSong.mediaId!!,
//                playWhenReady,
//                recentSong.description.extras
//            )
        }

        override fun onPrepareFromMediaId(
            mediaId: String,
            playWhenReady: Boolean,
            extras: Bundle?,
        ) {
//            launch {
//                val itemToPlay: MediaMetadataCompat? = mediaSource.find { item ->
//                    item.id == mediaId
//                }
//                if (itemToPlay == null) {
//                    Timber.w("Content not found: MediaID=$mediaId")
//                    // TODO: Notify caller of the error.
//                } else {
//                    val playbackStartPositionMs =
//                        extras?.getLong(
//                            MEDIA_DESCRIPTION_EXTRAS_START_PLAYBACK_POSITION_MS,
//                            C.TIME_UNSET
//                        )
//                            ?: C.TIME_UNSET
//
//                    preparePlaylist(
//                        buildPlaylist(itemToPlay),
//                        itemToPlay,
//                        playWhenReady,
//                        playbackStartPositionMs
//                    )
//                }
//            }
        }


        /**
         * 透過 Google Assistant 發送的指令會從這個 callback 收到
         */
        override fun onPrepareFromSearch(query: String, playWhenReady: Boolean, extras: Bundle?) =
            Unit

        override fun onPrepareFromUri(uri: Uri, playWhenReady: Boolean, extras: Bundle?) {
            val itemToPlay: MediaMetadataCompat = MediaMetadataCompat.Builder().apply {
                mediaUri = uri.toString()
            }.build()
            val list = mutableListOf<MediaMetadataCompat>()
            list.add(itemToPlay)
            preparePlaylist(
                list,
                itemToPlay,
                playWhenReady,
                0
            )
        }

        override fun onCommand(
            player: Player,
            controlDispatcher: ControlDispatcher,
            command: String,
            extras: Bundle?,
            cb: ResultReceiver?,
        ) = false
    }

    private inner class ActionProvider : MediaSessionConnector.CustomActionProvider {

        override fun onCustomAction(
            player: Player,
            controlDispatcher: ControlDispatcher,
            action: String,
            extras: Bundle?,
        ) {
            // 播放音频列表
            if (CUSTOM_ACTION_MUSIC_PLAY_QUEUE == action) {
                extras?.apply {
                    classLoader = MediaDescriptionCompat::class.java.classLoader
                    val list: List<MediaMetadataCompat> =
                        getParcelableArrayList(KEY_MUSIC_QUEUE) ?: return
                    val index = getInt(KEY_MUSIC_QUEUE_PLAY_INDEX, 0)
                    preparePlaylist(list.toMutableList(), list[index], true, C.TIME_UNSET)
                }
            }
        }

        override fun getCustomAction(player: Player): PlaybackStateCompat.CustomAction? {
            return PlaybackStateCompat.CustomAction.Builder(CUSTOM_ACTION_MUSIC_PLAY_QUEUE,
                "set queue",
                0).build()
        }

    }

    private inner class PlayerNotificationListener :
        PlayerNotificationManager.NotificationListener {

        override fun onNotificationPosted(
            notificationId: Int,
            notification: Notification,
            ongoing: Boolean,
        ) {
            if (ongoing && !isForegroundService) {
                ContextCompat.startForegroundService(
                    applicationContext,
                    Intent(applicationContext, this@AudioService.javaClass)
                )

                startForeground(notificationId, notification)
                isForegroundService = true
            }
        }

        override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
            stopForeground(true)
            isForegroundService = false
            stopSelf()
        }
    }

    private inner class PlayerEventListener : Player.EventListener {

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when (playbackState) {
                Player.STATE_BUFFERING,
                Player.STATE_READY,
                -> {
                    notificationManager.showNotificationForPlayer(exoPlayer)
                    if (playbackState == Player.STATE_READY) {
                        saveRecentSongToStorage()
                        if (!playWhenReady) {
                            // 如果playback被暂停，我们移除前台状态，它允许通知被取消。
                            // 另一种选择是在通知中提供一个“关闭”按钮，它可以停止回放和清除通知。
                            stopForeground(false)
                        }
                    }
                }
                else -> {
                    notificationManager.hideNotification()
                }
            }
        }

        override fun onPlayerError(error: ExoPlaybackException) {
            var message = R.string.generic_error
            when (error.type) {
                //如果MediaSource对象的数据不能被加载，外部播放器就会上发除源类型
                // 通过吐司消息将错误信息打印到UI/中通知用户。
                ExoPlaybackException.TYPE_SOURCE -> {
                    message = R.string.error_media_not_found
                    Timber.e("TYPE_SOURCE: %s", error.sourceException.message)
                }
                // 如果错误发生在渲染组件中，Exoplayer抛出type_remote错误。
                ExoPlaybackException.TYPE_RENDERER -> {
                    Timber.e("TYPE_RENDERER: %s", error.rendererException.message)
                }
                // 如果发生一个意外的RuntimeException，Exoplayer引发type_unexpected错误
                ExoPlaybackException.TYPE_UNEXPECTED -> {
                    Timber.e("TYPE_UNEXPECTED: %s", error.unexpectedException.message)
                }
                // 在有OutOMemory错误时发生。
                ExoPlaybackException.TYPE_OUT_OF_MEMORY -> {
                    Timber.e("TYPE_OUT_OF_MEMORY: %s", error.outOfMemoryError.message)
                }
                // 如果该错误发生在远程组件中，则Exoplayer会发出type_remote错误。
                ExoPlaybackException.TYPE_REMOTE -> {
                    Timber.e("TYPE_REMOTE: %s", error.message)
                }
            }
            Toast.makeText(
                applicationContext,
                message,
                Toast.LENGTH_LONG
            ).show()
        }
    }
}

private const val LEXICON_USER_AGENT = "lexicon.next"

const val MEDIA_ID_EMPTY_ROOT = "__EMPTY_ROOT__"
const val MEDIA_ID_ROOT = "__ROOT__"
const val TAG = "AudioService"

/**
 * action
 */
// 播放音频列表
const val CUSTOM_ACTION_MUSIC_PLAY_QUEUE = "com.netease.awakeing.music.MUSIC_QUEUE_PLAY"

// 更新队列
const val CUSTOM_ACTION_MUSIC_UPDATE_QUEUE = "com.netease.awakeing.music.MUSIC_QUEUE_UPDATE"

// 重置队列
const val CUSTOM_ACTION_MUSIC_QUEUE_RESET = "com.netease.awakeing.music.MUSIC_QUEUE_RESET"

/**
 * key
 */
// 音频队列数据
const val KEY_MUSIC_QUEUE = "com.netease.awakeing.music.KEY_MUSIC_QUEUE"

// 音频队列的title数据
const val KEY_MUSIC_QUEUE_TITLE = "com.netease.awakeing.music.KEY_MUSIC_QUEUE_TITLE"

// 播放index，小于0表示不播
const val KEY_MUSIC_QUEUE_PLAY_INDEX = "com.netease.awakeing.music.KEY_MUSIC_QUEUE_PLAY_INDEX"