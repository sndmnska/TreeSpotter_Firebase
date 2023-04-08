package com.smeiskaudio.treespotter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.GeoPoint
import java.util.*

private const val TAG = "TREE_MAP_FRAGMENT"

class TreeMapFragment : Fragment() {

    private lateinit var addTreeButton: FloatingActionButton

    // track to see if granted permission
    private var locationPermissionGranted = false

    private var movedMapToUserLocation = false

    // get user's location  -- this needed a special gradle import
    private var fusedLocationProvider: FusedLocationProviderClient? = null

    // the map
    private var map: GoogleMap? = null

    private val treeMarkers = mutableListOf<Marker>()

    private var treeList = listOf<Tree>()

    private val treeViewModel: TreeViewModel by lazy {
        ViewModelProvider(requireActivity()).get(TreeViewModel::class.java)
    }

    // callback function
    private val mapReadyCallback = OnMapReadyCallback {googleMap ->

        // map is decided if ready in onCreateView
        Log.d(TAG, "Google Map Ready")
        map = googleMap

        googleMap.setOnInfoWindowClickListener { marker ->
            val treeForMarker = marker.tag as Tree
            requestDeleteTree(treeForMarker)

        }
        updateMap()
    }

    private fun requestDeleteTree(tree: Tree) {
        AlertDialog.Builder(requireActivity())
            .setTitle(getString(R.string.delete))
            .setMessage(getString(R.string.confirm_delete_tree, tree.name))
            .setPositiveButton(android.R.string.ok) { dialog, id ->
                treeViewModel.deleteTree(tree) // delete tree directly from firebase
                showSnackbar(getString(R.string.tree_deleted, tree.name))
            }
            .setNegativeButton(android.R.string.cancel) { dialog, id ->
                // do nothing
            }
            .create()
            .show()
    }

    private fun updateMap() {
        // todo draw markers
        drawTrees()

        // draw blue dot as user's location
        if (locationPermissionGranted && !movedMapToUserLocation) {
            moveMapToUserLocation()
        }

     // todo show no location message if location not granted,
    //      or if device does not have location.

    }

    private fun setAddTreeButtonEnabled(isEnabled: Boolean) {
        // helper function
        addTreeButton.isClickable = isEnabled
        addTreeButton.isEnabled = isEnabled

        if (isEnabled) {
            addTreeButton.backgroundTintList =
                AppCompatResources.getColorStateList(
                    requireActivity(), android.R.color.holo_green_light)
        } else {
            addTreeButton.backgroundTintList =
                AppCompatResources.getColorStateList(
                    requireActivity(), android.R.color.darker_gray)
        }
    }

    private fun showSnackbar(message: String) {
        // helper function
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
    }

    // request location permission
    private fun requestLocationPermission() {
        // has user already granted permission?
        if (ContextCompat.checkSelfPermission(requireActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
            Log.d(TAG, "permission already granted")
            updateMap()
            setAddTreeButtonEnabled(true)
            fusedLocationProvider = LocationServices.getFusedLocationProviderClient(requireActivity())
        } else {
            // need to ask permission
            val requestLocationPermissionLauncher =
                registerForActivityResult(ActivityResultContracts.RequestPermission())   { granted ->
                    // launch ANY permission. Here's what you do with the granted result
                    if (granted) {
                        Log.d(TAG, "User Granted Permission")
                        setAddTreeButtonEnabled(true) // user can now add trees
                        locationPermissionGranted = true
                        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(requireActivity())
                    } else {
                        Log.d(TAG, "user did not grant permission")
                        setAddTreeButtonEnabled(false)
                        locationPermissionGranted = false
                        showSnackbar(getString(R.string.give_permission))
                    }

                }

            requestLocationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    @SuppressLint("MissingPermission")
    private fun moveMapToUserLocation()  {
        // wait for map to be ready
        // wait for user's location to be enabled

        if (map == null) {
            return // no point carrying on.
        }
        if (locationPermissionGranted) {
            // try to move the map
            map?.isMyLocationEnabled = true // suppressed due to already established code.
            map?.uiSettings?.isMyLocationButtonEnabled = true
            map?.uiSettings?.isZoomControlsEnabled = true
            map?.uiSettings?.isZoomGesturesEnabled = true

            fusedLocationProvider?.lastLocation?.addOnCompleteListener {getLocationTask ->
                val location = getLocationTask.result
                if (location != null) {
                    Log.d(TAG, "User's location: $location")
                    val center = LatLng(location.latitude, location.longitude)
                    val zoomLevel = 8f
                    map?.moveCamera(CameraUpdateFactory.newLatLngZoom(center, zoomLevel))
                    movedMapToUserLocation = true // so we don't KEEP moving the map to user's location.
                } else {
                    showSnackbar(getString(R.string.no_location))
                }
            }
        }
    }

    private fun drawTrees() {
        if (map == null) { return }

        for (marker in treeMarkers) {
            marker.remove()
        }

        for (tree in treeList) {
            // make a marker for each tree and add to map
            tree.location?.let {geoPoint ->  // "let" assumes geopoint location is nonNull
                val isFavorite = tree.favorite ?: false
                val iconId = if (isFavorite) R.drawable.filled_heart_small else R.drawable.tree_small


                val markerOptions = MarkerOptions()
                    .position(LatLng(geoPoint.latitude, geoPoint.longitude))
                    .title(tree.name)
                    .snippet("Spotted on ${tree.dateSpotted}")
                    .icon(BitmapDescriptorFactory.fromResource(iconId))

                map?.addMarker(markerOptions)?.also { marker ->
                    treeMarkers.add(marker)
                    marker.tag = tree // used to save information about what this marker represents
                }
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainView =  inflater.inflate(R.layout.fragment_tree_map, container, false)

        addTreeButton = mainView.findViewById(R.id.add_tree)
        addTreeButton.setOnClickListener {
            // add tree at user's location - if location permission granted and location available.
            addTreeAtLocation()
        }
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment?
        mapFragment?.getMapAsync(mapReadyCallback)

        // disable add tree button until location is known.
        setAddTreeButtonEnabled(false)

        // request user's permission to access device location.
        requestLocationPermission()

        // todo draw existing trees on the map.
        treeViewModel.latestTrees.observe(requireActivity()) {
            latestTrees ->
            treeList = latestTrees
            drawTrees()
        }

        return mainView
    }

    @SuppressLint("MissingPermission")
    private fun addTreeAtLocation() {
        if (map == null) { return }
        if (fusedLocationProvider == null) { return }
        if (!locationPermissionGranted) {
            showSnackbar(getString(R.string.grant_location_permission_to_add))
            return
        }

        // all tests pass
        fusedLocationProvider?.lastLocation?.addOnCompleteListener(requireActivity()) {
            locationRequestTask ->
            val location = locationRequestTask.result
            if (location != null) {
                val treeName = getTreeName()
                val tree = Tree(
                    name = treeName,
                    dateSpotted = Date(),
                    location = GeoPoint(location.latitude, location.longitude)
                )
                treeViewModel.addTree(tree)
                moveMapToUserLocation()
                showSnackbar(getString(R.string.added_tree, treeName))
            } else {
                showSnackbar(getString(R.string.no_location))
            }
        }


    }

    private fun getTreeName(): String {
        return listOf("Fir","Elm","Birch","Pine").random() // dummy data
    }


    // a fragment can have child fragments (sub-fragments)

    companion object {

        @JvmStatic
        fun newInstance() = TreeMapFragment()
    }
}