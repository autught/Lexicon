package com.aut.lexicon.bean

import android.database.Cursor
import android.net.Uri
import android.os.Parcelable
import androidx.core.net.toUri
import com.aut.lexicon.library.loader.AudioLoader
import kotlinx.android.parcel.Parcelize

@Parcelize
class AudioData constructor(
    private val mId: String,
    val mCoverUri: Uri,
    val mDisplayName: String,
    val mCount: Long,
) : Parcelable {

    constructor(cursor: Cursor) : this(
        cursor.getString(cursor.getColumnIndex("bucket_id")),
        cursor.getString(cursor.getColumnIndex("uri"))?.toUri() ?: Uri.EMPTY,
        cursor.getString(cursor.getColumnIndex("bucket_display_name")),
        cursor.getLong(cursor.getColumnIndex(AudioLoader.COLUMN_COUNT))
    )

    fun isAll() = (ALBUM_ID_ALL == mId)

    companion object {
        const val ALBUM_ID_ALL = "-1"
    }


}