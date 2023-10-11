package com.samespace.musicapp.data.datasource

import com.samespace.musicapp.data.ApiServices
import com.samespace.musicapp.data.model.SongsData
import com.samespace.musicapp.utils.DataResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import javax.inject.Inject

class SongsListDataSourceImpl @Inject constructor(
    retrofit : Retrofit
) : SongsListDataSource {

    private val callApiServices by lazy { retrofit.create(ApiServices::class.java)}
    override suspend fun getSongList(): DataResult<SongsData> {
        return withContext(Dispatchers.IO) {
            try {
                val response = callApiServices.getSongs()
                if (response.isSuccessful) {
                    DataResult.Success(data = response.body()!!)
                } else {
                    DataResult.Error(response.message())
                }
            } catch (exception: Exception) {
                DataResult.Error(exception.message?:"")
            }
        }
    }
}