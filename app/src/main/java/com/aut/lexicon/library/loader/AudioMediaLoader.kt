package com.aut.lexicon.library.loader

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.loader.content.CursorLoader


class AudioMediaLoader constructor(
    context: Context,
    selection: String?,
    selectionArgs: Array<out String>?,
) : CursorLoader(
    context,
    MediaStore.Files.getContentUri("external") ?: Uri.EMPTY,
    arrayOf(
        MediaStore.Files.FileColumns._ID,
        MediaStore.MediaColumns.DISPLAY_NAME,
        MediaStore.MediaColumns.MIME_TYPE,
        MediaStore.MediaColumns.SIZE,
        "duration"
    ),
    selection,
    selectionArgs,
    MediaStore.Audio.Media.DEFAULT_SORT_ORDER
) {

    constructor(context: Context, id: String?) : this(context,
        if (id != null) {
            (MediaStore.Files.FileColumns.MEDIA_TYPE
                    + "=?"
                    + " AND "
                    + " bucket_id=?"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0")
        } else {
            (MediaStore.Files.FileColumns.MEDIA_TYPE
                    + "=?"
                    + " AND "
                    + MediaStore.MediaColumns.SIZE + ">0")
        },
        id?.run {
            arrayOf(
                MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO.toString(), this
            )
        } ?: arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO.toString()
        )
    )

}