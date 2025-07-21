/*
 * Copyright (C) 2025 The AxionAOSP Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.systemui.media

import android.graphics.drawable.Drawable
import android.media.session.PlaybackState
import android.media.MediaMetadata

import com.android.systemui.util.WeakListenerManager

class MediaSessionManager private constructor() {

    interface MediaDataListener {
        fun onPlaybackStateChanged(state: Int) {}
        fun onAlbumArtChanged(drawable: Drawable) {}
        fun onMediaColorsChanged(color: Int) {}
        fun onMetadataChanged(track: String, artist: String) {}
    }

    private val listenerManager = WeakListenerManager<MediaDataListener>()
    var isMediaPlaying = false

    private var _trackTitle: String = "Unknown"
    private var _artist: String = "Unknown"

    val trackTitle: String
        get() = _trackTitle

    val artist: String
        get() = _artist

    fun addListener(listener: MediaDataListener) = listenerManager.addListener(listener)
    fun removeListener(listener: MediaDataListener) = listenerManager.removeListener(listener)

    fun onPlaybackStateChanged(state: Int) {
        isMediaPlaying = state == PlaybackState.STATE_PLAYING
        listenerManager.notify { it.onPlaybackStateChanged(state) }
    }

    fun onAlbumArtChanged(drawable: Drawable) {
        listenerManager.notify { it.onAlbumArtChanged(drawable) }
    }

    fun onMediaColorsChanged(color: Int) {
        listenerManager.notify { it.onMediaColorsChanged(color) }
    }

    fun onMetadataChanged(metadata: MediaMetadata) {
        _trackTitle = metadata.getString(MediaMetadata.METADATA_KEY_TITLE) ?: "Unknown"
        _artist = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST) ?: "Unknown"
        listenerManager.notify { it.onMetadataChanged(_trackTitle, _artist) }
    }

    companion object {
        @Volatile
        private var INSTANCE: MediaSessionManager? = null

        fun get(): MediaSessionManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MediaSessionManager().also { INSTANCE = it }
            }
        }
    }
}