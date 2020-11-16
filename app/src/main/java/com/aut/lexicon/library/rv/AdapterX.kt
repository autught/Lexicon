package com.aut.lexicon.library.rv

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference

/**
 * @description:
 * @author:  79120
 * @date :   2020/11/10 15:33
 */
class AdapterX(
    data: MutableList<Any>?,
    private val itemTypes: Map<Class<Any>, Item>,
    private val holderTypes: Map<Int, Class<RecyclerView.ViewHolder>>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mData: MutableList<Any> = data ?: mutableListOf()
    private var ref: WeakReference<LayoutInflater>? = null

    companion object {
        fun newInstance(config: Builder.() -> Unit): AdapterX {
            //生成类型建造者
            val builder = Builder()
            //用户添加类型配置
            config(builder)
            return AdapterX(null, builder.itemTypes, builder.holderType)
        }
    }

    /**
     * 通过布局ID产生唯一ItemType
     */
    override fun getItemViewType(position: Int): Int {
        val dataClass = mData[position].javaClass
        val type = itemTypes[dataClass] ?: throw NullPointerException("type can not be empty")
        return type.resId
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = ref?.get() ?: LayoutInflater.from(parent.context)
        val view = inflater.inflate(viewType, parent, false)
        val holderClazz =
            holderTypes[viewType] ?: throw NullPointerException("view holder can not be empty")
        return holderClazz.getConstructor(View::class.java).newInstance(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val d = mData[position] //数据
        val dataClass = d.javaClass //数据类Class
        val type = itemTypes[dataClass] //数据类型对应绑定函数
        type?.bind?.bind(d, holder, position)//绑定操作
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>,
    ) {
        val d = mData[position] //数据
        val dataClass = d.javaClass //数据类Class
        val type = itemTypes[dataClass] //数据类型对应绑定函数
        if (payloads.isNullOrEmpty() || type?.bindPayloads == null) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            type.run { bindPayloads?.bind(d, holder, position, payloads) }
        }
    }

    override fun getItemCount() = mData.size

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        ref = WeakReference(LayoutInflater.from(recyclerView.context))
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        ref = null
    }

    fun refresh(list: Collection<Any>?) {
        list?.takeIf { !it.isNullOrEmpty() }?.apply {
            mData.clear()
            mData.addAll(this)
            notifyDataSetChanged()
        }
    }

    fun append(list: Collection<Any>?) {
        list?.takeIf { !it.isNullOrEmpty() }?.apply {
            mData.addAll(this)
            notifyItemRangeChanged(itemCount, this.size)
        }
    }
}