package com.nryu.app.data

import android.net.Uri

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val uri: Uri,
    val duration: Long,
    val albumArtUri: Uri? = null
)
