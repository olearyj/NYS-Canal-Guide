package com.ayc.canalguide.ui.filter

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ayc.canalguide.R
import com.ayc.canalguide.data.CanalPreferences
import com.ayc.canalguide.databinding.FragmentFilterBinding
import com.ayc.canalguide.ui.map.MapsViewModel
import com.ayc.canalguide.utils.viewBinding
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * This class will:
 *  Handle data binding
 *  Handle container transition, animate transform filter FAB to this fragment
 *  Display and handle options menu with settings button
 *  Update shared preferences of filter states for caching
 */
@AndroidEntryPoint
class FilterFragment: Fragment(R.layout.fragment_filter) {


    @Inject lateinit var preferences: CanalPreferences

    private val mapsViewModel: MapsViewModel by activityViewModels()

    private val binding by viewBinding(FragmentFilterBinding::bind)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        setContainerTransition()

        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, this@FilterFragment.javaClass.simpleName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, this@FilterFragment.javaClass.simpleName)
        })
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_actionbar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_settings -> findNavController().navigate(R.id.settingsFragment)
            R.id.item_share -> {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, getString(R.string.app_playstore_url))
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(intent, getString(R.string.title_app_share)))
            }
        }
        return super.onOptionsItemSelected(item)
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