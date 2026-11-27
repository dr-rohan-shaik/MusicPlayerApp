package com.musicapp.core.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Artist(
    val id: String,
    val name: String,
    val artworkUrl: String?,
    val genres: List<String>,
    val followerCount: Long,
    val isVerified: Boolean,
    val biography: String?
) : Parcelable