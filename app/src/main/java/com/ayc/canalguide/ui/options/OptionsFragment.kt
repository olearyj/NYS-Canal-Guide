package com.ayc.canalguide.ui.options

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ayc.canalguide.R
import com.ayc.canalguide.ui.map.MapsViewModel

/**
 * A placeholder fragment containing a simple view.
 */
class OptionsFragment : Fragment(R.layout.fragment_options) {


    private val mapsViewModel: MapsViewModel by activityViewModels()

    //private val binding by viewBinding(FragmentOptionsBinding::bind)


    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int): OptionsFragment {
            return OptionsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}