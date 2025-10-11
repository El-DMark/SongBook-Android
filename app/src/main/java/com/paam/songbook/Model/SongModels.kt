package com.paam.songbook.model

data class Song(
    val title: String,
    val artist: String,
    val url: String,
    val albumArt: String
)

fun sampleSongs(): List<Song> = listOf(
    Song("Moonlight", "Ludwig", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3", "file:///storage/emulated/0/Music/art/moonlight.jpg"),
    Song("Aurora", "Nova", "/storage/emulated/0/Music/aurora.mp3", "file:///storage/emulated/0/Music/art/aurora.jpg"),
    Song("Midnight Drive", "Neon", "/storage/emulated/0/Music/midnight.mp3", "file:///storage/emulated/0/Music/art/midnight.jpg"),
    Song("Slow Waves", "Coast", "/storage/emulated/0/Music/slowwaves.mp3", "file:///storage/emulated/0/Music/art/slowwaves.jpg")
)
