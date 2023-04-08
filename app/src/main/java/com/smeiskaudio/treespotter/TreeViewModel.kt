package com.smeiskaudio.treespotter

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

private const val TAG = "TREE_VIEW_MODEL"

class TreeViewModel: ViewModel() {

    // ViewModel connects to firebase

    private val db = Firebase.firestore
    private val treeCollectionReference = db.collection("trees")

    val latestTrees = MutableLiveData<List<Tree>>() // constructor

    // get all of the tree sightings

    private val latestTreesListener = treeCollectionReference
        .orderBy("dateSpotted", Query.Direction.DESCENDING)
        .limit(10)
        .addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "Error fetching latest trees -- ", error)
            }
            else if (snapshot != null) {
//                val trees = snapshot.toObjects(Tree::class.java) // simple conversion of trees is a List of Tree objects
                val trees = mutableListOf<Tree>()
                for (treeDocument in snapshot) {
                    val tree = treeDocument.toObject(Tree::class.java)
                    tree.documentReference = treeDocument.reference // d
                    trees.add(tree)
                }
                Log.d(TAG, "Trees from firebase: $trees")
                latestTrees.postValue(trees) // postValue because it is happening as a background task
            }
        }
    fun setIsFavorite(tree: Tree, favorite: Boolean) {
        // which tree is being modified and update that tree
        Log.d(TAG, "Updating tree $tree to favorite $favorite")
        tree.documentReference?.update("favorite", favorite)
    }

    fun addTree(tree: Tree) {
        treeCollectionReference.add(tree)
            .addOnSuccessListener { treeDocumentReference ->
                Log.d(TAG, "New tree added at ${treeDocumentReference.path}")
            }
            .addOnFailureListener { error ->
                Log.e(TAG,"Error adding tree $tree", error)
            }
    }

    fun deleteTree(tree: Tree) {
        tree.documentReference?.delete()
    }
}