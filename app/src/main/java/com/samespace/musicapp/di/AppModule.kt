package com.samespace.musicapp.di

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.samespace.musicapp.data.datasource.SongsListDataSource
import com.samespace.musicapp.data.datasource.SongsListDataSourceImpl
import com.samespace.musicapp.data.repository.SongListDataRepository
import com.samespace.musicapp.data.repository.SongListDataRepositoryImpl
import com.samespace.musicapp.domain.SongListDataUseCase
import com.samespace.musicapp.utils.EndPoints
import com.samespace.musicapp.utils.MusicPlayerUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(EndPoints.BaseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideSongListDataSource(
        retrofit: Retrofit
    ) : SongsListDataSource = SongsListDataSourceImpl(retrofit)

    @Provides
    @Singleton
    fun provideRepository(
        songsListDataSource: SongsListDataSource
    ) : SongListDataRepository = SongListDataRepositoryImpl(songsListDataSource)

    @Provides
    @Singleton
    fun songListUseCase(
        repository: SongListDataRepository
    ) : SongListDataUseCase = SongListDataUseCase(repository)

    @Provides
    @Singleton
    fun provideExoPLayer(@ApplicationContext context: Context): ExoPlayer {
        return ExoPlayer.Builder(context).build()
    }

    @Provides
    @Singleton
    fun provideMyPlayer(player: ExoPlayer): MusicPlayerUtil {
        return MusicPlayerUtil(player)
    }

}