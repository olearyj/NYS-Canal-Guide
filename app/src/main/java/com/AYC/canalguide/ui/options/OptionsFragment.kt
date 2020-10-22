package com.AYC.canalguide.ui.options

import com.AYC.canalguide.ui.main.PageViewModel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.AYC.canalguide.R
import com.AYC.canalguide.ui.map.MapsViewModel
import com.AYC.canalguide.utils.viewBinding

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