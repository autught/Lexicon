package com.aut.lexicon.library.loader

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.database.MergeCursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.loader.content.CursorLoader
import com.aut.lexicon.bean.AudioData.Companion.ALBUM_ID_ALL


class AudioLoader constructor(
    context: Context,
    selection: String?,
    selectionArgs: Array<out String>?,
) : CursorLoader(
    context,
    MediaStore.Files.getContentUri("external") ?: Uri.EMPTY,
    if (beforeAndroidTen()) arrayOf(
        MediaStore.Files.FileColumns._ID,
        COLUMN_BUCKET_ID,
        COLUMN_BUCKET_DISPLAY_NAME,
        MediaStore.MediaColumns.MIME_TYPE,
        "COUNT(*) AS $COLUMN_COUNT"
    ) else arrayOf(
        MediaStore.Files.FileColumns._ID,
        COLUMN_BUCKET_ID,
        COLUMN_BUCKET_DISPLAY_NAME,
        MediaStore.MediaColumns.MIME_TYPE
    ),
    selection,
    selectionArgs,
    MediaStore.Audio.Media.DEFAULT_SORT_ORDER
) {

    constructor(context: Context) : this(
        context,
        if (beforeAndroidTen()) (MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                + " AND " + MediaStore.MediaColumns.SIZE + ">0"
                + ") GROUP BY (bucket_id")
        else (MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                + " AND " + MediaStore.MediaColumns.SIZE + ">0"),
        arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO.toString()
        )
    )

    override fun loadInBackground(): Cursor? {
        val cursor = super.loadInBackground()
        val allAudios = MatrixCursor(COLUMNS)
        val otherAudios = MatrixCursor(COLUMNS)
        return if (beforeAndroidTen()) {
            loadInBackgroundBefore(cursor, allAudios, otherAudios)
        } else {
            loadInBackgroundAfter(cursor, allAudios, otherAudios)
        }
    }

    override fun onContentChanged() {
        // FIXME a dirty way to fix loading multiple times
    }

    private fun loadInBackgroundBefore(
        cursor: Cursor?,
        allAudios: MatrixCursor,
        otherAudios: MatrixCursor,
    ): Cursor? {
        var totalCount = 0
        var allAudioUri: Uri? = null
        cursor?.let {
            while (it.moveToNext()) {
                val uri = getUri(it)
                val count = it.getInt(it.getColumnIndex(COLUMN_COUNT))
                otherAudios.addRow(
                    arrayOf(
                        it.getLong(it.getColumnIndex(MediaStore.Files.FileColumns._ID)).toString(),
                        it.getLong(it.getColumnIndex(COLUMN_BUCKET_ID)).toString(),
                        it.getString(it.getColumnIndex(COLUMN_BUCKET_DISPLAY_NAME)),
                        it.getString(it.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE)),
                        uri.toString(),
                        count.toString()
                    )
                )
                totalCount += count
            }
            if (it.moveToFirst()) {
                allAudioUri = getUri(it)
            }
        }
        allAudios.addRow(
            arrayOf(
                ALBUM_ID_ALL, ALBUM_ID_ALL, ALBUM_NAME_ALL, null,
                allAudioUri?.toString(),
                totalCount.toString()
            )
        )
        return MergeCursor(arrayOf(allAudios, otherAudios))
    }

    private fun loadInBackgroundAfter(
        cursor: Cursor?,
        allAudios: MatrixCursor,
        otherAudios: MatrixCursor,
    ): Cursor? {
        var totalCount = 0
        var allAlbumCoverUri: Uri? = null
        val countMap: MutableMap<Long, Long> = hashMapOf()
        cursor?.let {
            while (it.moveToNext()) {
                val bucketId: Long =
                    it.getLong(it.getColumnIndex(COLUMN_BUCKET_ID))
                val count = countMap[bucketId]?.inc() ?: 1L
                countMap[bucketId] = count
            }
            if (it.moveToFirst()) {
                allAlbumCoverUri = getUri(it)
                val done: MutableSet<Long> = hashSetOf()
                do {
                    val bucketId: Long =
                        it.getLong(it.getColumnIndex(COLUMN_BUCKET_ID))
                    if (done.contains(bucketId)) {
                        continue
                    }
                    val uri = getUri(it)
                    val count = countMap[bucketId]
                    otherAudios.addRow(
                        arrayOf(
                            it.getLong(it.getColumnIndex(MediaStore.Files.FileColumns._ID))
                                .toString(),
                            bucketId.toString(),
                            it.getString(it.getColumnIndex(COLUMN_BUCKET_DISPLAY_NAME)),
                            it.getString(it.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE)),
                            uri.toString(), count.toString()
                        )
                    )
                    done.add(bucketId)
                    totalCount += count?.toInt() ?: 0
                } while (it.moveToNext())
            }
        }
        allAudios.addRow(
            arrayOf(
                ALBUM_ID_ALL,
                ALBUM_ID_ALL,
                ALBUM_NAME_ALL,
                null,
                allAlbumCoverUri?.toString(),
                totalCount.toString()
            )
        )
        return MergeCursor(arrayOf<Cursor>(allAudios, otherAudios))
    }

    companion object {
        private const val COLUMN_BUCKET_ID = "bucket_id"
        private const val COLUMN_BUCKET_DISPLAY_NAME = "bucket_display_name"
        const val COLUMN_COUNT = "count"
        const val ALBUM_NAME_ALL = "All"

        private val COLUMNS = arrayOf(
            MediaStore.Files.FileColumns._ID,
            COLUMN_BUCKET_ID,
            COLUMN_BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.MIME_TYPE,
            "uri",
            COLUMN_COUNT
        )


        //--------------------------------------------------------------


        /**
         * @return 是否是 Android 10 （Q） 之前的版本
         */
        private fun beforeAndroidTen() =
            Build.VERSION.SDK_INT < Build.VERSION_CODES.Q

        private fun getUri(cursor: Cursor): Uri? {
            val id =
                cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID))
            return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
        }
    }
}