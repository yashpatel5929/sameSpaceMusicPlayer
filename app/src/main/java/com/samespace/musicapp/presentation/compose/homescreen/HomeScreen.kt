package com.samespace.musicapp.presentation.compose.homescreen

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TabRowDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.samespace.musicapp.R
import com.samespace.musicapp.data.model.SongList
import com.samespace.musicapp.presentation.viewmodels.HomeViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

enum class MusicTab(
    @StringRes val tabTitle: Int
) {
    FOR_YOU(
        R.string.for_you
    ),
    TOP_TRACKS(R.string.top_tracks)
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel,
    onSongItemClick: (Int , List<SongList>) -> Unit,
) {
    val pagerState = rememberPagerState()
    val songListState by rememberUpdatedState(flow {
        homeViewModel.getSongList() // Trigger data fetching
        homeViewModel.songList.collect { songList -> emit(songList) }
    }.collectAsState(emptyList()))

    // Access the actual list of songs from the State
    val songList = songListState.value

    HomePagerScreen(modifier = modifier
        .background(Color(0xFF000000)), pagerState ,songList, onSongItemClick = onSongItemClick)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomePagerScreen(
    modifier: Modifier, pagerState: PagerState,
    songList: List<SongList>,
    tabes: Array<MusicTab> = MusicTab.values(),
    onSongItemClick: (Int , List<SongList>) -> Unit
) {

    Column(modifier.fillMaxSize()) {
        val coroutineScope = rememberCoroutineScope()
        HorizontalPager(pageCount = tabes.size ,
            state = pagerState , verticalAlignment = Alignment.Top , modifier = modifier
                .fillMaxSize()
                .padding(start = 10.dp, end = 10.dp)
                .weight(1f)) {
            when (tabes[it]) {
                MusicTab.FOR_YOU -> {
                    ForYouListing(modifier = modifier, songList = songList , onItemClick ={
                        it.id?.let { it1 -> onSongItemClick(it1, songList) }
                    } )
                }
                MusicTab.TOP_TRACKS -> {
                    val filterList = songList.filter {
                        it.topTrack == true
                    }
                   ForYouListing(modifier = modifier, songList = filterList, onItemClick = {
                       it.id?.let { it1 -> onSongItemClick(it1 , songList) }
                   })
                }
                else -> {}
            }
        }
       // Spacer(modifier = Modifier.weight(0.1f))

        TabRow(selectedTabIndex = pagerState.currentPage , indicator ={ tabPosition -> TabRowDefaults.Indicator(
            modifier = Modifier
                .tabIndicatorOffset(tabPosition[pagerState.currentPage])
                .background(
                    Color.Black
                )
                .width(0.dp)

        ) }, modifier = modifier.background(Color.Black) , divider = {}) {
            tabes.forEachIndexed { index, musicTab ->
                val title = stringResource(id = musicTab.tabTitle)
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(index) }
                    },
                    unselectedContentColor = MaterialTheme.colors.secondary,
                    modifier = modifier.background(Color.Transparent)
                ) {
                    BottomNavigationItem(modifier , title = title ,selectedIndex = pagerState.currentPage, currentIndex = index ) {
                        coroutineScope.launch { pagerState.animateScrollToPage(index) }
                    }
                }
            }

        }
    }
    
}

@Composable
fun BottomNavigationItem(
    modifier: Modifier,
    title : String,
    selectedIndex: Int,
    currentIndex: Int,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable {
                onClick()
            }
            .padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(bottom = 10.dp),
            text = title,
            color = if (selectedIndex == currentIndex) Color.White else Color(0xFF999898),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Box(
            modifier = Modifier
                .size(5.dp)
                .background(
                    color = if (selectedIndex == currentIndex) Color.White else Color.Black
                )
                .clip(CircleShape)

        )
    }}
