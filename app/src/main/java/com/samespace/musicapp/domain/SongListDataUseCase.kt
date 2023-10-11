package com.samespace.musicapp.domain

import com.samespace.musicapp.data.model.SongList
import com.samespace.musicapp.data.repository.SongListDataRepository
import com.samespace.musicapp.utils.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

import javax.inject.Inject

class SongListDataUseCase @Inject constructor(
    private val repository: SongListDataRepository
) {

    suspend fun getSongList() : Flow<DataResult<List<SongList>>> = flow {
        when(val response = repository.getSongList()) {
            is DataResult.Success -> {
                emit(DataResult.Success(response.data.data))
            }
            is DataResult.Error -> {
                emit(DataResult.Error("Something went wrong"))
            }
            else -> {}
        }
    }

}