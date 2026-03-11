package com.essence.essenceapp.feature.playlist.ui.list.componets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.essence.essenceapp.ui.theme.EssenceAppTheme

private val LibraryAccent = Color(0xFF00CED1)

@Composable
fun PlaylistListTopBar(
    title: String = "Mi Biblioteca",
    onCreatePlaylist: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 56.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        IconButton(
            onClick = onCreatePlaylist,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(LibraryAccent.copy(alpha = 0.12f))
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Crear playlist",
                tint = LibraryAccent
            )
        }
    }
}

@Preview(name = "Playlist List TopBar", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun PlaylistListTopBarPreview() {
    EssenceAppTheme {
        PlaylistListTopBar()
    }
}
