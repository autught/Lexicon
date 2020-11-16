package com.aut.lexicon.library.bottomnavigation

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.aut.lexicon.R

/**
 * 自定义底部导航栏
 */
class BottomNavigationBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), View.OnClickListener {
    private var onNavigationItemSelectedListener: OnNavigationItemSelectedListener? = null

    //这里是-1主要是为了第一次比较
    private var mCurrentPosition = -1

    //选中的color
    private var mTextSelectedColor = 0

    //未选中的color
    private var mTextUnSelectedColor = 0
    private var mTextSize = 0

    fun setOnNavigationItemSelectedListener(onNavigationItemSelectedListener: OnNavigationItemSelectedListener?) {
        this.onNavigationItemSelectedListener = onNavigationItemSelectedListener
    }

    fun setEntities(list: MutableList<BottomNavigationEntity>?) {
        list?.let {
            removeAllViews()
            val params =
                LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1F)
            for ((index,element) in it.withIndex()){
                BottomNavigationItemView(context).apply {
                    setTextSize(mTextSize)
                    setTextColor(mTextSelectedColor, mTextUnSelectedColor)
                    setBottomNavigationEntity(element)
                    tag = index
                    this@BottomNavigationBar.addView(this, params)
                    setOnClickListener(this@BottomNavigationBar)
                    setDefaultState()
                }
            }
        }
    }

    /**
     * 初始化
     */
    init {
        gravity = Gravity.CENTER
        setBackgroundColor(Color.WHITE)
        context.obtainStyledAttributes(attrs, R.styleable.BottomNavigationBar).run {
            mTextSelectedColor = getColor(
                R.styleable.BottomNavigationBar_selectedTextColor,
                Color.parseColor(DEFAULT_SELECTED_COLOR)
            )
            mTextUnSelectedColor = getColor(
                R.styleable.BottomNavigationBar_unselectedTextColor,
                Color.parseColor(DEFAULT_UNSELECTED_COLOR)
            )
            mTextSize = getDimensionPixelSize(
                R.styleable.BottomNavigationBar_textSize,
                context.resources.getDimensionPixelSize(R.dimen.sp12)
            )
            recycle()
        }
    }

    override fun onClick(view: View) {
        val position = view.tag as Int
        if (position != mCurrentPosition) {
            setCurrentPosition(position)
        }
    }

    /**
     * 设置当前选中位置
     *
     * @param position 当前选中的item位置索引
     */
    fun setCurrentPosition(position: Int) {
        if (position > childCount || position == mCurrentPosition) {
            return
        }
        getChildAt(mCurrentPosition)?.apply {
            isSelected = false
        }
        getChildAt(position)?.apply {
            isSelected = true
        }
        mCurrentPosition = position
        onNavigationItemSelectedListener?.onNavigationItemSelected(position)
    }

    /**
     * 刷新的目的是：
     * 1.更新budge
     * 2.后续添加功能
     *
     * @param index 刷新index
     */
    fun refreshItem(index: Int) {
        if (index < 0 || index >= childCount) {
            return
        }
        (getChildAt(index) as BottomNavigationItemView).apply {
            refresh()
        }
    }

    interface OnNavigationItemSelectedListener {
        fun onNavigationItemSelected(position: Int)
    }

    companion object {
        private const val DEFAULT_SELECTED_COLOR = "#000000"
        private const val DEFAULT_UNSELECTED_COLOR = "#999999"
    }
}