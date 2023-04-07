package com.smeiskaudio.treespotter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TreeRecyclerViewAdapter(var trees: List<Tree>, val treeHeartListener: (Tree, Boolean) -> Unit):
    RecyclerView.Adapter<TreeRecyclerViewAdapter.ViewHolder>() // parenthesis for constuctor
{
    inner class ViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
        fun bind(tree: Tree) { // the part where we do the most work
            view.findViewById<TextView>(R.id.tree_name).text = tree.name
            view.findViewById<TextView>(R.id.date_spotted).text = "${tree.dateSpotted}" // because date object
            view.findViewById<CheckBox>(R.id.heart_check).apply {
                isChecked = tree.favorite ?: false
                setOnCheckedChangeListener { checkbox, isChecked ->
                    treeHeartListener(tree, isChecked)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_tree_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tree = trees[position]
        holder.bind(tree) // see inner class for function
    }

    override fun getItemCount(): Int {
        return trees.size
    }


}