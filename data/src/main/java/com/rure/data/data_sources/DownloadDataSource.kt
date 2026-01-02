package com.rure.data.data_sources

import com.rure.data.entities.DownloadedTrack
import com.rure.data.entities.TrackRaw
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DownloadDataSource {
    fun observerTracks(): Flow<List<DownloadedTrack>> = flow {  }

}