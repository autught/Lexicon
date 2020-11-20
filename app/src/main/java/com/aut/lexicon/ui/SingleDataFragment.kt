package com.aut.lexicon.ui

import android.content.ContentUris
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.aut.lexicon.R
import com.aut.lexicon.adapter.AudioDataCursorAdapter
import com.aut.lexicon.library.audio.artist
import com.aut.lexicon.library.audio.id
import com.aut.lexicon.library.audio.mediaUri
import com.aut.lexicon.library.audio.title
import com.aut.lexicon.viewmodel.LocalDataViewModel
import kotlinx.android.synthetic.main.fragment_local_data.*


class SingleDataFragment : Fragment() {
    private val localDataViewModel: LocalDataViewModel by activityViewModels()

    //    private lateinit var adapter: AdapterX

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_local_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = AudioDataCursorAdapter(context)
        list.adapter = adapter
        list.emptyView = empty
        list.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val item = list.getItemAtPosition(position) as Cursor
            val fileId = item.getLong(item.getColumnIndex(MediaStore.Files.FileColumns._ID))
            val uri =
                ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, fileId)
            val metadata: MediaMetadataCompat = MediaMetadataCompat.Builder().apply {
                id = fileId.toString()
                mediaUri = uri.toString()
                val display =
                    item.getString(item.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME))
//                artist=item.getString(item.getColumnIndex(MediaStore.Files.FileColumns.ARTIST))
                if (display.contains("-")) {
                    val str: List<String> = display.split("-")
                    title = str[1]
                    artist = str[0]
                } else {
                    title = display
                    artist = "未知"
                }
            }.build()
            val bundle = Bundle().apply {
                putParcelable("data", metadata)
            }
            localDataViewModel.playMedia(uri, bundle)
        }
        localDataViewModel.singleData.observe(viewLifecycleOwner, {
            adapter.changeCursor(it)
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() = SingleDataFragment()

    }


}