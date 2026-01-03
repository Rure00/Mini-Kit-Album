package com.rure.data.data_sources

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.rure.data.entities.DownloadedTrack
import com.rure.data.entities.TrackRaw
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.time.Duration
import javax.inject.Inject

private const val PATH = "track"
private const val TAG = "DownloadDataSource"

class DownloadDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val ioDispatcher: CoroutineDispatcher,
) {
    private val okHttp: OkHttpClient = OkHttpClient.Builder()
        .callTimeout(Duration.ofMillis(300 * 1000L))
        .readTimeout(Duration.ofMillis(30 * 1000L))
        .writeTimeout(Duration.ofMillis(30 * 1000L))
        .build()

    private val downloadedTrackFlow = MutableStateFlow<List<DownloadedTrack>>(listOf())

    init {
        Log.d("UpdateErrorFind", "DownloadDataSource Init")
        update()
    }

    private fun trackDir(): File {
        val base = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
            ?: throw IllegalStateException("ExternalFilesDir is null")
        return File(base, PATH).apply { mkdirs() }
    }

    private fun update() {
        val dir = trackDir()
        val files = dir.listFiles()
            ?.asSequence()
            ?.filter { it.isFile && it.extension.equals("mp3", ignoreCase = true) }
            ?.toList()
            .orEmpty()

        val scanned = files.map { f ->
            DownloadedTrack(
                id = f.nameWithoutExtension,
                albumId = "",
                uri = Uri.fromFile(f).toString()
            )
        }

        downloadedTrackFlow.tryEmit(scanned)
    }

    fun observerTracks(): Flow<List<DownloadedTrack>> = downloadedTrackFlow.asSharedFlow()

    suspend fun saveMp3(trackRaw: TrackRaw): Result<DownloadedTrack> = withContext(ioDispatcher) {
        runCatching {
            val dir = trackDir()
            val outFile = File(dir, "${trackRaw.id}.mp3")

            val request = Request.Builder()
                .url(trackRaw.uri)
                .get()
                .build()

            okHttp.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IllegalStateException("HTTP ${response.code}")
                val body = response.body ?: throw IllegalStateException("Empty body")

                if (outFile.exists()) outFile.delete()

                outFile.outputStream().use { out ->
                    body.byteStream().use { input ->
                        val buf = ByteArray(DEFAULT_BUFFER_SIZE)
                        while (true) {
                            val read = input.read(buf)
                            if (read < 0) break
                            out.write(buf, 0, read)
                        }
                        out.flush()
                    }
                }
            }

            val saved = DownloadedTrack(
                id = trackRaw.id,
                albumId = trackRaw.albumId,
                uri = Uri.fromFile(outFile).toString()
            )

            update()

            saved
        }.onFailure {
            Log.i(TAG, "saveMp3 Failed: ${it.message}", it)
        }
    }
}
