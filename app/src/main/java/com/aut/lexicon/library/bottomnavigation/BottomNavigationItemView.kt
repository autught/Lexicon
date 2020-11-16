package com.aut.lexicon.library.bottomnavigation

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.aut.lexicon.R
import com.aut.lexicon.util.DensityUtils

/**
 * BottomNavigationItemView
 */
class BottomNavigationItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private var mBottomNavigationEntity: BottomNavigationEntity? = null
    private var mTextSelectedColor = 0
    private var mTextUnSelectedColor = 0
    private var mItemText: TextView? = null
    private var mItemBadge: TextView? = null

    /**
     * 设置
     */
    fun setBottomNavigationEntity(bottomNavigationEntity: BottomNavigationEntity?) {
        mBottomNavigationEntity = bottomNavigationEntity
        setDefaultState()
    }

    fun setTextColor(textSelectedColor: Int, textUnSelectedColor: Int) {
        mTextSelectedColor = textSelectedColor
        mTextUnSelectedColor = textUnSelectedColor
    }

    fun setTextSize(size: Int) {
        mItemText?.setTextSize(TypedValue.COMPLEX_UNIT_PX, size.toFloat())
    }

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        rendingContent(selected)
    }

    /**
     * 设置为初始状态
     * 默认未选中
     */
    fun setDefaultState() {
        rendingContent(false)
        rendingItemBadge()
    }

    /**
     * 目前刷新只是刷新badge
     */
    fun refresh() {
        rendingItemBadge()
    }

    private fun rendingContent(select: Boolean) {
        val text = mBottomNavigationEntity?.text
        if (TextUtils.isEmpty(text)) {
            mItemText?.compoundDrawablePadding = 0
        } else {
            mItemText?.text = text
            mItemText?.compoundDrawablePadding =
                context.resources.getDimensionPixelOffset(R.dimen.sp3)
            mItemText?.setTextColor(if (select) mTextSelectedColor else mTextUnSelectedColor)
        }
        if (mBottomNavigationEntity != null) {
            val top = ContextCompat.getDrawable(
                context,
                if (select) mBottomNavigationEntity!!.selectedIcon else mBottomNavigationEntity!!.unSelectIcon
            )
            val size = DensityUtils.dp2px(context, 20F)
            top?.setBounds(0, 0, size, size)
            mItemText?.setCompoundDrawables(null, top, null, null)
        }
    }

    private fun rendingItemBadge() {
        if (mBottomNavigationEntity == null) {
            return
        }
        val num = mBottomNavigationEntity?.badgeNum ?: 0
//        if (num > 0) {
//            if (num < 99) {
//                mItemBadge?.text = num.toString()
//            } else {
//                mItemBadge?.text = String.format(Locale.getDefault(), "%d+", 99)
//            }
//            mItemBadge?.visibility = View.VISIBLE
//        } else {
//            mItemBadge?.visibility = View.GONE
//        }
    }

    init {
        mItemText = TextView(context).apply {
            gravity = Gravity.CENTER
            val param = LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            param.gravity = Gravity.CENTER
            this@BottomNavigationItemView.addView(this, param)
        }

        mItemBadge = TextView(context).apply {
            gravity = Gravity.CENTER
            textSize = 9F
            text = "9"
            setTextColor(Color.WHITE)
            setBackgroundResource(R.drawable.bg_red_dot)
            val param = LayoutParams(
                DensityUtils.dp2px(context, 15F),
                DensityUtils.dp2px(context, 15F),
                Gravity.CENTER_HORIZONTAL
            )
            param.topMargin = DensityUtils.dp2px(context, 8F)
            param.marginStart = DensityUtils.dp2px(context, 20F)
            this@BottomNavigationItemView.addView(this, param)
        }
    }
}