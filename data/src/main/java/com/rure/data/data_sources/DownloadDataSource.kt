package com.rure.data.data_sources

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.rure.data.entities.DownloadedTrack
import com.rure.data.entities.TrackRaw
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.Duration
import javax.inject.Inject

private const val PATH = "track/"
private const val TAG = "DownloadDataSource"

class DownloadDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val ioDispatcher: CoroutineDispatcher,
){
    fun observerTracks(): Flow<List<DownloadedTrack>> = flow {  }

    suspend fun saveMp3(trackRaw: TrackRaw): Result<DownloadedTrack> = withContext(ioDispatcher) {
        val resolver = context.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "${trackRaw.id}.mp3")
            put(MediaStore.MediaColumns.RELATIVE_PATH, PATH)
            put(MediaStore.Audio.Media.MIME_TYPE, "audio/mpeg")
            put(MediaStore.Audio.Media.IS_PENDING, 1)
        }

        val uri = resolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values)
            ?: throw IllegalStateException("MediaStore insert failed")

        runCatching {
            val okHttp = OkHttpClient
                .Builder()
                .callTimeout(Duration.ofMillis(300 * 1000L))
                .readTimeout(Duration.ofMillis(30 * 1000L))
                .writeTimeout(Duration.ofMillis(30 * 1000L))
                .build()

            val request = Request.Builder()
                .url(trackRaw.uri)
                .get()
                .build()

            okHttp.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IllegalStateException("HTTP ${response.code}")
                val body = response.body ?: throw IllegalStateException("Empty body")

                resolver.openOutputStream(uri, "w")?.use { out ->
                    body.byteStream().use { input ->
                        val buf = ByteArray(DEFAULT_BUFFER_SIZE)
                        while (true) {
                            val read = input.read(buf)
                            if (read < 0) break
                            out.write(buf, 0, read)
                        }
                        out.flush()
                    }
                } ?: throw IllegalStateException("openOutputStream null")
            }

            ContentValues()
                .apply { put(MediaStore.Audio.Media.IS_PENDING, 0) }
                .also { resolver.update(uri, it, null, null) }

            DownloadedTrack(trackRaw.id, trackRaw.albumId, uri.toString())
        }.onFailure {
            resolver.delete(uri, null, null)
            Log.i(TAG, "saveMp3 Failed: ${it.message}")
        }
    }

    suspend fun removeMp3(uri: String): Result<Boolean> = withContext(ioDispatcher) {
        runCatching {
            val resolver = context.contentResolver
            val result = resolver.delete(Uri.parse(uri), null, null)

            result != 0 && result != -1
        }.onFailure {
            Log.i(TAG, "removeMp3 Failed: ${it.message}")
        }
    }
}