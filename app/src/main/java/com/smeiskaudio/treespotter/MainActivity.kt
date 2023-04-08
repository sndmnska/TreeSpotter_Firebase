package com.smeiskaudio.treespotter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import org.checkerframework.common.subtyping.qual.Bottom
import java.util.*

private const val TAG  = "MAIN_ACTIVITY"

/**
 * Directions for the homework portion of lab:
 *
 *
 * The app shows a single button, which when pressed, adds a tree with a random name at the user’s
 * location.
 *
 * Modify your app so that instead of a random tree, the user can select or enter the new
 * tree’s name.
 *
 * For example, the user could select from a list of choices, or you could allow the user to
 * type in a name.
 *
 * You’ll need to think about what user interface components to use.
 * There are many ways to solve this.
 */

/*  *PROGRAMMER BRAINSTORMING AREA*
*  One way to tackle this is to open up a new Fragment to gather user data, then send this data
* down the correct chain of data handling.
*
* A combo box can help with this, and I do wonder if there might be a way to collect data from
* Firebase in terms of collecting tree names already collected, to ease the need for user typing.
* It would be nice to be able to populate the combo box choices with that.
*
* */
class MainActivity : AppCompatActivity() {

    val CURRENT_FRAGMENT_BUNDLE_KEY = "Current Fragment Bundle Key"
    var currentFragmentTag = "MAP"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        showFragment("MAP") // show map always (not ideal)
        currentFragmentTag = savedInstanceState?.getString(CURRENT_FRAGMENT_BUNDLE_KEY) ?: "MAP"
        showFragment(currentFragmentTag)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) { // itemId set in xml files
                R.id.show_map ->  {
                    showFragment("MAP")
                    true
                }
                R.id.show_list -> {
                    showFragment("LIST")
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    private fun showFragment(tag: String) {
        // if we are not seeing the fragment with the given tag, display it

        currentFragmentTag = tag // what do we want to see on the screen

        if (supportFragmentManager.findFragmentByTag(tag) == null) {
            val transaction = supportFragmentManager.beginTransaction()
            when (tag) {
                "MAP" -> transaction.replace(R.id.fragmentContainerView, TreeMapFragment.newInstance(), "MAP")
                "LIST" -> transaction.replace(R.id.fragmentContainerView, TreeListFragment.newInstance(), "LIST")
            }
            transaction.commit()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(CURRENT_FRAGMENT_BUNDLE_KEY, currentFragmentTag)
    }
}