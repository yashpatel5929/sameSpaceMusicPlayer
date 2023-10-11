package com.samespace.musicapp.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samespace.musicapp.data.model.SongList
import com.samespace.musicapp.domain.SongListDataUseCase
import com.samespace.musicapp.utils.DataResult
import com.samespace.musicapp.utils.MusicPlayerUtil
import com.samespace.musicapp.utils.PlayerStates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val songListDataUseCase: SongListDataUseCase,
    val musicPlayerUtil: MusicPlayerUtil
) : ViewModel() {

    private val _songList = MutableStateFlow<List<SongList>>(emptyList())
    val songList: StateFlow<List<SongList>> = _songList

    fun getSongList() {
        viewModelScope.launch {
            songListDataUseCase.getSongList().collect { response ->
                when (response) {
                    is DataResult.Success -> {
                        _songList.value = response.data
                    }

                    is DataResult.Error -> {
                        Log.d("TAG", "getSongList: ${response.message} ")
                    }
                }
            }
        }

    }


    fun obervePlayerState() {
        musicPlayerUtil.isPlaying.tryEmit( true)
        viewModelScope.launch {
            musicPlayerUtil.playerState.collect {
                updateState(it)
            }
        }
    }

    private fun updateState(it: PlayerStates) {
        if (musicPlayerUtil.selectedIndex.value != -1 && musicPlayerUtil.selectedIndex.value != songList.value.size) {
            updatePlayBacState(it)
            if (it == PlayerStates.STATE_NEXT_TRACK) {
                musicPlayerUtil.setUpTrack(musicPlayerUtil.selectedIndex.value + 1 , musicPlayerUtil.isPlaying.value)
            }

            if(it == PlayerStates.STATE_END)
                musicPlayerUtil.setUpTrack(0 , musicPlayerUtil.isPlaying.value)

        }
    }

    private fun updatePlayBacState(it : PlayerStates) {
        viewModelScope.launch {
            do {

                delay(1000)
            } while (it == PlayerStates.STATE_PLAYING && isActive)
        }
    }

}