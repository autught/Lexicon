///*
// * Copyright 2017 Google Inc. All rights reserved.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.aut.lexicon.library.audio
//
//import android.app.Notification
//import android.app.PendingIntent
//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import android.os.ResultReceiver
//import android.support.v4.media.MediaBrowserCompat
//import android.support.v4.media.MediaBrowserCompat.MediaItem
//import android.support.v4.media.MediaDescriptionCompat
//import android.support.v4.media.MediaMetadataCompat
//import android.support.v4.media.session.MediaSessionCompat
//import android.support.v4.media.session.PlaybackStateCompat
//import android.widget.Toast
//import androidx.core.content.ContextCompat
//import androidx.media.MediaBrowserServiceCompat
//import androidx.media.MediaBrowserServiceCompat.BrowserRoot.EXTRA_RECENT
//import com.aut.lexicon.R
//import com.aut.lexicon.app.Constants
//import com.google.android.exoplayer2.*
//import com.google.android.exoplayer2.audio.AudioAttributes
//import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
//import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
//import com.google.android.exoplayer2.ui.PlayerNotificationManager
//import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
//import com.google.android.exoplayer2.util.Util
//import timber.log.Timber
//
///**
// * 这个类是浏览和播放从APP的UI的命令的入口节点。从方法[AudioService.onGetRoot]开始浏览，
// * 并在回调[AudioService.onLoadChildren]中继续。
// *
// * 有关实现MediaBrowserService的更多信息，访问
// * [https://developer.android.com/guide/topics/media-apps/audio-app/building-a-mediabrowserservice.html]
// * (https://developer.android.com/guide/topics/media-apps/audio-app/building-a-mediabrowserservice.html).
// */
//open class AudioService : MediaBrowserServiceCompat() {
//    private lateinit var notificationManager: NotificationManager
//    private lateinit var mediaSession: MediaSessionCompat
//    private lateinit var mediaSessionConnector: MediaSessionConnector
//    private var currentPlaylistItems: List<MediaMetadataCompat> = emptyList()
////todo
////    private lateinit var storage: PersistentStorage
//
//    private val dataSourceFactory: DefaultDataSourceFactory by lazy {
//        DefaultDataSourceFactory(
//            /* context= */ this,
//            Util.getUserAgent(/* context= */ this, UAMP_USER_AGENT), /* listener= */
//            null
//        )
//    }
//
//    private var isForegroundService = false
//
//    private val mAudioAttributes = AudioAttributes.Builder()
//        .setContentType(C.CONTENT_TYPE_MUSIC)
//        .setUsage(C.USAGE_MEDIA)
//        .build()
//
//    private val playerListener = PlayerEventListener()
//
//    /**
//     * Configure ExoPlayer to handle audio focus for us.
//     * See [Player.AudioComponent.setAudioAttributes] for details.
//     */
//    private val exoPlayer: ExoPlayer by lazy {
//        SimpleExoPlayer.Builder(this).build().apply {
//            setAudioAttributes(mAudioAttributes, true)
//            setHandleAudioBecomingNoisy(true)
//            addListener(playerListener)
//        }
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//
//        // Build a PendingIntent that can be used to launch the UI.
//        val sessionActivityPendingIntent =
//            packageManager?.getLaunchIntentForPackage(packageName)?.let { sessionIntent ->
//                PendingIntent.getActivity(this, 0, sessionIntent, 0)
//            }
//
//        // Create a new MediaSession.
//        mediaSession = MediaSessionCompat(this, "AudioService")
//            .apply {
//                setSessionActivity(sessionActivityPendingIntent)
//                isActive = true
//            }
//
//        /**
//         *  为了[MediaBrowserCompat.ConnectionCallback.onConnected]被调用,
//         *  一[MediaSessionCompat.Token]需要在[MediaBrowserServiceCompat]上设置
//         *  如果需要一个特定的用例，可以等待设置session token
//         *  但是，token必须在[MediaBrowserServiceCompat.onGetRoot] 返回时设置，
//         *  否则连接将静默失败。(系统甚至不会调用[MediaBrowserCompat.ConnectionCallback.onConnectionFailed])
//         */
//        sessionToken = mediaSession.sessionToken
//
//        /**
//         * 通知管理器将使用我们的播放器和媒体会话来决定何时发布通知。
//         * 当发布或删除通知时，我们的侦听器将被调用，
//         * 这允许我们将服务提升到前台(这是必需的，这样当主Ul 不可见时，我们不会被杀死)
//         */
//        notificationManager = NotificationManager(
//            this,
//            mediaSession.sessionToken,
//            PlayerNotificationListener()
//        )
//        // ExoPlayer will manage the MediaSession for us.
//        mediaSessionConnector = MediaSessionConnector(mediaSession)
//        mediaSessionConnector.setPlaybackPreparer(PlaybackPreparer())
//        mediaSessionConnector.setQueueNavigator(QueueNavigator(mediaSession))
//        mediaSessionConnector.setPlayer(exoPlayer)
//        notificationManager.showNotificationForPlayer(exoPlayer)
//        //todo
////        storage = PersistentStorage.getInstance(applicationContext)
//    }
//
//    /**
//     * This is the code that causes UAMP to stop playing when swiping the activity away from
//     * recents. The choice to do this is app specific. Some apps stop playback, while others allow
//     * playback to continue and allow users to stop it with the notification.
//     */
//    override fun onTaskRemoved(rootIntent: Intent) {
//        saveRecentSongToStorage()
//        super.onTaskRemoved(rootIntent)
//        /**
//         * By stopping playback, the player will transition to [Player.STATE_IDLE] triggering
//         * [Player.EventListener.onPlayerStateChanged] to be called. This will cause the
//         * notification to be hidden and trigger
//         * [PlayerNotificationManager.NotificationListener.onNotificationCancelled] to be called.
//         * The service will then remove itself as a foreground service, and will call
//         * [stopSelf].
//         */
//        exoPlayer.stop(/* reset= */true)
//    }
//
//    override fun onDestroy() {
//        mediaSession.run {
//            isActive = false
//            release()
//        }
//        // Free ExoPlayer resources.
//        exoPlayer.removeListener(playerListener)
//        exoPlayer.release()
//    }
//
//    /**
//     * Returns the "root" media ID that the client should request to get the list of
//     * [MediaItem]s to browse/play.
//     */
//    override fun onGetRoot(
//        clientPackageName: String,
//        clientUid: Int,
//        rootHints: Bundle?
//    ): BrowserRoot? {
//        val rootExtras = Bundle().apply {
//            putBoolean(MEDIA_SEARCH_SUPPORTED, true)
//            putBoolean(CONTENT_STYLE_SUPPORTED, true)
//            putInt(CONTENT_STYLE_BROWSABLE_HINT, CONTENT_STYLE_GRID)
//            putInt(CONTENT_STYLE_PLAYABLE_HINT, CONTENT_STYLE_LIST)
//        }
//
//        return if (clientPackageName == packageName) {
//            /**
//             * By default return the browsable root. Treat the EXTRA_RECENT flag as a special case
//             * and return the recent root instead.
//             */
//            val isRecentRequest = rootHints?.getBoolean(EXTRA_RECENT) ?: false
//            val browserRootPath = if (isRecentRequest) UAMP_RECENT_ROOT else UAMP_BROWSABLE_ROOT
//            BrowserRoot(browserRootPath, rootExtras)
//        } else {
//            /**
//             * Unknown caller. There are two main ways to handle this:
//             * 1) Return a root without any content, which still allows the connecting client
//             * to issue commands.
//             * 2) Return `null`, which will cause the system to disconnect the app.
//             *
//             * UAMP takes the first approach for a variety of reasons, but both are valid
//             * options.
//             */
//            BrowserRoot(UAMP_EMPTY_ROOT, rootExtras)
//        }
//    }
//
//
//    /**
//     * Returns (via the [result] parameter) a list of [MediaItem]s that are child
//     * items of the provided [parentMediaId]. See [BrowseTree] for more details on
//     * how this is build/more details about the relationships.
//     */
//    override fun onLoadChildren(
//        parentMediaId: String,
//        result: Result<List<MediaItem>>
//    ) {
//
//        /**
//         * If the caller requests the recent root, return the most recently played song.
//         */
//        if (parentMediaId == UAMP_RECENT_ROOT) {
////            result.sendResult(storage.loadRecentSong()?.let { song -> listOf(song) })
//        } else {
//            // If the media source is ready, the results will be set synchronously here.
//            val resultsSent = mediaSource.whenReady { successfullyInitialized ->
//                if (successfullyInitialized) {
//                    val children = browseTree[parentMediaId]?.map { item ->
//                        MediaItem(item.description, item.flag)
//                    }
//                    result.sendResult(children)
//                } else {
//                    mediaSession.sendSessionEvent(NETWORK_FAILURE, null)
//                    result.sendResult(null)
//                }
//            }
//
//            // If the results are not ready, the service must "detach" the results before
//            // the method returns. After the source is ready, the lambda above will run,
//            // and the caller will be notified that the results are ready.
//            //
//            // See [MediaItemFragmentViewModel.subscriptionCallback] for how this is passed to the
//            // UI/displayed in the [RecyclerView].
//            if (!resultsSent) {
//                result.detach()
//            }
//        }
//    }
//
//    /**
//     * Returns a list of [MediaItem]s that match the given search query
//     */
//    override fun onSearch(
//        query: String,
//        extras: Bundle?,
//        result: Result<List<MediaItem>>
//    ) {
//
//        val resultsSent = mediaSource.whenReady { successfullyInitialized ->
//            if (successfullyInitialized) {
//                val resultsList = mediaSource.search(query, extras ?: Bundle.EMPTY)
//                    .map { mediaMetadata ->
//                        MediaItem(mediaMetadata.description, mediaMetadata.flag)
//                    }
//                result.sendResult(resultsList)
//            }
//        }
//
//        if (!resultsSent) {
//            result.detach()
//        }
//    }
//
//    /**
//     * Load the supplied list of songs and the song to play into the current player.
//     */
//    private fun preparePlaylist(
//        metadataList: List<MediaMetadataCompat>,
//        itemToPlay: MediaMetadataCompat?,
//        playWhenReady: Boolean,
//        playbackStartPositionMs: Long
//    ) {
//        // Since the playlist was probably based on some ordering (such as tracks
//        // on an album), find which window index to play first so that the song the
//        // user actually wants to hear plays first.
//        val initialWindowIndex = if (itemToPlay == null) 0 else metadataList.indexOf(itemToPlay)
//        currentPlaylistItems = metadataList
//
//        exoPlayer.playWhenReady = playWhenReady
//        exoPlayer.stop(/* reset= */ true)
//        val mediaSource = metadataList.toMediaSource(dataSourceFactory)
//        exoPlayer.prepare(mediaSource)
//        exoPlayer.seekTo(initialWindowIndex, playbackStartPositionMs)
//    }
//
//    private fun saveRecentSongToStorage() {
//
//        // Obtain the current song details *before* saving them on a separate thread, otherwise
//        // the current player may have been unloaded by the time the save routine runs.
//        val description = currentPlaylistItems[exoPlayer.currentWindowIndex].description
//        val position = exoPlayer.currentPosition
//
////        serviceScope.launch {
////            storage.saveRecentSong(
////                description,
////                position
////            )
////        }
//    }
//
//    private inner class QueueNavigator(
//        mediaSession: MediaSessionCompat
//    ) : TimelineQueueNavigator(mediaSession) {
//        override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat =
//            currentPlaylistItems[windowIndex].description
//    }
//
//    private inner class PlaybackPreparer : MediaSessionConnector.PlaybackPreparer {
//
//        /**
//         * UAMP supports preparing (and playing) from search, as well as media ID, so those
//         * capabilities are declared here.
//         *
//         * TODO: Add support for ACTION_PREPARE and ACTION_PLAY, which mean "prepare/play something".
//         */
//        override fun getSupportedPrepareActions(): Long =
//            PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID or
//                    PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or
//                    PlaybackStateCompat.ACTION_PREPARE_FROM_SEARCH or
//                    PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH
//
//        override fun onPrepare(playWhenReady: Boolean) {
////            val recentSong = storage.loadRecentSong() ?: return
////            onPrepareFromMediaId(
////                recentSong.mediaId!!,
////                playWhenReady,
////                recentSong.description.extras
////            )
//        }
//
//        override fun onPrepareFromMediaId(
//            mediaId: String,
//            playWhenReady: Boolean,
//            extras: Bundle?
//        ) {
//            mediaSource.whenReady {
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
//        }
//
//        /**
//         * This method is used by the Google Assistant to respond to requests such as:
//         * - Play Geisha from Wake Up on UAMP
//         * - Play electronic music on UAMP
//         * - Play music on UAMP
//         *
//         * For details on how search is handled, see [AbstractMusicSource.search].
//         */
//        override fun onPrepareFromSearch(query: String, playWhenReady: Boolean, extras: Bundle?) {
//            mediaSource.whenReady {
//                val metadataList = mediaSource.search(query, extras ?: Bundle.EMPTY)
//                if (metadataList.isNotEmpty()) {
//                    preparePlaylist(
//                        metadataList,
//                        metadataList[0],
//                        playWhenReady,
//                        playbackStartPositionMs = C.TIME_UNSET
//                    )
//                }
//            }
//        }
//
//        override fun onPrepareFromUri(uri: Uri, playWhenReady: Boolean, extras: Bundle?) = Unit
//
//        override fun onCommand(
//            player: Player,
//            controlDispatcher: ControlDispatcher,
//            command: String,
//            extras: Bundle?,
//            cb: ResultReceiver?
//        ) = false
//
//        /**
//         * Builds a playlist based on a [MediaMetadataCompat].
//         *
//         * TODO: Support building a playlist by artist, genre, etc...
//         *
//         * @param item Item to base the playlist on.
//         * @return a [List] of [MediaMetadataCompat] objects representing a playlist.
//         */
////        private fun buildPlaylist(item: MediaMetadataCompat): List<MediaMetadataCompat> =
////            mediaSource.filter { it.album == item.album }.sortedBy { it.trackNumber }
//    }
//
//    /**
//     * Listen for notification events.
//     */
//    private inner class PlayerNotificationListener :
//        PlayerNotificationManager.NotificationListener {
//        override fun onNotificationPosted(
//            notificationId: Int,
//            notification: Notification,
//            ongoing: Boolean
//        ) {
//            if (ongoing && !isForegroundService) {
//                ContextCompat.startForegroundService(
//                    applicationContext,
//                    Intent(applicationContext, this@AudioService.javaClass)
//                )
//
//                startForeground(notificationId, notification)
//                isForegroundService = true
//            }
//        }
//
//        override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
//            stopForeground(true)
//            isForegroundService = false
//            stopSelf()
//        }
//    }
//
//    /**
//     * Listen for events from ExoPlayer.
//     */
//    private inner class PlayerEventListener : Player.EventListener {
//        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
//            when (playbackState) {
//                Player.STATE_BUFFERING,
//                Player.STATE_READY -> {
//                    // TODO: 2020/10/24 通知栏更新
////                    notificationManager.showNotificationForPlayer(currentPlayer)
//                    if (playbackState == Player.STATE_READY) {
//                        //当播放/暂停时，保存当前媒体项目持久存储，以便回放可以在设备重启之间恢复。搜索“媒体恢复”以获取更多信息。
//                        saveRecentSongToStorage()
//                        if (!playWhenReady) {
//                            // 如果playback被暂停，我们移除前台状态，它允许通知被取消。
//                            // 另一种选择是在通知中提供一个“关闭”按钮，它可以停止回放和清除通知。
//                            stopForeground(false)
//                        }
//                    }
//                }
//                else -> {
//                    // TODO: 2020/10/24
////                    notificationManager.hideNotification()
//                }
//            }
//        }
//
//        override fun onPlayerError(error: ExoPlaybackException) {
//            var message = R.string.generic_error
//            when (error.type) {
//                //如果MediaSource对象的数据不能被加载，外部播放器就会上发除源类型
//                // 通过吐司消息将错误信息打印到UI/中通知用户。
//                ExoPlaybackException.TYPE_SOURCE -> {
//                    message = R.string.error_media_not_found
//                    Timber.e("TYPE_SOURCE: %s", error.sourceException.message)
//                }
//                // 如果错误发生在渲染组件中，Exoplayer抛出type_remote错误。
//                ExoPlaybackException.TYPE_RENDERER -> {
//                    Timber.e("TYPE_RENDERER: %s", error.rendererException.message)
//                }
//                // 如果发生一个意外的RuntimeException，Exoplayer引发type_unexpected错误
//                ExoPlaybackException.TYPE_UNEXPECTED -> {
//                    Timber.e("TYPE_UNEXPECTED: %s", error.unexpectedException.message)
//                }
//                // 在有OutOMemory错误时发生。
//                ExoPlaybackException.TYPE_OUT_OF_MEMORY -> {
//                    Timber.e("TYPE_OUT_OF_MEMORY: %s", error.outOfMemoryError.message)
//                }
//                // 如果该错误发生在远程组件中，则Exoplayer会发出type_remote错误。
//                ExoPlaybackException.TYPE_REMOTE -> {
//                    Timber.e("TYPE_REMOTE: %s", error.message)
//                }
//            }
//            Toast.makeText(
//                applicationContext,
//                message,
//                Toast.LENGTH_LONG
//            ).show()
//        }
//    }
//}
//
///*
// * (Media) Session events
// */
//const val NETWORK_FAILURE = Constants.APP_ID+".media.session.NETWORK_FAILURE"
//
///** Content styling constants */
//private const val CONTENT_STYLE_BROWSABLE_HINT = "android.media.browse.CONTENT_STYLE_BROWSABLE_HINT"
//private const val CONTENT_STYLE_PLAYABLE_HINT = "android.media.browse.CONTENT_STYLE_PLAYABLE_HINT"
//private const val CONTENT_STYLE_SUPPORTED = "android.media.browse.CONTENT_STYLE_SUPPORTED"
//private const val CONTENT_STYLE_LIST = 1
//private const val CONTENT_STYLE_GRID = 2
//
//private const val UAMP_USER_AGENT = "uamp.next"
//
//val MEDIA_DESCRIPTION_EXTRAS_START_PLAYBACK_POSITION_MS = "playback_start_position_ms"
//
//const val UAMP_BROWSABLE_ROOT = "/"
//const val UAMP_EMPTY_ROOT = "@empty@"
//const val UAMP_RECOMMENDED_ROOT = "__RECOMMENDED__"
//const val UAMP_ALBUMS_ROOT = "__ALBUMS__"
//const val UAMP_RECENT_ROOT = "__RECENT__"
//
//const val MEDIA_SEARCH_SUPPORTED = "android.media.browse.SEARCH_SUPPORTED"