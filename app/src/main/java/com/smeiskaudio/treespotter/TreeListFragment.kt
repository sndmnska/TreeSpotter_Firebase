package com.smeiskaudio.treespotter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TreeListFragment : Fragment() {

    private val treeViewModel: TreeViewModel by lazy {
        ViewModelProvider(requireActivity()).get(TreeViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val recyclerView =  inflater.inflate(R.layout.fragment_tree_list, container, false)
        if (recyclerView !is RecyclerView) { throw  java.lang.RuntimeException("TreeListFragment should be a recyclerView") } // !is == "is not"

        val trees = listOf<Tree>() //  some data to populate the list before list arrives fro Firebase. Empty list.
        val adapter = TreeRecyclerViewAdapter(trees) { tree, isFavorite ->
            treeViewModel.setIsFavorite(tree, isFavorite)
        }
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        treeViewModel.latestTrees.observe(requireActivity()) { treeList -> // associate everything with the Activity, the container for both fragments.
            adapter.trees = treeList
            adapter.notifyDataSetChanged()
        }
        // set up user interface here
        return recyclerView
    }

    companion object {
        @JvmStatic
        fun newInstance() = TreeListFragment()
    }
}