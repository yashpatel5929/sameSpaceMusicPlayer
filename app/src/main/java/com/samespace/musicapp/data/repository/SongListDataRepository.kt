package com.samespace.musicapp.data.repository

import com.samespace.musicapp.data.model.SongsData
import com.samespace.musicapp.utils.DataResult

interface SongListDataRepository {
    suspend fun getSongList() : DataResult<SongsData>
}