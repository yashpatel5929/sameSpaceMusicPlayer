package com.samespace.musicapp.utils

import androidx.compose.ui.graphics.Color
import androidx.media3.common.MediaItem
import com.samespace.musicapp.data.model.SongList

fun List<SongList>.toMediaItemList(): MutableList<MediaItem> {
    return this.map { MediaItem.fromUri(it.url!!) }.toMutableList()
}

fun Long.formatTime(): String {
    val totalSeconds = this / 1000
    val minutes = totalSeconds / 60
    val remainingSeconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}

fun String.fromHex() : Color {
    val cleanHex = if (this.startsWith("#")) this.substring(1) else this

    // Convert the hexadecimal string to an integer
    val colorInt = Integer.parseInt(cleanHex, 16)

    // Create a Color object from the integer
    return Color(colorInt)
}

