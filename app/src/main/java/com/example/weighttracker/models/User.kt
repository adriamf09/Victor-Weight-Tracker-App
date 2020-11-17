package com.example.weighttracker.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val name: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    var startWeight: Double? = null,
    var goalWeight: Double? = null
): Parcelable