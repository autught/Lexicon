package com.aut.lexicon.util

import android.content.Context

object DensityUtils {
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param context 上下文
     * @param dpValue dp单位值
     * @return 转换后的px值
     */
    fun dp2px(context: Context, dpValue: Float): Int {
        val scale: Float = context.applicationContext.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     *
     * @param context 上下文
     * @param pxValue px单位值
     * @return 转换后的dp值
     */
    fun px2dp(context: Context, pxValue: Float): Int {
        val scale: Float = context.applicationContext.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * 获取屏幕宽度px
     *
     * @param context 上下文
     * @return 屏幕密度px值
     */
    fun deviceWidthPX(context: Context): Int {
        return context.applicationContext.resources.displayMetrics.widthPixels
    }

    /**
     * 获取屏幕高度px
     *
     * @param context 上下文
     * @return 屏幕高度px
     */
    fun deviceHeightPX(context: Context): Int {
        return context.applicationContext.resources.displayMetrics.heightPixels
    }

    /**
     * sp 转成为 px
     *
     * @param context 上下文
     * @param sp      sp值
     * @return 转换后的px值
     */
    fun sp2px(context: Context, sp: Int): Int {
        // metric 度量
        // density 密度
        val density: Float = context.resources.displayMetrics.scaledDensity
        return (sp * density + 0.5f).toInt()
    }

}