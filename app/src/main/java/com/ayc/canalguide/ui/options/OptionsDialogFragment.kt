package com.ayc.canalguide.ui.options

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.ayc.canalguide.R
import com.ayc.canalguide.databinding.FragmentOptionsBinding
import com.ayc.canalguide.ui.map.MapsViewModel
import com.ayc.canalguide.utils.viewBinding
import com.google.android.material.transition.MaterialContainerTransform

class OptionsDialogFragment: DialogFragment() {


    private val mapsViewModel: MapsViewModel by activityViewModels()

    private val binding by viewBinding(FragmentOptionsBinding::bind)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_options, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mapsViewModel = mapsViewModel
        binding.lifecycleOwner = viewLifecycleOwner

    }

}