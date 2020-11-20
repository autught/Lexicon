package com.aut.lexicon.bean

import android.net.Uri

/**
 * Utility class used to represent the metadata necessary to display the
 * media item currently being played.
 */
data class NowPlayingMetadata(
    val id: String,
    val albumArtUri: Uri?,
    val title: String?,
    val subtitle: String?,
    val duration: Long,
)