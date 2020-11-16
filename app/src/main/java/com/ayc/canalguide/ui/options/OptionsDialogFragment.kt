package com.ayc.canalguide.ui.options

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.ayc.canalguide.R
import com.ayc.canalguide.data.CanalPreferences
import com.ayc.canalguide.databinding.FragmentOptionsBinding
import com.ayc.canalguide.ui.map.MapsViewModel
import com.ayc.canalguide.utils.viewBinding
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OptionsDialogFragment: DialogFragment() {


    private val mapsViewModel: MapsViewModel by activityViewModels()

    private val binding by viewBinding(FragmentOptionsBinding::bind)

    @Inject lateinit var preferences: CanalPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContainerTransition()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_options, container, false)
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