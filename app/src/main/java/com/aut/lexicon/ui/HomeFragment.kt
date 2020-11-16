package com.aut.lexicon.ui

import android.content.Intent
import android.graphics.Outline
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.fragment.app.Fragment
import com.aut.lexicon.R
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        v_favorite.clipToOutline = true
        v_sheet.clipToOutline = true
        val radius = resources.getDimension(R.dimen.dp15)
        v_favorite.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(
                    0, 0, view.width, view.height + radius.toInt(), radius
                )
                outline.alpha = 0.5F
            }
        }
        v_sheet.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(
                    0, -radius.toInt(), view.width, view.height, radius
                )
                outline.alpha = 0.5F
            }
        }
        tv_local.setOnClickListener {
            startActivity(Intent(context, LocalDataActivity::class.java))
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }

}