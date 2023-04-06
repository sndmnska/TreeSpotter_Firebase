package com.smeiskaudio.treespotter

import java.util.Date

data class Tree(val name: String? = null,
                val favorite: Boolean? = null,
                val dateSpotted: Date? = null) // data class for the free toString() method
