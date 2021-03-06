package com.aut.lexicon.adapter

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.net.toUri
import androidx.cursoradapter.widget.CursorAdapter
import com.aut.lexicon.R

/**
 * @description:
 * @author:  79120
 * @date :   2020/11/13 17:33
 */
class AudioDataCursorAdapter(
    context: Context?,
) : CursorAdapter(context, null, true) {
    private val inflater = LayoutInflater.from(context)

    override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
        val tvTitle = view?.findViewById<TextView>(R.id.tv_title)
        val tvSubTitle = view?.findViewById<TextView>(R.id.tv_singer)
        cursor?.let {
            var title = it.getString(it.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME))
            var artist: String? = null
            if (title.contains("-")) {
                val str: List<String> = title.split("-")
                title = str[1]
                artist = str[0]
            }
            tvTitle?.text = title
            tvSubTitle?.text = artist ?: "未知"
        }

    }

    override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?): View {
        return inflater.inflate(R.layout.item_local_data_single, parent, false)
    }

}