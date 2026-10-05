package com.nryu.app.ui

import android.app.Application
import android.content.ComponentName
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.media3.common.MediaItem
import com.google.common.util.concurrent.MoreExecutors
import com.nryu.app.data.Song
import com.nryu.app.data.SongRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MusicViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SongRepository(application)
    private var mediaController: MediaController? = null

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs.asStateFlow()

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    init {
        loadSongs()
        setupMediaController()
    }

    private fun loadSongs() {
        viewModelScope.launch {
            _songs.value = repository.fetchSongs()
        }
    }

    private fun setupMediaController() {
        val sessionToken = SessionToken(
            getApplication(), 
            ComponentName(getApplication(), com.nryu.app.service.MusicService::class.java)
        )
        val controllerFuture = MediaController.Builder(
            getApplication(), 
            sessionToken, 
            { controller ->
                mediaController = controller
                // Listen for playback changes here
            }
        ).build()
    }

    fun playSong(song: Song) {
        _currentSong.value = song
        val mediaItem = MediaItem.Builder()
            .setMediaId(song.id.toString())
            .setUri(song.uri)
            .build()
        
        mediaController?.setMediaItem(mediaItem)
        mediaController?.prepare()
        mediaController?.play()
        _isPlaying.value = true
    }

    fun togglePlayback() {
        if (_isPlaying.value) {
            mediaController?.pause()
        } else {
            mediaController?.play()
        }
        _isPlaying.value = !_isPlaying.value
    }

    fun release() {
        // In a real app, release the controller
    }
}
