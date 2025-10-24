package com.paam.songbook.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ArtistFilterList(
    artists: List<String>,
    selectedArtist: String?,
    onArtistSelected: (String?) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // "All" option
        FilterListItem(
            label = "All Artists",
            selected = selectedArtist == null,
            onClick = { onArtistSelected(null) }
        )

        artists.forEach { artist ->
            FilterListItem(
                label = artist,
                selected = selectedArtist == artist,
                onClick = { onArtistSelected(artist) }
            )
        }
    }
}

@Composable
private fun FilterListItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        else MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = label,
            style = if (selected) MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.primary
            ) else MaterialTheme.typography.bodyLarge
        )
    }
}
