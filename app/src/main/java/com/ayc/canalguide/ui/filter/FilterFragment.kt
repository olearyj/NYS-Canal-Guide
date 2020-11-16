package com.ayc.canalguide.ui.filter

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ayc.canalguide.R
import com.ayc.canalguide.data.CanalPreferences
import com.ayc.canalguide.databinding.FragmentFilterBinding
import com.ayc.canalguide.ui.map.MapsViewModel
import com.ayc.canalguide.utils.viewBinding
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FilterFragment: Fragment(R.layout.fragment_filter) {


    @Inject lateinit var preferences: CanalPreferences

    private val mapsViewModel: MapsViewModel by activityViewModels()

    private val binding by viewBinding(FragmentFilterBinding::bind)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContainerTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mapsViewModel = mapsViewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    override fun onPause() {
        preferences.updateCachedFilterStates(
            lock = mapsViewModel.lockFilterState.value!!,
            bridge = mapsViewModel.bridgeGateFilterState.value!!,
            launch = mapsViewModel.launchFilterState.value!!,
            marina = mapsViewModel.marinaFilterState.value!!,
            cruise = mapsViewModel.cruiseFilterState.value!!,
            navinfo = mapsViewModel.navInfoFilterState.value!!
        )
        super.onPause()
    }

    private fun setContainerTransition() {
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            fadeMode = MaterialContainerTransform.FADE_MODE_CROSS
            duration = 450
            setPathMotion(MaterialArcMotion())
            startContainerColor = ContextCompat.getColor(requireContext(), R.color.mmi_yellow)
            endContainerColor = Color.WHITE
        }
        sharedElementReturnTransition = MaterialContainerTransform().apply {
            fadeMode = MaterialContainerTransform.FADE_MODE_CROSS
            duration = 450
            setPathMotion(MaterialArcMotion())
            startContainerColor = Color.WHITE
            endContainerColor = ContextCompat.getColor(requireContext(), R.color.mmi_yellow)
        }
    }

}