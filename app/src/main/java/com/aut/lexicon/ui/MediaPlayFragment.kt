package com.aut.lexicon.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.aut.lexicon.R
import com.aut.lexicon.util.InjectorUtils
import com.aut.lexicon.viewmodel.NowPlayingViewModel
import kotlinx.android.synthetic.main.fragment_media_play.*


class MediaPlayFragment : Fragment() {
    private val viewModel: NowPlayingViewModel by viewModels {
        InjectorUtils.provideAudioServiceViewModel(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_media_play, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.mediaButtonRes.observe(viewLifecycleOwner, {
            ib_toggle.setImageResource(it)
        })
        viewModel.mediaMetadata.observe(viewLifecycleOwner, {
            tv_title.text = it.title
            tv_subtitle.text = it.subtitle
            pb.max = it.duration.toInt()
        })
        viewModel.mediaPosition.observe(viewLifecycleOwner, {
            pb.progress = it.toInt()
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() = MediaPlayFragment()
    }
}