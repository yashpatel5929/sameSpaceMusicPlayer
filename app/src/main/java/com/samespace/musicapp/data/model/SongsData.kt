package com.samespace.musicapp.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

data class SongsData(

    @SerializedName("data")
    val data: List<SongList>
)

@Parcelize
data class SongList(
    val accent: String?,
    val artist: String?,
    val cover: String?,
    @SerializedName("date_created")
    val dateCreated: String?,
    @SerializedName("date_updated")
    val dateUpdated: String?,
    val id: Int?,
    val name: String?,
    val sort: @RawValue Any?,
    val status: String?,
    @SerializedName("top_track")
    val topTrack: Boolean?,
    val url: String?,
    @SerializedName("user_created")
    val userCreated: String?,
    @SerializedName("user_updated")
    val userUpdated: String?
) : Parcelable
