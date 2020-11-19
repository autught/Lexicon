package com.aut.lexicon.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.aut.lexicon.R
import com.aut.lexicon.library.rv.AdapterX

class DictionaryDataFragment : Fragment() {
    private lateinit var adapter: AdapterX

    // Inflate the layout for this fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_local_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = AdapterX.newInstance {
            addType<AudioItemData>(R.layout.item_local_data_dictionary, { data, holder, position ->
                holder.findViewById<TextView>(R.id.tv_title)?.text = "最美的期待"
                holder.findViewById<TextView>(R.id.tv_path)?.text = "李宇春"
                holder.findViewById<ImageButton>(R.id.ib_add)?.setOnClickListener {
                    Toast.makeText(context, "添加", Toast.LENGTH_LONG).show()
                }
                holder.setOnItemClickListener {
                    Toast.makeText(context, "播放", Toast.LENGTH_LONG).show()
                }
            })
        }
//        rv.layoutManager = LinearLayoutManager(context)
//        rv.adapter = this.adapter
//
//        adapter.refresh(mutableListOf<AudioItemData>().apply {
//            for (index in 0..50) {
//                add(AudioItemData(1L, "ceshi", 100L, 1000L))
//            }
//        })

    }

    companion object {
        @JvmStatic
        fun newInstance() = DictionaryDataFragment()
    }
}