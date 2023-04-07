package com.smeiskaudio.treespotter

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.GeoPoint
import java.util.Date

data class Tree(val name: String? = null,
                val favorite: Boolean? = null,
                val location: GeoPoint? = null,
                val dateSpotted: Date? = null,
    // documentReference doesn't need to be saved as a field in Firebase,
    // during get and set documentReference field will be ignored.
    // it is part of Firebase
                @get:Exclude @set:Exclude var documentReference: DocumentReference? = null,
) // data class for the free toString() method
