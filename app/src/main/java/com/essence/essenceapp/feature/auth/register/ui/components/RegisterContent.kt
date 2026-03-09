package com.essence.essenceapp.feature.auth.register.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.feature.auth.register.ui.RegisterAction
import com.essence.essenceapp.feature.auth.register.ui.RegisterUiState
import com.essence.essenceapp.feature.auth.register.ui.UsernameStatus
import com.essence.essenceapp.ui.theme.EssenceAppTheme

@Composable
fun RegisterContent(
    modifier: Modifier = Modifier,
    state: RegisterUiState,
    onAction: (RegisterAction) -> Unit
) {
    when (state) {
        is RegisterUiState.Success -> Unit
        is RegisterUiState.Editing -> RegisterEditingContent(
            modifier = modifier,
            ui = state,
            onAction = onAction
        )
        is RegisterUiState.Idle -> RegisterEditingContent(
            modifier = modifier,
            ui = RegisterUiState.Editing(),
            onAction = onAction
        )
    }
}

@Composable
private fun RegisterEditingContent(
    modifier: Modifier = Modifier,
    ui: RegisterUiState.Editing,
    onAction: (RegisterAction) -> Unit
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            RegisterFormCard(
                ui = ui,
                passwordVisible = passwordVisible,
                onTogglePasswordVisibility = { passwordVisible = !passwordVisible },
                onAction = onAction
            )
        }
    }
}

@Composable
private fun RegisterFormCard(
    ui: RegisterUiState.Editing,
    passwordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    onAction: (RegisterAction) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(0.94f),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RegisterCardHeader()

            OutlinedTextField(
                value = ui.form.username,
                onValueChange = {
                    onAction(RegisterAction.UsernameChanged(it))
                    if (ui.errorMessage != null) onAction(RegisterAction.ClearError)
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nombre de usuario") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )

            UsernameStatusMessage(status = ui.usernameStatus)

            OutlinedTextField(
                value = ui.form.email,
                onValueChange = {
                    onAction(RegisterAction.EmailChanged(it))
                    if (ui.errorMessage != null) onAction(RegisterAction.ClearError)
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Correo electrónico") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            OutlinedTextField(
                value = ui.form.password,
                onValueChange = {
                    onAction(RegisterAction.PasswordChanged(it))
                    if (ui.errorMessage != null) onAction(RegisterAction.ClearError)
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Contraseña") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    IconButton(onClick = onTogglePasswordVisibility) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { if (ui.canSubmit) onAction(RegisterAction.Submit) }
                )
            )

            if (ui.errorMessage != null) {
                Text(
                    text = ui.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Button(
                onClick = { onAction(RegisterAction.Submit) },
                enabled = ui.canSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                if (ui.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Crear cuenta")
                }
            }
        }
    }
}

@Composable
private fun RegisterCardHeader() {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "Registro",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Crea tu cuenta para continuar",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
        )
    }
}

@Composable
private fun UsernameStatusMessage(status: UsernameStatus) {
    when (status) {
        is UsernameStatus.Idle -> Unit

        is UsernameStatus.Checking -> {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(14.dp),
                    strokeWidth = 2.dp
                )
                Text(
                    text = "Validando usuario...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                )
            }
        }

        is UsernameStatus.Available -> {
            Text(
                text = "Nombre de usuario disponible",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        is UsernameStatus.Unavailable -> {
            Text(
                text = status.message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Preview(name = "Register - Idle", showBackground = true)
@Composable
private fun RegisterIdlePreview() {
    EssenceAppTheme {
        RegisterContent(
            state = RegisterUiState.Idle,
            onAction = {}
        )
    }
}

@Preview(name = "Register - Editing", showBackground = true)
@Composable
private fun RegisterEditingPreview() {
    EssenceAppTheme {
        RegisterContent(
            state = RegisterUiState.Editing(),
            onAction = {}
        )
    }
}

@Preview(name = "Register - Checking Username", showBackground = true)
@Composable
private fun RegisterCheckingPreview() {
    EssenceAppTheme {
        RegisterContent(
            state = RegisterUiState.Editing(
                usernameStatus = UsernameStatus.Checking
            ),
            onAction = {}
        )
    }
}

@Preview(name = "Register - Error", showBackground = true)
@Composable
private fun RegisterErrorPreview() {
    EssenceAppTheme {
        RegisterContent(
            state = RegisterUiState.Editing(
                usernameStatus = UsernameStatus.Unavailable("Usuario no disponible"),
                errorMessage = "No se pudo registrar. Inténtalo otra vez."
            ),
            onAction = {}
        )
    }
}