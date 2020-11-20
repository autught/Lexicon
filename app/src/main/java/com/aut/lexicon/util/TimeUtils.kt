package com.aut.lexicon.util

import android.content.Context
import com.aut.lexicon.R
import kotlin.math.floor

/**
 * @description:
 * @author:  79120
 * @date :   2020/11/20 9:57
 */
object TimeUtils {
    /**
     * Utility method to convert milliseconds to a display of minutes and seconds
     */
    @JvmStatic
    fun timestampToMSS(context: Context, position: Long): String {
        val totalSeconds = floor(position / 1E3).toInt()
        val minutes = totalSeconds / 60
        val remainingSeconds = totalSeconds - (minutes * 60)
        return if (position < 0) context.getString(R.string.duration_unknown)
        else context.getString(R.string.duration_format).format(minutes, remainingSeconds)
    }
}