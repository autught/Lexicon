package com.aut.lexicon.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.Outline
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.animation.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.LinearLayout.HORIZONTAL
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import com.aut.lexicon.R


/**
 * @description:
 * @author:  79120
 * @date :   2020/11/6 15:14
 */
class SegmentView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), View.OnClickListener {
    private var radius = 0
    private var animateSelect = 0
    private var animateDuration = 500
    private var unselectedBackground = Color.WHITE
    private var selectedBackground = Color.TRANSPARENT
    private lateinit var interpolator: Interpolator
    private lateinit var track: View
    private var unit: Int = 0
    private var currentIndex = -1

    init {
        initAttributeSet(context, attrs)
        initInterpolations()
        clipToOutline = true
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height, radius.toFloat())
            }
        }
    }

    private fun initAttributeSet(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SegmentView)
        unselectedBackground =
            typedArray.getColor(R.styleable.SegmentView_unselectedBackground, Color.BLACK)
        selectedBackground = typedArray.getColor(R.styleable.SegmentView_selectedBackground,
            Color.TRANSPARENT)
        radius = typedArray.getDimensionPixelSize(R.styleable.SegmentView_radius, 0)
        animateSelect = typedArray.getInt(R.styleable.SegmentView_animateCut, 0)
        animateDuration = typedArray.getInt(R.styleable.SegmentView_animateCut, 500)
        typedArray.recycle()
    }

    private fun initInterpolations() {
        val interpolatorList = mutableListOf<Class<out Interpolator>>().apply {
            add(FastOutSlowInInterpolator::class.java)
            add(BounceInterpolator::class.java)
            add(LinearInterpolator::class.java)
            add(DecelerateInterpolator::class.java)
            add(CycleInterpolator::class.java)
            add(AnticipateInterpolator::class.java)
            add(AccelerateDecelerateInterpolator::class.java)
            add(AccelerateInterpolator::class.java)
            add(AnticipateOvershootInterpolator::class.java)
            add(FastOutLinearInInterpolator::class.java)
            add(LinearOutSlowInInterpolator::class.java)
            add(OvershootInterpolator::class.java)
        }

        try {
            interpolator = interpolatorList[animateSelect].newInstance()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }

    fun setSegmentTitles(list: MutableList<String>?) {
        list?.let {
            removeAllViews()
            unit = width / it.size
            val params =
                ViewGroup.LayoutParams(unit, ViewGroup.LayoutParams.MATCH_PARENT)
            track = FrameLayout(context).apply {
                setBackgroundColor(selectedBackground)
                this@SegmentView.addView(this, params)
            }

            val container = LinearLayout(context).apply {
                orientation = HORIZONTAL
                setBackgroundColor(unselectedBackground)
                this@SegmentView.addView(this,
                    ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT))
            }

            for ((index, element) in it.withIndex()) {
                TextView(context).apply {
                    gravity = Gravity.CENTER
                    textSize = 14F
                    setTextColor(Color.WHITE)
                    text = element
                    tag = index
                    container.addView(this, params)
                    setOnClickListener(this@SegmentView)
                }
            }
        }
    }

    public fun setCurrent(position: Int) {
        if (currentIndex == position) return
        currentIndex = position
        ValueAnimator.ofFloat(currentIndex.toFloat(), position.toFloat()).apply {
            addUpdateListener { animation ->
                val toggleOffset = animation.animatedValue as Float
                track.updateLayoutParams<MarginLayoutParams> {
                    marginStart += ((toggleOffset - currentIndex) * unit).toInt()
                }
            }
            interpolator = interpolator
            duration = animateDuration.toLong()
            start()
        }
    }

    override fun onClick(view: View) {
        val position = view.tag as Int
        if (position != currentIndex) {
            setCurrent(position)
        }
    }
}