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

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showFragment("MAP")

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
        if (supportFragmentManager.findFragmentByTag(tag) == null) {
            val transaction = supportFragmentManager.beginTransaction()
            when (tag) {
                "MAP" -> transaction.replace(R.id.fragmentContainerView, TreeMapFragment.newInstance(), "MAP")
                "LIST" -> transaction.replace(R.id.fragmentContainerView, TreeListFragment.newInstance(), "LIST")
            }
            transaction.commit()
        }
    }
}