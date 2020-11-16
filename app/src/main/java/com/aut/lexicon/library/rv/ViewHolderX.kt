package com.aut.lexicon.library.rv

import android.util.SparseArray
import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView

/**
 * @description:
 * @author:  79120
 * @date :   2020/11/10 16:40
 */
class ViewHolderX(private val root: View) : RecyclerView.ViewHolder(root) {

    /**
     * Views indexed with their IDs
     */
    private val views: SparseArray<View?> = SparseArray()

    @Suppress("UNCHECKED_CAST")
    fun <T : View> findViewById(@IdRes viewId: Int): T? {
        val view = views.get(viewId)
        if (view == null) {
            root.findViewById<T>(viewId)?.let {
                views.put(viewId, it)
                return it
            }
        }
        return view as? T
    }

    fun setOnItemClickListener(listener: View.OnClickListener) {
        root.setOnClickListener(listener)
    }

}