package com.samespace.musicapp.presentation.compose.musicplayerscreen

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.rememberAsyncImagePainter
import com.samespace.musicapp.R
import com.samespace.musicapp.data.model.SongList
import com.samespace.musicapp.presentation.viewmodels.HomeViewModel
import com.samespace.musicapp.utils.MusicPlayerUtil
import com.samespace.musicapp.utils.formatTime
import com.samespace.musicapp.utils.toMediaItemList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import com.samespace.musicapp.utils.fromHex


@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun MusicPlayer(
    navController: NavController,
    homeViewModel: HomeViewModel
) {
    val selctedSongId: Int? =
        navController.currentBackStackEntryAsState().value?.arguments?.getInt("Id")

    val musicPlayerUtil = homeViewModel.musicPlayerUtil
    DisposableEffect(musicPlayerUtil) {
        onDispose {
            //musicPlayerUtil.releasePlayer()
        }
    }
    val songList = homeViewModel.songList.value

    LaunchedEffect(key1 = homeViewModel) {
        musicPlayerUtil.initPlayer(songList.toMediaItemList())
        homeViewModel.obervePlayerState()
    }
    if (selctedSongId != null) {
        musicPlayerUtil.setSelectedIndex(selctedSongId - 1)
        HomePlayerView(songList, selctedSongId - 1, musicPlayerUtil)
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(
    ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class
)
@Composable
fun HomePlayerView(
    songList: List<SongList>,
    selectedSongIndex: Int,
    musicPlayerUtil: MusicPlayerUtil
) {
    val pagerState = rememberPagerState(musicPlayerUtil.selectedIndex.collectAsState().value)
    val scope = rememberCoroutineScope()

    musicPlayerUtil.selectedIndex.value.let {
        LaunchedEffect(key1 = it) {
            scope.launch {
                pagerState.animateScrollToPage(it)
            }
        }
    }

    LaunchedEffect(key1 = pagerState.currentPage) {
        if (pagerState.currentPage > musicPlayerUtil.selectedIndex.value) {
            musicPlayerUtil.setSelectedIndex(musicPlayerUtil.selectedIndex.value + 1)
            oNextTabClick(pagerState, musicPlayerUtil, songList)
        } else if (pagerState.currentPage < musicPlayerUtil.selectedIndex.value) {
            musicPlayerUtil.setSelectedIndex(musicPlayerUtil.selectedIndex.value - 1)
            onPreviousTabClick(pagerState, musicPlayerUtil)
        }
    }


    //val color = songList[pagerState.currentPage].accent?.toIntOrNull()?.let { Color(it) }
    val color = songList[pagerState.currentPage].accent?.fromHex()!!

    Scaffold(
        modifier = Modifier.background(color)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    color = Color(0xFF000000)
                )
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center
        ) {
            HorizontalPager(
                pageCount = songList.size,
                state = pagerState
            ) {
                Image(
                    modifier = Modifier
                        .background(
                            color = Color.Transparent,
                            shape = RoundedCornerShape(5.dp)
                        )
                        .fillMaxWidth(0.8f)
                        .height(300.dp)
                        .size(50.dp),
                    painter = rememberAsyncImagePainter(model = "https://cms.samespace.com/assets/${songList[it].cover}"),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 50.dp, start = 20.dp, end = 20.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier,
                    text = songList[pagerState.currentPage].name ?: "",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                Text(
                    modifier = Modifier
                        .padding(top = 5.dp),
                    text = songList[pagerState.currentPage].artist ?: "",
                    color = Color(0xFF999898),
                    fontSize = 16.sp
                )
            }

            TrackProgressSlider(musicPlayerUtil, songList, pagerState, scope)
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
fun onPreviousTabClick(pagerState: PagerState, musicPlayerUtil: MusicPlayerUtil) {
    if (pagerState.currentPage != 0) {
        musicPlayerUtil.setUpTrack(
            musicPlayerUtil.selectedIndex.value,
            musicPlayerUtil.isPlaying.value
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
fun oNextTabClick(
    pagerState: PagerState,
    musicPlayerUtil: MusicPlayerUtil,
    songList: List<SongList>
) {
    if (pagerState.currentPage != songList.size) {
        musicPlayerUtil.setUpTrack(
            musicPlayerUtil.selectedIndex.value,
            musicPlayerUtil.isPlaying.value
        )
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TrackProgressSlider(
    musicPlayerUtil: MusicPlayerUtil,
    songList: List<SongList>,
    pagerState: PagerState,
    scope: CoroutineScope
) {

    var isPlaying = musicPlayerUtil.isPlaying.collectAsState()
    var currentPosition = musicPlayerUtil.currentPlaybackPosition.collectAsState()
    var totalDuration = musicPlayerUtil.currentTrackDuration.collectAsState()
    var currentPosTemp by rememberSaveable { mutableStateOf(0f) }

    Slider(
        value = if (currentPosTemp == 0f) currentPosition.value.toFloat() else currentPosTemp,
        onValueChange = { currentPosTemp = it },
        onValueChangeFinished = {
            musicPlayerUtil.setCurrentPosition(currentPosTemp)
            currentPosTemp = 0f
            scope.launch {
                musicPlayerUtil.seekToPosition(currentPosition.value)
            }

        },
        valueRange = 0f..totalDuration.value.toFloat(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        thumb = {},
        colors = SliderDefaults.colors(
            thumbColor = Color.Transparent,
            activeTrackColor = Color(0xFF999898)
        )
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = currentPosition.value.formatTime(),
            color = Color(0xFF999898),
            fontSize = 12.sp
        )
        Text(
            text = totalDuration.value.formatTime(),
            color = Color(0xFF999898),
            fontSize = 12.sp
        )
    }

    ControlView(
        modifier = Modifier,
        isPlaying = isPlaying,
        musicPlayerUtil = musicPlayerUtil,
        pagerState = pagerState,
        scope = scope,
        songList
    )

}


@OptIn(ExperimentalFoundationApi::class)

@Composable
fun ControlView(
    modifier: Modifier = Modifier,
    isPlaying: State<Boolean>,
    musicPlayerUtil: MusicPlayerUtil,
    pagerState: PagerState,
    scope: CoroutineScope,
    songList: List<SongList>
) {
    val playpause by remember {
        mutableStateOf(isPlaying)
    }
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Image(
            modifier = Modifier
                .size(50.dp)
                .clickable {
                    scope.launch {
                        musicPlayerUtil.setSelectedIndex(musicPlayerUtil.selectedIndex.value - 1)
                        onPreviousTabClick(pagerState, musicPlayerUtil)
                    }

                },
            painter = painterResource(id = R.drawable.baseline_fast_rewind_24),
            contentDescription = null
        )
        Image(
            modifier = Modifier
                .size(50.dp)
                .clickable {
                    musicPlayerUtil.playPause()
                },
            painter = if (isPlaying.value) painterResource(id = R.drawable.baseline_pause_circle_outline_24) else painterResource(
                id = R.drawable.baseline_play_circle_outline_24
            ),
            contentDescription = null
        )
        Image(
            modifier = Modifier
                .size(50.dp)
                .clickable {
                    scope.launch {
                        musicPlayerUtil.setSelectedIndex(musicPlayerUtil.selectedIndex.value + 1)
                        oNextTabClick(pagerState, musicPlayerUtil, songList)
                    }
                },
            painter = painterResource(id = R.drawable.baseline_fast_forward_24),
            contentDescription = null
        )
    }
}


