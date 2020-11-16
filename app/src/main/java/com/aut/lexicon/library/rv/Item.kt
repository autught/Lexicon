package com.aut.lexicon.library.rv

import androidx.recyclerview.widget.RecyclerView

/**
 * @description:
 * @author:  79120
 * @date :   2020/11/10 15:06
 */
data class Item(
    /**
     * Item对应的布局ID
     */
    val resId: Int,
    /**
     * Item绑定数据的回调函数
     */
    val bind: BindFunc<Any, RecyclerView.ViewHolder>?,

    val bindPayloads: BindFuncPayloads<Any, RecyclerView.ViewHolder>? = null,
)