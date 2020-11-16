package com.aut.lexicon.library.loader

import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore

class AudioItemData constructor(
    val id: Long,
    val mimeType: String?,
    val size: Long,
    val duration: Long
) {
    var uri: Uri? = null

    init {
        val contentUri = if (isAudio()) {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        } else {
            MediaStore.Files.getContentUri("external")
        }
        uri = ContentUris.withAppendedId(contentUri, id)
    }

    private fun isAudio() = mimeType?.startsWith("video") ?: false

    companion object {
        fun valueOf(cursor: Cursor): AudioItemData? {
            return AudioItemData(
                cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID)),
                cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE)),
                cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.SIZE)),
                cursor.getLong(cursor.getColumnIndex("duration"))
            )
        }
    }

}