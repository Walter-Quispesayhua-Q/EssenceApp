package com.essence.essenceapp.feature.playlist.ui.form.componets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.BorderStroke
import com.essence.essenceapp.feature.playlist.ui.form.PlaylistFormAction
import com.essence.essenceapp.feature.playlist.ui.form.PlaylistFormState
import com.essence.essenceapp.feature.playlist.ui.form.PlaylistFormUiState
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.SoftRose

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
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MutedTeal,
        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
        cursorColor = MutedTeal,
        focusedLabelColor = MutedTeal
    )

    Box(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PlaylistFormHeader(isEditing = isEditing, onDismiss = onDismiss)

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
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
                        enabled = !state.isSubmitting,
                        shape = MaterialTheme.shapes.medium,
                        colors = textFieldColors
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
                        enabled = !state.isSubmitting,
                        shape = MaterialTheme.shapes.medium,
                        colors = textFieldColors
                    )
                }
            }

            PlaylistVisibilityIsland(
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
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    enabled = !state.isSubmitting,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                ) {
                    Text(
                        text = "Cancelar",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                OutlinedButton(
                    onClick = { onAction(PlaylistFormAction.Submit) },
                    enabled = state.canSubmit,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(
                        1.dp,
                        if (state.canSubmit) MutedTeal else MutedTeal.copy(alpha = 0.3f)
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MutedTeal,
                        disabledContentColor = MutedTeal.copy(alpha = 0.4f)
                    )
                ) {
                    Text(
                        text = if (isEditing) "Guardar" else "Crear",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        if (state.isSubmitting) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MutedTeal,
                    strokeWidth = 3.dp
                )
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
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Completa los campos y guarda los cambios.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f)
            )
        }

        IconButton(onClick = onDismiss) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = "Cerrar",
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun PlaylistVisibilityIsland(
    isPublic: Boolean,
    enabled: Boolean,
    onChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isPublic) Icons.Outlined.Public else Icons.Outlined.Lock,
                    contentDescription = null,
                    tint = if (isPublic) MutedTeal else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Visibilidad",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isPublic) "Pública — visible para todos" else "Privada — solo tú",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                    )
                }
            }

            Switch(
                checked = isPublic,
                onCheckedChange = onChange,
                enabled = enabled,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.onSecondary,
                    checkedTrackColor = MutedTeal,
                    uncheckedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    uncheckedTrackColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    }
}

@Composable
private fun PlaylistFormSuccessContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = MutedTeal.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = MutedTeal,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
        Text(
            text = "Playlist guardada",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Los cambios se aplicaron correctamente",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
            textAlign = TextAlign.Center
        )
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

@Preview(name = "Playlist Form - Create", showBackground = true, backgroundColor = 0xFF121212)
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

@Preview(name = "Playlist Form - Edit", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun PlaylistFormEditPreview() {
    EssenceAppTheme {
        PlaylistFormContent(
            state = PlaylistFormUiState.Editing(
                form = PlaylistFormState(
                    title = "Lo-Fi Nights",
                    description = "Canciones para estudiar.",
                    isPublic = true
                )
            ),
            isEditing = true,
            onAction = {},
            onDismiss = {}
        )
    }
}

@Preview(name = "Playlist Form - Success", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun PlaylistFormSuccessPreview() {
    EssenceAppTheme {
        PlaylistFormContent(
            state = PlaylistFormUiState.Success,
            isEditing = false,
            onAction = {},
            onDismiss = {}
        )
    }
}