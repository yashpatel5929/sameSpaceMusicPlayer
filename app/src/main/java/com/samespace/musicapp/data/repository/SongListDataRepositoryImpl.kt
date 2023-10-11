package com.samespace.musicapp.data.repository

import com.samespace.musicapp.data.datasource.SongsListDataSource
import com.samespace.musicapp.data.model.SongsData
import com.samespace.musicapp.utils.DataResult
import javax.inject.Inject

class SongListDataRepositoryImpl @Inject constructor(
    private val songsListDataSource: SongsListDataSource
) : SongListDataRepository {
    override suspend fun getSongList(): DataResult<SongsData> {
        return songsListDataSource.getSongList()
    }
}