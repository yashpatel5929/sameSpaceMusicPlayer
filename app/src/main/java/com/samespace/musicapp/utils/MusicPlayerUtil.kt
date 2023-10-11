package com.samespace.musicapp.utils

import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class MusicPlayerUtil @Inject constructor(
    val player : ExoPlayer
) : Player.Listener {

    val playerState = MutableStateFlow(PlayerStates.STATE_IDLE)
    val isPlaying = MutableStateFlow(false)

    private val _currentPlaybackPosition = MutableStateFlow(0L)
    val currentPlaybackPosition: StateFlow<Long> = _currentPlaybackPosition

    // Define a StateFlow for the current track duration
    private val _currentTrackDuration = MutableStateFlow(0L)
    val currentTrackDuration: StateFlow<Long> = _currentTrackDuration

    private var _selectedIndex = MutableStateFlow(0)
    val selectedIndex : StateFlow<Int> = _selectedIndex


    fun initPlayer(trackList: MutableList<MediaItem>) {
        player.addListener(this)
        player.setMediaItems(trackList)
        player.prepare()
        player.playWhenReady = true
    }

    fun setUpTrack(index: Int, isTrackPlay: Boolean) {
        if (player.playbackState == Player.STATE_IDLE) player.prepare()
        player.seekTo(index, 0)
        if (isTrackPlay) player.playWhenReady = true
    }


    fun playPause() {
        if (player.playbackState == Player.STATE_IDLE) player.prepare()
        player.playWhenReady = !player.playWhenReady
        isPlaying.tryEmit(player.playWhenReady)
    }

    fun releasePlayer() {
        player.release()
    }


    fun seekToPosition(position: Long) {
        player.seekTo(position)
    }


    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        playerState.tryEmit(PlayerStates.STATE_ERROR)
    }

    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        if (player.playbackState == Player.STATE_READY) {
            if (playWhenReady) {
                playerState.tryEmit(PlayerStates.STATE_PLAYING)
            } else {
                playerState.tryEmit(PlayerStates.STATE_PAUSE)
            }
        }
    }


    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)
        if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO) {
            playerState.tryEmit(PlayerStates.STATE_NEXT_TRACK)
            playerState.tryEmit(PlayerStates.STATE_PLAYING)
        }
    }


    override fun onPlaybackStateChanged(playbackState: Int) {
        _currentPlaybackPosition.tryEmit(if (player.currentPosition > 0) player.currentPosition else 0L)
        // Update the current track duration
        _currentTrackDuration.tryEmit( if (player.duration > 0) player.duration else 0L)
        when (playbackState) {
            Player.STATE_IDLE -> {
                playerState.tryEmit(PlayerStates.STATE_IDLE)
            }

            Player.STATE_BUFFERING -> {
                playerState.tryEmit(PlayerStates.STATE_BUFFERING)
            }

            Player.STATE_READY -> {
                playerState.tryEmit(PlayerStates.STATE_READY)
                if (player.playWhenReady) {
                    playerState.tryEmit(PlayerStates.STATE_PLAYING)
                } else {
                    playerState.tryEmit(PlayerStates.STATE_PAUSE)
                }
            }

            Player.STATE_ENDED -> {
                playerState.tryEmit(PlayerStates.STATE_END)
            }
        }
    }

    fun setSelectedIndex(index : Int) {
        _selectedIndex.tryEmit(index)
    }

    fun setCurrentPosition(position : Float) {
        _currentTrackDuration.tryEmit(position.toLong())
    }
}