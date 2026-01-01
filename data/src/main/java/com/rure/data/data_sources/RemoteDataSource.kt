package com.rure.data.data_sources

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.rure.data.entities.AlbumRaw
import com.rure.data.entities.MyAlbumInRemote
import com.rure.data.entities.TrackRaw
import com.rure.data.toAlbum
import com.rure.data.toTrack
import com.rure.domain.entities.Album
import com.rure.domain.entities.Track
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject

private const val FILE_NAME = "mockAlbumData.json"
private const val ALBUM_TAG = "albums"
private const val TRACK_TAG = "tracks"

private const val ID_FILED = "id"

private const val DEBUGGING_TAG = "RemoteDataSource"

class RemoteDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val localDataSource: LocalDataSource,   // To manage registered albums
    private val ioDispatcher: CoroutineDispatcher
) {
    suspend fun getAlbumByCode(albumCode: String): Album = withContext(ioDispatcher) {
        getAlbum(albumCode).first()
    }

    suspend fun getTrackById(trackId: String): Track = withContext(ioDispatcher) {
        getTrack(trackId).first()
    }

    suspend fun registerAlbum(albumCode: String): Boolean = withContext(ioDispatcher) {
        runCatching {
            val album = getAlbumByCode(albumCode)
            localDataSource.registerAlbum(MyAlbumInRemote(album.id, album.tracks.map { it.id }))
            true
        }.onFailure {
            Log.i(DEBUGGING_TAG, "registerAlbum Failed: ${it.message}")
        }.getOrElse { false }
    }

    private suspend fun getAlbum(vararg id: String): List<Album> = withContext(ioDispatcher) {
        runCatching {
            val json = context.assets.open(FILE_NAME)
                .bufferedReader()
                .use { it.readText() }

            val setOfId = id.toSet()

            val jObject = JSONObject(json)
            val albumJsonArray = jObject.getJSONArray(ALBUM_TAG)

            val albumList = mutableListOf<Album>()
            var found = 0
            val gson = Gson()
            for (idx in 0 until albumJsonArray.length()) {
                val obj = albumJsonArray.getJSONObject(idx)
                val objId = obj.getString(ID_FILED)

                if (setOfId.contains(objId)) {
                    val raw = gson.fromJson(obj.toString(), AlbumRaw::class.java)
                    val tracks = getTrack(*raw.tracksId.toTypedArray())

                    albumList.add(raw.toAlbum(tracks))

                    found++
                }

                if (found >= id.size) break
            }

            albumList
        }.onFailure {
            Log.i(DEBUGGING_TAG, "getAlbum Failed: ${it.message}")
        }.getOrElse { listOf() }
    }

    private suspend fun getTrack(vararg trackIds: String): List<Track> = withContext(ioDispatcher) {
        runCatching {
            val json = context.assets.open(FILE_NAME)
                .bufferedReader()
                .use { it.readText() }

            val setOfId = trackIds.toSet()

            val jObject = JSONObject(json)
            val trackJsonArray = jObject.getJSONArray(TRACK_TAG)

            val trackList = mutableListOf<Track>()
            var found = 0
            for (idx in 0 until trackJsonArray.length()) {
                val obj = trackJsonArray.getJSONObject(idx)
                val objId = obj.getString(ID_FILED)

                if (setOfId.contains(objId)) {
                    val gson = Gson()
                    val raw = gson.fromJson(obj.toString(), TrackRaw::class.java)

                    trackList.add(raw.toTrack())

                    found++
                }

                if (found >= trackIds.size) break
            }

            trackList
        }.onFailure {
            Log.i(DEBUGGING_TAG, "getTrack Failed: ${it.message}")
        }.getOrElse { listOf() }
    }
}