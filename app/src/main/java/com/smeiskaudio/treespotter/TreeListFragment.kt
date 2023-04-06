package com.smeiskaudio.treespotter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class TreeListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val mainView =  inflater.inflate(R.layout.fragment_tree_list, container, false)
        // set up user interface here
        return mainView
    }

    companion object {
        @JvmStatic
        fun newInstance() = TreeListFragment()
    }
}