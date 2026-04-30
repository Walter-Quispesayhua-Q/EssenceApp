package com.essence.essenceapp.feature.auth.register.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.essence.essenceapp.feature.auth.register.ui.RegisterAction
import com.essence.essenceapp.feature.auth.register.ui.RegisterFormState
import com.essence.essenceapp.feature.auth.register.ui.RegisterUiState
import com.essence.essenceapp.feature.auth.register.ui.UsernameStatus
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import com.essence.essenceapp.ui.theme.GraphiteSurface
import com.essence.essenceapp.ui.theme.LuxeGold
import com.essence.essenceapp.ui.theme.MidnightBlack
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose

@Composable
fun RegisterContent(
    modifier: Modifier = Modifier,
    state: RegisterUiState,
    onAction: (RegisterAction) -> Unit,
    onBack: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    when (state) {
        is RegisterUiState.Success -> Unit
        is RegisterUiState.Editing -> RegisterEditingContent(
            modifier = modifier,
            ui = state,
            onAction = onAction,
            onBack = onBack,
            onNavigateToLogin = onNavigateToLogin
        )
        is RegisterUiState.Idle -> RegisterEditingContent(
            modifier = modifier,
            ui = RegisterUiState.Editing(),
            onAction = onAction,
            onBack = onBack,
            onNavigateToLogin = onNavigateToLogin
        )
    }
}

@Composable
private fun RegisterEditingContent(
    modifier: Modifier = Modifier,
    ui: RegisterUiState.Editing,
    onAction: (RegisterAction) -> Unit,
    onBack: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MutedTeal.copy(alpha = 0.15f),
                            SoftRose.copy(alpha = 0.08f),
                            MidnightBlack
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MutedTeal.copy(alpha = 0.06f),
                            Color.Transparent
                        ),
                        radius = 900f
                    )
                )
        )

        Surface(
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 16.dp, top = 12.dp)
                .zIndex(10f),
            shape = CircleShape,
            color = GraphiteSurface.copy(alpha = 0.5f),
            border = BorderStroke(1.dp, PureWhite.copy(alpha = 0.08f))
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = PureWhite.copy(alpha = 0.85f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(96.dp))

            Text(
                text = "Essence",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = PureWhite
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Únete a la experiencia",
                style = MaterialTheme.typography.bodyMedium,
                color = PureWhite.copy(alpha = 0.45f)
            )

            Spacer(modifier = Modifier.height(40.dp))

            GlassFormIsland(
                ui = ui,
                passwordVisible = passwordVisible,
                onTogglePasswordVisibility = { passwordVisible = !passwordVisible },
                onAction = onAction
            )

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿Ya tienes cuenta?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PureWhite.copy(alpha = 0.5f)
                )
                TextButton(onClick = onNavigateToLogin) {
                    Text(
                        text = "Inicia sesión",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MutedTeal
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun GlassFormIsland(
    ui: RegisterUiState.Editing,
    passwordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    onAction: (RegisterAction) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = GraphiteSurface.copy(alpha = 0.55f),
        border = BorderStroke(1.dp, PureWhite.copy(alpha = 0.06f))
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                PureWhite.copy(alpha = 0.04f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                MutedTeal.copy(alpha = 0.06f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Crear cuenta",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite
                )

                Text(
                    text = "Completa tus datos para comenzar",
                    style = MaterialTheme.typography.bodySmall,
                    color = PureWhite.copy(alpha = 0.45f)
                )

                Spacer(modifier = Modifier.height(2.dp))

                OutlinedTextField(
                    value = ui.form.username,
                    onValueChange = {
                        onAction(RegisterAction.UsernameChanged(it))
                        if (ui.errorMessage != null) onAction(RegisterAction.ClearError)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nombre de usuario") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = LuxeGold.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = glassTextFieldColors(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )
                )

                UsernameStatusIndicator(status = ui.usernameStatus)

                OutlinedTextField(
                    value = ui.form.email,
                    onValueChange = {
                        onAction(RegisterAction.EmailChanged(it))
                        if (ui.errorMessage != null) onAction(RegisterAction.ClearError)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Correo electrónico") },
                    isError = ui.emailError != null,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = MutedTeal.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = glassTextFieldColors(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    supportingText = {
                        if (ui.emailError != null) {
                            Text(
                                text = ui.emailError,
                                style = MaterialTheme.typography.labelSmall,
                                color = SoftRose.copy(alpha = 0.85f)
                            )
                        }
                    }
                )

                OutlinedTextField(
                    value = ui.form.password,
                    onValueChange = {
                        onAction(RegisterAction.PasswordChanged(it))
                        if (ui.errorMessage != null) onAction(RegisterAction.ClearError)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Contraseña") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = MutedTeal.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = glassTextFieldColors(),
                    visualTransformation = if (passwordVisible) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = onTogglePasswordVisibility) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff
                                else Icons.Default.Visibility,
                                contentDescription = if (passwordVisible) "Ocultar" else "Mostrar",
                                tint = PureWhite.copy(alpha = 0.4f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { if (ui.canSubmit) onAction(RegisterAction.Submit) }
                    ),
                    supportingText = {
                        if (ui.form.password.isNotEmpty() && ui.form.password.length < 6) {
                            Text(
                                text = "Mínimo 6 caracteres",
                                style = MaterialTheme.typography.labelSmall,
                                color = SoftRose.copy(alpha = 0.7f)
                            )
                        }
                    }
                )

                if (ui.errorMessage != null) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFB00020).copy(alpha = 0.1f),
                        border = BorderStroke(1.dp, Color(0xFFB00020).copy(alpha = 0.2f))
                    ) {
                        Text(
                            text = ui.errorMessage,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFCF6679),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                ElevatedButton(
                    onClick = { onAction(RegisterAction.Submit) },
                    enabled = ui.canSubmit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = MutedTeal,
                        contentColor = PureWhite,
                        disabledContainerColor = MutedTeal.copy(alpha = 0.3f),
                        disabledContentColor = PureWhite.copy(alpha = 0.4f)
                    ),
                    elevation = ButtonDefaults.elevatedButtonElevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 2.dp
                    )
                ) {
                    if (ui.isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp,
                            color = PureWhite
                        )
                    } else {
                        Text(
                            text = "Crear cuenta",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UsernameStatusIndicator(status: UsernameStatus) {
    when (status) {
        is UsernameStatus.Idle -> Unit

        is UsernameStatus.Checking -> {
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = LuxeGold.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, LuxeGold.copy(alpha = 0.12f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(12.dp),
                        strokeWidth = 1.5.dp,
                        color = LuxeGold
                    )
                    Text(
                        text = "Validando usuario...",
                        style = MaterialTheme.typography.labelSmall,
                        color = LuxeGold.copy(alpha = 0.85f)
                    )
                }
            }
        }

        is UsernameStatus.Available -> {
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = MutedTeal.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, MutedTeal.copy(alpha = 0.15f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MutedTeal,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Nombre disponible",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = MutedTeal
                    )
                }
            }
        }

        is UsernameStatus.Unavailable -> {
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = SoftRose.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, SoftRose.copy(alpha = 0.15f))
            ) {
                Text(
                    text = status.message,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = SoftRose
                )
            }
        }
    }
}

