package com.aut.lexicon.library.rv

import androidx.recyclerview.widget.RecyclerView

/**
 * 类型建造者
 */
class Builder {
    /**
     * 类型，布局，视图绑定函数 的集合，以数据类型为 Key
     */
    val itemTypes = hashMapOf<Class<Any>, Item>()

    /**
     * 类型，布局，视图绑定函数 的集合，以数据类型为 Key
     */
    val holderType = hashMapOf<Int, Class<RecyclerView.ViewHolder>>()


//    inline fun <reified D : Any, reified H : RecyclerView.ViewHolder> addType(
//        resId: Int,
//        func: BindViewHolder<D, H>?,
//    ) {
//        itemTypes[D::class.java] = Item(resId, func as BindViewHolder<Any, RecyclerView.ViewHolder>)
//        holderType[resId] = H::class.java as Class<RecyclerView.ViewHolder>
//    }

    /**
     * 添加数据/视图类型函数
     * @param resId 布局ID
     * @param func 视图绑定函数
     * @since 0.3
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <reified D : Any> addType(
        resId: Int,
        func: BindFunc<D, ViewHolderX>?,
        funcPayloads: BindFuncPayloads<D, ViewHolderX>? = null,
    ) {
        itemTypes[D::class.java as Class<Any>] =
            Item(resId,
                func as BindFunc<Any, RecyclerView.ViewHolder>?,
                funcPayloads as BindFuncPayloads<Any, RecyclerView.ViewHolder>?)
        holderType[resId] = ViewHolderX::class.java as Class<RecyclerView.ViewHolder>
    }
}