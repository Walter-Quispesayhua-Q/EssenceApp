package com.essence.essenceapp.feature.auth.login.ui.components

import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.essence.essenceapp.ui.theme.EssenceAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginTopBar(
    title: String,
    onBackClick: (() -> Unit)? = null
) {
    CenterAlignedTopAppBar(
        modifier = Modifier.statusBarsPadding(),
        title = { Text(text = title, style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
            if (onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            navigationIconContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginTopBarPreview() {
    EssenceAppTheme(darkTheme = true, dynamicColor = false) {
        LoginTopBar(title = "Iniciar sesión")
    }
}
