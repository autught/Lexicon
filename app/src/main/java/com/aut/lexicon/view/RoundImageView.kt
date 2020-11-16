package com.aut.lexicon.view

import android.content.Context
import android.graphics.Color
import android.graphics.Outline
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import androidx.appcompat.widget.AppCompatImageView
import com.aut.lexicon.R
import kotlin.math.min

/**
 * @description:
 * @author:  79120
 * @date :   2020/11/5 22:34
 */
class RoundImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
    private var mShadowAlpha = 0.25f
    private var mRadius = 0
    private var mShadowColor = Color.BLACK

    init {
        context.obtainStyledAttributes(attrs, R.styleable.RoundImageView).run {
            mShadowColor = getColor(
                R.styleable.RoundImageView_shadowColor,
                Color.BLACK
            )
            mShadowAlpha = getFloat(
                R.styleable.RoundImageView_shadowAlpha,
                0.25F
            )
            mRadius = getDimensionPixelSize(
                R.styleable.RoundImageView_radius,
                0
            )
            recycle()
        }
        setRadiusAndShadow(mRadius, mShadowColor, mShadowAlpha)
    }

    private fun setRadiusAndShadow(radius: Int, shadowColor: Int, shadowAlpha: Float) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            outlineAmbientShadowColor = shadowColor
            outlineSpotShadowColor = shadowColor
        }
        clipToOutline = mRadius > 0
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                val w = view.width
                val h = view.height
                if (w == 0 || h == 0) {
                    return
                }
                val crisis = min(w, h) shr 1
                val temp = if (radius > crisis) {
                    crisis.toFloat()
                } else {
                    radius.toFloat()
                }
                outline.alpha = shadowAlpha
                if (radius <= 0) {
                    outline.setRect(0, 0, w, h)
                } else {
                    outline.setRoundRect(0, 0, w, h, temp)
                }
            }
        }
    }

    fun setRadius(radius: Int) {
        if (mRadius != radius) {
            setRadiusAndShadow(radius, mShadowColor, mShadowAlpha)
        }
    }

    fun setShadowAlpha(shadowAlpha: Float) {
        if (mShadowAlpha == shadowAlpha) {
            return
        }
        mShadowAlpha = shadowAlpha
        invalidateOutline()
    }

    fun setShadowColor(shadowColor: Int) {
        if (mShadowColor == shadowColor) {
            return
        }
        mShadowColor = shadowColor
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            outlineAmbientShadowColor = shadowColor
            outlineSpotShadowColor = shadowColor
            invalidate()
        }
    }


}