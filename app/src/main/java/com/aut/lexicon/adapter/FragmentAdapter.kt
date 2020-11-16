package com.aut.lexicon.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * @description:
 * @author:  79120
 * @date :   2020/11/10 10:54
 */
class FragmentAdapter(fragmentActivity: FragmentActivity, vararg args: Fragment) :
    FragmentStateAdapter(fragmentActivity) {
    private val fragments = args

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int) = fragments[position]
}