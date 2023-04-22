package com.smeiskaudio.treespotter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts

const val EXTRA_ADD_TREE_NAME = "com.smeiskaudio.treespotter.EXTRA_ADD_TREE_NAME"

private const val TAG = "ADD_TREE_ACTIVITY"

class AddTreeActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var addTreeButton: Button
    private lateinit var treeNameSpinner: Spinner
    private lateinit var customTreeEditText: EditText
    private lateinit var treeNamePreviewTextView: TextView

    private var treeName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_tree)

        addTreeButton = findViewById(R.id.add_tree_button)
        treeNameSpinner = findViewById(R.id.tree_name_spinner)
        customTreeEditText = findViewById(R.id.custom_tree_edit_text)
        treeNamePreviewTextView = findViewById(R.id.tree_name_preview_textview)

        // There is no data coming into this Activity that we need to worry about

        // Define the spinner selection contents
        ArrayAdapter.createFromResource(
            this, R.array.common_tree_list, android.R.layout.simple_spinner_item)
            .also { adapter ->
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter to the spinner
                treeNameSpinner.adapter = adapter
            }

        updateTreePreview()

        addTreeButton.setOnClickListener {
            // if nothing selected and button clicked
            // Treat as if a name was intended to be added - give user an error
            if (treeName == null) {
                Toast.makeText(
                    this,
                    getString(R.string.please_choose_a_name),
                    Toast.LENGTH_SHORT
                ).show()
                updateTreePreview()
            } else { // treeName has a value to send, send result intent.
                val resultIntent = Intent()
                resultIntent.putExtra(EXTRA_ADD_TREE_NAME, treeName)
                setResult(RESULT_OK, resultIntent)
                finish() // closes the activity
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        val selection = parent?.getItemAtPosition(pos)
        Log.d(TAG, "Selection value = $selection")
        if (selection == "*Custom Tree*") {
            updateTreePreview()
        } else {
            treeName = selection.toString()
            updateTreePreview()
        }
    }

    private fun updateTreePreview() {
        if (treeName == null) {
            treeNamePreviewTextView.text = R.string.no_tree_selected.toString()
        } else {
            treeNamePreviewTextView.text = treeName
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        Log.d(TAG, "Nothing selected")
    }

//    companion object AddTreeActivity : AppCompatActivity() {
//        private val addTreeActivityLauncher = registerForActivityResult(
//            ActivityResultContracts
//                .StartActivityForResult()) {result -> handleNewTreeResult(result)}
//
//        fun handleNewTreeResult(result: ActivityResult): String? {
//            if (result.resultCode == RESULT_OK) {
//                val intent = result.data
//                val newTreeName = intent?.getStringExtra(EXTRA_ADD_TREE_NAME)
//                return newTreeName
//            }
//        }
//
//    }
    }

