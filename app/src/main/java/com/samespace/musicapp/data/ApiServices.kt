package com.samespace.musicapp.data

import com.samespace.musicapp.data.model.SongsData
import com.samespace.musicapp.utils.EndPoints
import retrofit2.Response
import retrofit2.http.GET

interface ApiServices {
    @GET(EndPoints.songs)
    suspend fun getSongs() : Response<SongsData>
}