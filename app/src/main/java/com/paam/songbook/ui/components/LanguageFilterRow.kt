package com.paam.songbook.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LanguageFilterRow(
    languages: List<String>,
    selectedLanguage: String?,
    onLanguageSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(vertical = 12.dp)) {
        Text(
            text = "Language",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedLanguage == null,
                    onClick = { onLanguageSelected(null) },
                    label = { Text("All") }
                )
            }
            items(languages) { lang ->
                FilterChip(
                    selected = selectedLanguage == lang,
                    onClick = { onLanguageSelected(lang) },
                    label = { Text(lang) }
                )
            }
        }
    }
}
