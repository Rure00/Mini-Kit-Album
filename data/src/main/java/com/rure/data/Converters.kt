package com.rure.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.rure.data.entities.AlbumRaw
import com.rure.data.entities.TrackRaw
import com.rure.domain.entities.Album
import com.rure.domain.entities.Track

class Converters {
    @TypeConverter
    fun listToJson(value: List<String>?): String? {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun jsonToList(value: String): List<String>? {
        return Gson().fromJson(value, Array<String>::class.java)?.toList()
    }
}

fun Album.toRaw() = AlbumRaw(
    id = this.id,
    title = this.title,
    artist = this.artist,
    genre = this.genre,
    releaseDate = this.releaseDate,
    description = this.description,
    coverUrl = this.coverUrl,
    tracksId = this.tracks.map{ it.id },
    images = this.images,
    videos = this.videos
)

fun AlbumRaw.toAlbum(tracks: List<Track>) = Album(
    id = this.id,
    title = this.title,
    artist = this.artist,
    genre = this.genre,
    releaseDate = this.releaseDate,
    description = this.description,
    coverUrl = this.coverUrl,
    tracks = tracks,
    images = this.images,
    videos = this.videos
)

fun Track.toRaw() = TrackRaw(
    id = this.id,
    albumId = this.albumId,
    title = this.title,
    uri = this.uri,
    durationSec = this.durationSec,
    downloaded = this.downloaded
)

fun TrackRaw.toTrack() = Track(
    id = this.id,
    albumId = this.albumId,
    title = this.title,
    uri = this.uri,
    durationSec = this.durationSec,
    downloaded = this.downloaded
)