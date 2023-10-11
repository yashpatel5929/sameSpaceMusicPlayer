package com.samespace.musicapp.data.datasource

import com.samespace.musicapp.data.model.SongList
import com.samespace.musicapp.data.model.SongsData
import com.samespace.musicapp.utils.DataResult

interface SongsListDataSource {
    suspend fun getSongList() : DataResult<SongsData>
}