@Composable
private fun glassTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = PureWhite,
    unfocusedTextColor = PureWhite.copy(alpha = 0.8f),
    cursorColor = MutedTeal,
    focusedBorderColor = MutedTeal.copy(alpha = 0.5f),
    unfocusedBorderColor = PureWhite.copy(alpha = 0.1f),
    focusedLabelColor = MutedTeal.copy(alpha = 0.8f),
    unfocusedLabelColor = PureWhite.copy(alpha = 0.35f),
    focusedLeadingIconColor = MutedTeal,
    unfocusedLeadingIconColor = MutedTeal.copy(alpha = 0.5f),
    focusedTrailingIconColor = PureWhite.copy(alpha = 0.6f),
    unfocusedTrailingIconColor = PureWhite.copy(alpha = 0.35f),
    focusedContainerColor = PureWhite.copy(alpha = 0.03f),
    unfocusedContainerColor = Color.Transparent,
    focusedSupportingTextColor = SoftRose.copy(alpha = 0.7f),
    unfocusedSupportingTextColor = PureWhite.copy(alpha = 0.35f)
)

@Preview(name = "Register - Idle", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun IdlePreview() {
    EssenceAppTheme {
        RegisterContent(state = RegisterUiState.Idle, onAction = {})
    }
}

@Preview(name = "Register - Editing", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun EditingPreview() {
    EssenceAppTheme {
        RegisterContent(state = RegisterUiState.Editing(), onAction = {})
    }
}

@Preview(name = "Register - Checking", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun CheckingPreview() {
    EssenceAppTheme {
        RegisterContent(
            state = RegisterUiState.Editing(usernameStatus = UsernameStatus.Checking),
            onAction = {}
        )
    }
}

@Preview(name = "Register - Available", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun AvailablePreview() {
    EssenceAppTheme {
        RegisterContent(
            state = RegisterUiState.Editing(
                form = RegisterFormState(username = "bunny_fan", email = "fan@mail.com", password = "123456"),
                usernameStatus = UsernameStatus.Available
            ),
            onAction = {}
        )
    }
}

@Preview(name = "Register - Error", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun ErrorPreview() {
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
