package com.essence.essenceapp.feature.home.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.R
import com.essence.essenceapp.ui.theme.EssenceAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    title: String?
) {
    TopAppBar(
        navigationIcon = {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        },
        title = {
            Text(text = title ?: "Home")
                },
        colors = TopAppBarDefaults.topAppBarColors(
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun HomeTopBarPreview() {
    EssenceAppTheme {
        HomeTopBar(title = null)
    }

}