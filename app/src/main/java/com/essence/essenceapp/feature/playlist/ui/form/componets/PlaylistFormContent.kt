package com.essence.essenceapp.feature.playlist.ui.form.componets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ErrorOutline
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.feature.playlist.ui.form.PlaylistFormAction
import com.essence.essenceapp.feature.playlist.ui.form.PlaylistFormState
import com.essence.essenceapp.feature.playlist.ui.form.PlaylistFormUiState
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import com.essence.essenceapp.ui.theme.MidnightBlack
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
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            PlaylistFormHeader(isEditing = isEditing, onDismiss = onDismiss)

            FormFieldsIsland(
                state = state,
                textFieldColors = textFieldColors,
                onAction = onAction,
                hasError = safeError != null
            )

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

            AnimatedVisibility(
                visible = safeError != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                ErrorBanner(message = safeError ?: "")
            }

            ActionButtonsRow(
                isEditing = isEditing,
                canSubmit = state.canSubmit,
                isSubmitting = state.isSubmitting,
                onSubmit = { onAction(PlaylistFormAction.Submit) },
                onDismiss = onDismiss
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        if (state.isSubmitting) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.55f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = SoftRose,
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
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(12.dp),
                color = SoftRose.copy(alpha = 0.10f),
                border = BorderStroke(1.dp, SoftRose.copy(alpha = 0.22f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.PlaylistAdd,
                        contentDescription = null,
                        tint = SoftRose,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = if (isEditing) "Editar playlist" else "Nueva playlist",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = if (isEditing) "Actualiza los datos de tu playlist."
                    else "Completa los campos para crearla.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f)
                )
            }
        }

        IconButton(onClick = onDismiss) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = "Cerrar",
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f)
            )
        }
    }
}

@Composable
private fun FormFieldsIsland(
    state: PlaylistFormUiState.Editing,
    textFieldColors: androidx.compose.material3.TextFieldColors,
    onAction: (PlaylistFormAction) -> Unit,
    hasError: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.55f),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)
        ),
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
                        hasError = hasError,
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
                        hasError = hasError,
                        onAction = onAction
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Descripción") },
                placeholder = {
                    Text(
                        text = "Opcional",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
                    )
                },
                minLines = 3,
                maxLines = 5,
                enabled = !state.isSubmitting,
                shape = MaterialTheme.shapes.medium,
                colors = textFieldColors
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
    val accent = if (isPublic) MutedTeal else MaterialTheme.colorScheme.onSurface
    val accentAlpha = if (isPublic) 0.22f else 0.08f

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onChange(!isPublic) },
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.55f),
        border = BorderStroke(1.dp, accent.copy(alpha = accentAlpha)),
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
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(36.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = if (isPublic) MutedTeal.copy(alpha = 0.10f)
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isPublic) Icons.Outlined.Public else Icons.Outlined.Lock,
                            contentDescription = null,
                            tint = if (isPublic) MutedTeal
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Visibilidad",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isPublic) "Pública — visible para todos"
                        else "Privada — solo tú",
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
private fun ErrorBanner(message: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.error.copy(alpha = 0.08f),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.error.copy(alpha = 0.22f)
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ActionButtonsRow(
    isEditing: Boolean,
    canSubmit: Boolean,
    isSubmitting: Boolean,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onDismiss,
            enabled = !isSubmitting,
            modifier = Modifier
                .weight(1f)
                .height(50.dp),
            shape = RoundedCornerShape(50),
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.20f)
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.78f)
            )
        ) {
            Text(
                text = "Cancelar",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }

        PrimarySubmitButton(
            modifier = Modifier
                .weight(1f)
                .height(50.dp),
            enabled = canSubmit,
            text = if (isEditing) "Guardar" else "Crear",
            onClick = onSubmit
        )
    }
}

@Composable
private fun PrimarySubmitButton(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    text: String,
    onClick: () -> Unit
) {
    val alpha = if (enabled) 1f else 0.40f

    Surface(
        modifier = modifier.clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(50),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            SoftRose.copy(alpha = 0.95f * alpha),
                            Color(0xFFBB4477).copy(alpha = 0.92f * alpha)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MidnightBlack.copy(alpha = 0.92f * alpha)
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
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Surface(
            modifier = Modifier.size(64.dp),
            shape = CircleShape,
            color = MutedTeal.copy(alpha = 0.12f),
            border = BorderStroke(1.dp, MutedTeal.copy(alpha = 0.30f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = MutedTeal,
                    modifier = Modifier.size(34.dp)
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
            text = "Los cambios se aplicaron correctamente.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.60f),
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

@Preview(name = "Playlist Form - Error", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun PlaylistFormErrorPreview() {
    EssenceAppTheme {
        PlaylistFormContent(
            state = PlaylistFormUiState.Editing(
                form = PlaylistFormState(title = "Lo-Fi Nights"),
                errorMessage = "No se pudo guardar la playlist. Verifica tu conexión."
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
