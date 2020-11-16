package com.aut.lexicon.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.aut.lexicon.R
import com.aut.lexicon.adapter.AudioDataCursorAdapter
import com.aut.lexicon.viewmodel.LocalDataViewModel
import kotlinx.android.synthetic.main.fragment_local_data.*


class SingleDataFragment : Fragment() {
    private val localDataViewModel: LocalDataViewModel by activityViewModels()

    //    private lateinit var adapter: AdapterX

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_local_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = AudioDataCursorAdapter(context)
        list.adapter = adapter
        list.emptyView = empty
        list.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
               val item= list.getItemAtPosition(position)
        }
        localDataViewModel.singleData.observe(viewLifecycleOwner, {
            adapter.changeCursor(it)
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() = SingleDataFragment()

    }


}