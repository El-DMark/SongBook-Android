package com.paam.songbook.model

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata

data class Song(
    val title: String,
    val artist: String,
    val url: String,
    val albumArt: String
)

fun sampleSongs(): List<Song> = listOf(
    Song(
        "Snow White Dove",
        "Bro. Lal",
        "https://drive.google.com/uc?export=download&id=1tkXoNuMh33ZqBrrOMiKFa8GWZ0onDWT3",
        "https://i0.wp.com/endtimesmessages.com/wp-content/uploads/2019/03/The-Supernatural-Cloud-1963.jpg"
    ),
    Song(
        "As The Deer",
        "Bro. Nischal",
        "https://drive.google.com/uc?export=download&id=1T3-MiEJQ5E2vK2uiI5TFlp_vjVfURGL8",
        "https://i0.wp.com/endtimesmessages.com/wp-content/uploads/2019/03/The-Supernatural-Cloud-1963.jpg"
    ),
    Song(
        "Abba Father",
        "Bro. Ashish",
        "https://drive.google.com/uc?export=download&id=1AbGD7hYC1j9p09EaQzpeFjvLIQ_B7gDi",
        "https://i0.wp.com/endtimesmessages.com/wp-content/uploads/2019/03/The-Supernatural-Cloud-1963.jpg"
    ),
    Song(
        "I Want To Go",
        "Bro. Vin Dayal",
        "https://drive.google.com/uc?export=download&id=1-YGY5T2tJQhlxvSoOV9-7aJue7JrDj-m",
        "https://i0.wp.com/endtimesmessages.com/wp-content/uploads/2019/03/The-Supernatural-Cloud-1963.jpg"
    ),
    Song(
        "My Faith Look Up To Thee",
        "Bro. Branham",
        "https://drive.google.com/uc?export=download&id=1Sw_Bdg7AH6eKREKr6WB3PUdDUyc9N9Az",
        "https://i0.wp.com/endtimesmessages.com/wp-content/uploads/2019/03/The-Supernatural-Cloud-1963.jpg"
    ),
    Song(
        "गर चाहते छूटना गुनाहों से तुम लहू में कुदरत है",
        "Bro. Nischal",
        "https://drive.google.com/uc?export=download&id=1NHaDHQlDXBWwkAuH60XLXnOhyyjm2G79",
        "https://i0.wp.com/endtimesmessages.com/wp-content/uploads/2019/03/The-Supernatural-Cloud-1963.jpg"
    )
)

/**
 * Helper extension to convert a Song into a MediaItem for ExoPlayer/MediaController.
 */
fun Song.toMediaItem(): MediaItem =
    MediaItem.Builder()
        .setUri(url)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(title)
                .setArtist(artist)
                .setArtworkUri(Uri.parse(albumArt))
                .build()
        )
        .build()
