package com.aut.lexicon.library.loader

import android.content.Context
import android.provider.MediaStore
import androidx.loader.content.CursorLoader

/**
 * @description:
 * @author:  79120
 * @date :   2020/11/12 11:27
 */
class LocalAudioLoader(context: Context) : CursorLoader(
    context,
    MediaStore.Files.getContentUri("external"),
    arrayOf(
        MediaStore.Files.FileColumns._ID,
        MediaStore.MediaColumns.DISPLAY_NAME,
        MediaStore.Files.FileColumns.MEDIA_TYPE,
        MediaStore.MediaColumns.SIZE
    ),
    MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " AND " + MediaStore.MediaColumns.SIZE + ">0",
    arrayOf(
        MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO.toString()
    ),
    "datetaken DESC"
)