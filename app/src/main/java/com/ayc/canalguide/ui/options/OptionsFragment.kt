package com.ayc.canalguide.ui.options

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ayc.canalguide.R
import com.ayc.canalguide.databinding.FragmentOptionsBinding
import com.ayc.canalguide.ui.map.MapsViewModel
import com.ayc.canalguide.utils.viewBinding
import com.google.android.gms.maps.GoogleMap


/**
 * A placeholder fragment containing a simple view.
 */
class OptionsFragment : Fragment(R.layout.fragment_options) {


    private val mapsViewModel: MapsViewModel by activityViewModels()

    private val binding by viewBinding(FragmentOptionsBinding::bind)


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.mapsViewModel = mapsViewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

}