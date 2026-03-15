// app/src/main/java/com/essence/essenceapp/feature/playlist/ui/form/componets/PlaylistFormContent.kt
package com.essence.essenceapp.feature.playlist.ui.form.componets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.feature.playlist.ui.form.PlaylistFormAction
import com.essence.essenceapp.feature.playlist.ui.form.PlaylistFormState
import com.essence.essenceapp.feature.playlist.ui.form.PlaylistFormUiState
import com.essence.essenceapp.shared.ui.components.cards.BaseCard
import com.essence.essenceapp.ui.theme.EssenceAppTheme

@Composable
fun PlaylistFormContent(
    modifier: Modifier = Modifier,
    state: PlaylistFormUiState,
    isEditing: Boolean,
    onAction: (PlaylistFormAction) -> Unit,
    onDismiss: () -> Unit
) {
    when (state) {
        is PlaylistFormUiState.Editing -> PlaylistFormEditingContent(
            modifier = modifier,
            state = state,
            isEditing = isEditing,
            onAction = onAction,
            onDismiss = onDismiss
        )

        PlaylistFormUiState.Success -> PlaylistFormSuccessContent(modifier = modifier)
    }
}

@Composable
private fun PlaylistFormEditingContent(
    modifier: Modifier = Modifier,
    state: PlaylistFormUiState.Editing,
    isEditing: Boolean,
    onAction: (PlaylistFormAction) -> Unit,
    onDismiss: () -> Unit
) {
    val safeError = state.errorMessage?.ifBlank { "No se pudo guardar la playlist." }

    Box(modifier = modifier.fillMaxWidth()) {
        BaseCard(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PlaylistFormHeader(
                    isEditing = isEditing,
                    onDismiss = onDismiss
                )

                OutlinedTextField(
                    value = state.form.title,
                    onValueChange = {
                        dispatchFieldAction(
                            action = PlaylistFormAction.TitleChanged(it),
                            hasError = safeError != null,
                            onAction = onAction
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Título") },
                    singleLine = true,
                    enabled = !state.isSubmitting
                )

                OutlinedTextField(
                    value = state.form.description,
                    onValueChange = {
                        dispatchFieldAction(
                            action = PlaylistFormAction.DescriptionChanged(it),
                            hasError = safeError != null,
                            onAction = onAction
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Descripción") },
                    minLines = 3,
                    maxLines = 5,
                    enabled = !state.isSubmitting
                )

                PlaylistVisibilityCard(
                    isPublic = state.form.isPublic,
                    enabled = !state.isSubmitting,
                    onChange = {
                        dispatchFieldAction(
                            action = PlaylistFormAction.IsPublicChanged(it),
                            hasError = safeError != null,
                            onAction = onAction
                        )
                    }
                )

                if (safeError != null) {
                    Text(
                        text = safeError,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        enabled = !state.isSubmitting,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = { onAction(PlaylistFormAction.Submit) },
                        enabled = state.canSubmit,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (isEditing) "Guardar" else "Crear")
                    }
                }
            }
        }

        if (state.isSubmitting) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.45f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun PlaylistFormHeader(
    isEditing: Boolean,
    onDismiss: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = if (isEditing) "Editar playlist" else "Nueva playlist",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Completa los campos y guarda los cambios.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
            )
        }

        TextButton(onClick = onDismiss) {
            Text("Cerrar")
        }
    }
}

@Composable
private fun PlaylistVisibilityCard(
    isPublic: Boolean,
    enabled: Boolean,
    onChange: (Boolean) -> Unit
) {
    Surface(
        tonalElevation = 2.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Visibilidad",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (isPublic) "Pública" else "Privada",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                )
            }

            Switch(
                checked = isPublic,
                onCheckedChange = onChange,
                enabled = enabled
            )
        }
    }
}

@Composable
private fun PlaylistFormSuccessContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(30.dp)
            )
            Text(
                text = "Playlist guardada",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private fun dispatchFieldAction(
    action: PlaylistFormAction,
    hasError: Boolean,
    onAction: (PlaylistFormAction) -> Unit
) {
    onAction(action)
    if (hasError) onAction(PlaylistFormAction.ClearError)
}

@Preview(name = "Playlist Form - Create", showBackground = true)
@Composable
private fun PlaylistFormCreatePreview() {
    EssenceAppTheme {
        PlaylistFormContent(
            state = PlaylistFormUiState.Editing(),
            isEditing = false,
            onAction = {},
            onDismiss = {}
        )
    }
}

@Preview(name = "Playlist Form - Edit / Submitting", showBackground = true)
@Composable
private fun PlaylistFormEditSubmittingPreview() {
    EssenceAppTheme {
        PlaylistFormContent(
            state = PlaylistFormUiState.Editing(
                form = PlaylistFormState(
                    title = "Lo-Fi Nights",
                    description = "Canciones para estudiar y concentrarme.",
                    isPublic = true
                ),
                isSubmitting = true
            ),
            isEditing = true,
            onAction = {},
            onDismiss = {}
        )
    }
}
