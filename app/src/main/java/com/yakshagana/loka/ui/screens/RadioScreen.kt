package com.yakshagana.loka.ui.screens

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import com.yakshagana.loka.model.AudioClip
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.URL

private const val RawScheme = "raw://"
private const val RemotePlaylistUrl =
    "https://raw.githubusercontent.com/SurajKulal1/yakshagana-radio-assets/main/radio/playlist.json"
private const val RepoTreeApiUrl =
    "https://api.github.com/repos/SurajKulal1/yakshagana-radio-assets/git/trees/main?recursive=1"
private const val RepoRawBaseUrl =
    "https://raw.githubusercontent.com/SurajKulal1/yakshagana-radio-assets/main/"

@Composable
fun RadioScreen(clips: List<AudioClip>) {
    val context = LocalContext.current
    val player = remember {
        ExoPlayer.Builder(context).build()
    }

    var query by remember { mutableStateOf("") }
    var nowPlaying by remember { mutableStateOf<AudioClip?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var remoteClips by remember { mutableStateOf<List<AudioClip>>(emptyList()) }
    var statusText by remember { mutableStateOf<String?>(null) }
    var currentQueue by remember { mutableStateOf<List<AudioClip>>(emptyList()) }
    var currentIndex by remember { mutableStateOf(-1) }
    var showNowPlaying by remember { mutableStateOf(false) }
    var playbackPositionMs by remember { mutableStateOf(0L) }
    var durationMs by remember { mutableStateOf(0L) }

    LaunchedEffect(Unit) {
        if (!isOnline(context)) {
            statusText = "You are offline"
            return@LaunchedEffect
        }

        val fetched = fetchRemotePlaylist()
        if (fetched.isNotEmpty()) {
            remoteClips = fetched
            statusText = "Online playlist loaded from GitHub"
        } else {
            val fallbackClips = fetchGitHubSongsFromRepo()
            if (fallbackClips.isEmpty()) {
                statusText = "GitHub playlist unavailable."
            } else {
                remoteClips = fallbackClips
                statusText = "Loaded songs directly from GitHub repo"
            }
        }
    }

    DisposableEffect(Unit) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlayingNow: Boolean) {
                isPlaying = isPlayingNow
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    if (currentIndex in currentQueue.indices && currentIndex < currentQueue.lastIndex) {
                        val nextIndex = currentIndex + 1
                        val nextClip = currentQueue[nextIndex]
                        val uri = resolveClipUri(context, nextClip.url) ?: return
                        currentIndex = nextIndex
                        nowPlaying = nextClip
                        player.setMediaItem(MediaItem.Builder().setUri(uri).setMediaId(nextClip.id).build())
                        player.prepare()
                        player.play()
                    } else {
                        player.stop()
                    }
                }
            }
        }
        player.addListener(listener)
        onDispose {
            player.removeListener(listener)
            player.release()
        }
    }

    LaunchedEffect(player, nowPlaying) {
        while (true) {
            playbackPositionMs = player.currentPosition.coerceAtLeast(0L)
            durationMs = if (player.duration > 0) player.duration else 0L
            delay(500)
        }
    }

    val filtered = remember(clips, remoteClips, query) {
        val activeClips = if (remoteClips.isNotEmpty()) remoteClips else clips
        if (query.isBlank()) activeClips
        else activeClips.filter {
            it.title.contains(query, ignoreCase = true) ||
                it.artistName.contains(query, ignoreCase = true)
        }
    }

    fun playFromQueue(index: Int) {
        if (index !in currentQueue.indices) return
        val clip = currentQueue[index]
        val uri = resolveClipUri(context, clip.url)
        if (uri == null) {
            Toast.makeText(
                context,
                "Cannot play this track right now.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        currentIndex = index
        nowPlaying = clip
        player.setMediaItem(MediaItem.Builder().setUri(uri).setMediaId(clip.id).build())
        player.prepare()
        player.play()
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(modifier = Modifier.height(12.dp)) }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Now Streaming",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Your playlist",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text("Search songs or artist") }
                    )
                    statusText?.let { msg ->
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = msg,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }

            items(filtered) { clip ->
                RadioTrackRow(
                    clip = clip,
                    isActive = nowPlaying?.id == clip.id,
                    isPlaying = isPlaying && nowPlaying?.id == clip.id,
                    onPlayPause = {
                        if (nowPlaying?.id == clip.id && player.isPlaying) {
                            player.pause()
                        } else if (nowPlaying?.id == clip.id) {
                            player.play()
                        } else {
                            if (isYoutubeUrl(clip.url)) {
                                Toast.makeText(
                                    context,
                                    "Unsupported URL: only direct GitHub audio links are allowed.",
                                    Toast.LENGTH_LONG
                                ).show()
                                return@RadioTrackRow
                            }
                            currentQueue = filtered
                            val indexInQueue = currentQueue.indexOfFirst { it.id == clip.id }
                            if (indexInQueue == -1) return@RadioTrackRow
                            playFromQueue(indexInQueue)
                        }
                    },
                    onDownload = {
                        if (clip.url.startsWith(RawScheme)) {
                            Toast.makeText(
                                context,
                                "This is a local song inside the app. Export-to-Downloads can be added next.",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            downloadClip(context, clip)
                        }
                    },
                    onSetRingtone = {
                        // Real ringtone setting requires a downloaded local file + write settings permission.
                        // We provide a reliable flow: open system settings.
                        openRingtoneSettings(context)
                        Toast.makeText(
                            context,
                            "Download the track, then set it as ringtone from system settings.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(110.dp)) }
        }

        if (nowPlaying != null) {
            MiniPlayerBar(
                title = nowPlaying!!.title,
                subtitle = nowPlaying!!.artistName,
                isPlaying = isPlaying,
                positionMs = playbackPositionMs,
                durationMs = durationMs,
                canGoPrevious = currentIndex > 0,
                canGoNext = currentIndex in currentQueue.indices && currentIndex < currentQueue.lastIndex,
                onPrevious = { playFromQueue(currentIndex - 1) },
                onPlayPause = {
                    if (player.isPlaying) player.pause() else player.play()
                },
                onSeekBack = {
                    player.seekTo((player.currentPosition - 10_000L).coerceAtLeast(0L))
                },
                onSeekForward = {
                    val maxSeek = if (player.duration > 0) player.duration else player.currentPosition + 10_000L
                    player.seekTo((player.currentPosition + 10_000L).coerceAtMost(maxSeek))
                },
                onNext = { playFromQueue(currentIndex + 1) },
                onStop = {
                    player.stop()
                    isPlaying = false
                },
                onSeekTo = { seekPosition -> player.seekTo(seekPosition.toLong()) },
                onOpenNowPlaying = { showNowPlaying = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(androidx.compose.ui.Alignment.BottomCenter)
            )
        }

        if (showNowPlaying && nowPlaying != null) {
            NowPlayingSheet(
                clip = nowPlaying!!,
                isPlaying = isPlaying,
                positionMs = playbackPositionMs,
                durationMs = durationMs,
                canGoPrevious = currentIndex > 0,
                canGoNext = currentIndex in currentQueue.indices && currentIndex < currentQueue.lastIndex,
                onDismiss = { showNowPlaying = false },
                onPrevious = { playFromQueue(currentIndex - 1) },
                onPlayPause = { if (player.isPlaying) player.pause() else player.play() },
                onSeekBack = {
                    player.seekTo((player.currentPosition - 10_000L).coerceAtLeast(0L))
                },
                onSeekForward = {
                    val maxSeek = if (player.duration > 0) player.duration else player.currentPosition + 10_000L
                    player.seekTo((player.currentPosition + 10_000L).coerceAtMost(maxSeek))
                },
                onNext = { playFromQueue(currentIndex + 1) },
                onStop = {
                    player.stop()
                    isPlaying = false
                },
                onSeekTo = { seekPosition -> player.seekTo(seekPosition.toLong()) }
            )
        }
    }
}

@Composable
private fun RadioTrackRow(
    clip: AudioClip,
    isActive: Boolean,
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onDownload: () -> Unit,
    onSetRingtone: () -> Unit
) {
    var menuOpen by remember { mutableStateOf(false) }
    val melaGreen = Color(0xFF16A34A)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) melaGreen.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(10.dp)
                )
            }
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(14.dp),
                color = if (isActive) melaGreen.copy(alpha = 0.16f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
            ) {
                IconButton(onClick = onPlayPause) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = if (isActive) melaGreen else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = clip.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = clip.artistName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box {
                IconButton(onClick = { menuOpen = true }) {
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More")
                }
                DropdownMenu(
                    expanded = menuOpen,
                    onDismissRequest = { menuOpen = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Download") },
                        leadingIcon = { Icon(Icons.Default.Download, contentDescription = null) },
                        onClick = {
                            menuOpen = false
                            onDownload()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Set as ringtone") },
                        leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null) },
                        onClick = {
                            menuOpen = false
                            onSetRingtone()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MiniPlayerBar(
    title: String,
    subtitle: String,
    isPlaying: Boolean,
    positionMs: Long,
    durationMs: Long,
    canGoPrevious: Boolean,
    canGoNext: Boolean,
    onPrevious: () -> Unit,
    onPlayPause: () -> Unit,
    onSeekBack: () -> Unit,
    onSeekForward: () -> Unit,
    onNext: () -> Unit,
    onStop: () -> Unit,
    onSeekTo: (Float) -> Unit,
    onOpenNowPlaying: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .clickable { onOpenNowPlaying() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
            Slider(
                value = if (durationMs > 0L) playbackProgress(positionMs, durationMs) else 0f,
                onValueChange = { progress -> onSeekTo(progress * durationMs) },
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(formatDuration(positionMs), style = MaterialTheme.typography.labelSmall)
                Text(formatDuration(durationMs), style = MaterialTheme.typography.labelSmall)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onPrevious, enabled = canGoPrevious) {
                    Icon(imageVector = Icons.Default.SkipPrevious, contentDescription = "Previous")
                }
                IconButton(onClick = onSeekBack) {
                    Icon(imageVector = Icons.Default.FastRewind, contentDescription = "Back 10 seconds")
                }
                IconButton(onClick = onPlayPause) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play or pause"
                    )
                }
                IconButton(onClick = onSeekForward) {
                    Icon(imageVector = Icons.Default.FastForward, contentDescription = "Forward 10 seconds")
                }
                IconButton(onClick = onNext, enabled = canGoNext) {
                    Icon(imageVector = Icons.Default.SkipNext, contentDescription = "Next")
                }
                IconButton(onClick = onStop) {
                    Icon(imageVector = Icons.Default.Stop, contentDescription = "Stop")
                }
            }
        }
    }
}

@Composable
private fun NowPlayingSheet(
    clip: AudioClip,
    isPlaying: Boolean,
    positionMs: Long,
    durationMs: Long,
    canGoPrevious: Boolean,
    canGoNext: Boolean,
    onDismiss: () -> Unit,
    onPrevious: () -> Unit,
    onPlayPause: () -> Unit,
    onSeekBack: () -> Unit,
    onSeekForward: () -> Unit,
    onNext: () -> Unit,
    onStop: () -> Unit,
    onSeekTo: (Float) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.98f))
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Now Playing",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onDismiss() }
            )
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            ) {
                Box(contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(96.dp)
                    )
                }
            }
            Text(text = clip.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(text = clip.artistName, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Slider(
                value = if (durationMs > 0L) playbackProgress(positionMs, durationMs) else 0f,
                onValueChange = { progress -> onSeekTo(progress * durationMs) },
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(formatDuration(positionMs), style = MaterialTheme.typography.bodySmall)
                Text(formatDuration(durationMs), style = MaterialTheme.typography.bodySmall)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(onClick = onPrevious, enabled = canGoPrevious) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = "Previous", modifier = Modifier.size(32.dp))
                }
                IconButton(onClick = onSeekBack) {
                    Icon(Icons.Default.FastRewind, contentDescription = "Back 10 seconds", modifier = Modifier.size(32.dp))
                }
                IconButton(onClick = onPlayPause) {
                    Icon(
                        if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        modifier = Modifier.size(42.dp)
                    )
                }
                IconButton(onClick = onSeekForward) {
                    Icon(Icons.Default.FastForward, contentDescription = "Forward 10 seconds", modifier = Modifier.size(32.dp))
                }
                IconButton(onClick = onNext, enabled = canGoNext) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Next", modifier = Modifier.size(32.dp))
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(onClick = onStop) {
                    Icon(Icons.Default.Stop, contentDescription = "Stop")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Close",
                    modifier = Modifier
                        .align(androidx.compose.ui.Alignment.CenterVertically)
                        .clickable { onDismiss() },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

private fun downloadClip(context: Context, clip: AudioClip) {
    val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val request = DownloadManager.Request(Uri.parse(clip.url))
        .setTitle(clip.title)
        .setDescription(clip.artistName)
        .setAllowedOverMetered(true)
        .setAllowedOverRoaming(true)
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setDestinationInExternalPublicDir(
            android.os.Environment.DIRECTORY_DOWNLOADS,
            "${sanitizeFileName(clip.title)}.mp3"
        )

    try {
        manager.enqueue(request)
        Toast.makeText(context, "Downloading…", Toast.LENGTH_SHORT).show()
    } catch (_: Exception) {
        Toast.makeText(context, "Download failed on this device.", Toast.LENGTH_LONG).show()
    }
}

private fun openRingtoneSettings(context: Context) {
    try {
        context.startActivity(Intent(Settings.ACTION_SOUND_SETTINGS))
    } catch (_: Exception) {
        Toast.makeText(context, "Unable to open sound settings.", Toast.LENGTH_SHORT).show()
    }
}

private fun sanitizeFileName(input: String): String {
    return input
        .trim()
        .replace(Regex("[\\\\/:*?\"<>|]"), "_")
        .take(80)
        .ifBlank { "yakshagana_track" }
}

private fun resolveClipUri(context: Context, url: String): Uri? {
    if (!url.startsWith(RawScheme)) return Uri.parse(url)
    val rawName = url.removePrefix(RawScheme)
    val resId = context.resources.getIdentifier(rawName, "raw", context.packageName)
    if (resId == 0) return null
    return RawResourceDataSource.buildRawResourceUri(resId)
}

private fun isOnline(context: Context): Boolean {
    val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = manager.activeNetwork ?: return false
    val capabilities = manager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

private suspend fun fetchRemotePlaylist(): List<AudioClip> = withContext(Dispatchers.IO) {
    try {
        val body = URL(RemotePlaylistUrl).readText()
        val array = JSONArray(body)
        buildList {
            for (i in 0 until array.length()) {
                val item = array.optJSONObject(i) ?: continue
                val id = item.optString("id").ifBlank { "online_$i" }
                val title = item.optString("title").ifBlank { "Untitled track" }
                val artist = item.optString("artistName").ifBlank { "Unknown artist" }
                val url = item.optString("url")
                if (url.isNotBlank()) {
                    add(
                        AudioClip(
                            id = id,
                            title = title,
                            artistName = artist,
                            url = url
                        )
                    )
                }
            }
        }
    } catch (_: Exception) {
        emptyList()
    }
}

private suspend fun fetchGitHubSongsFromRepo(): List<AudioClip> = withContext(Dispatchers.IO) {
    try {
        val body = URL(RepoTreeApiUrl).readText()
        val root = org.json.JSONObject(body)
        val tree = root.optJSONArray("tree") ?: return@withContext emptyList()
        buildList {
            var index = 1
            for (i in 0 until tree.length()) {
                val node = tree.optJSONObject(i) ?: continue
                if (node.optString("type") != "blob") continue
                val path = node.optString("path")
                if (!path.startsWith("radio/songs/")) continue
                if (!path.matches(Regex(""".+\.(mp3|m4a|aac|wav)$""", RegexOption.IGNORE_CASE))) continue

                val fileName = path.substringAfterLast('/')
                val title = fileName.substringBeforeLast('.')
                val encodedPath = Uri.encode(path, "/")
                add(
                    AudioClip(
                        id = "github_song_$index",
                        title = title,
                        artistName = "Yakshagana",
                        url = "$RepoRawBaseUrl$encodedPath"
                    )
                )
                index++
            }
        }.sortedBy { it.title.lowercase() }
    } catch (_: Exception) {
        emptyList()
    }
}

private fun isYoutubeUrl(url: String): Boolean {
    return url.contains("youtube.com", ignoreCase = true) || url.contains("youtu.be", ignoreCase = true)
}

private fun playbackProgress(positionMs: Long, durationMs: Long): Float {
    if (durationMs <= 0L) return 0f
    return (positionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
}

private fun formatDuration(durationMs: Long): String {
    if (durationMs <= 0L) return "0:00"
    val totalSeconds = durationMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "$minutes:${seconds.toString().padStart(2, '0')}"
}
