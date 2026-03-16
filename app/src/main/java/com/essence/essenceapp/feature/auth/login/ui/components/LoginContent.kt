package com.essence.essenceapp.feature.auth.login.ui.components

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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
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
import com.essence.essenceapp.feature.auth.login.ui.LoginAction
import com.essence.essenceapp.feature.auth.login.ui.LoginUiState
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import com.essence.essenceapp.ui.theme.GraphiteSurface
import com.essence.essenceapp.ui.theme.LuxeGold
import com.essence.essenceapp.ui.theme.MidnightBlack
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose

@Composable
fun LoginContent(
    modifier: Modifier = Modifier,
    state: LoginUiState,
    onAction: (LoginAction) -> Unit,
    onBack: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {}
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    val isLoading = state is LoginUiState.Loading
    val errorMessage = (state as? LoginUiState.Error)?.message
    val canSubmit = !isLoading && email.isNotBlank() && password.length >= 6

    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            SoftRose.copy(alpha = 0.15f),
                            MutedTeal.copy(alpha = 0.08f),
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
                            SoftRose.copy(alpha = 0.06f),
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
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            Text(
                text = "Essence",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = PureWhite
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Tu música, tu esencia",
                style = MaterialTheme.typography.bodyMedium,
                color = PureWhite.copy(alpha = 0.45f)
            )

            Spacer(modifier = Modifier.height(48.dp))

            GlassFormIsland(
                email = email,
                password = password,
                passwordVisible = passwordVisible,
                isLoading = isLoading,
                errorMessage = errorMessage,
                canSubmit = canSubmit,
                onEmailChange = {
                    email = it
                    onAction(LoginAction.EmailChanged(it))
                    if (state is LoginUiState.Error) onAction(LoginAction.ClearError)
                },
                onPasswordChange = {
                    password = it
                    onAction(LoginAction.PasswordChanged(it))
                    if (state is LoginUiState.Error) onAction(LoginAction.ClearError)
                },
                onTogglePasswordVisibility = { passwordVisible = !passwordVisible },
                onSubmit = { onAction(LoginAction.Submit) }
            )

            Spacer(modifier = Modifier.height(28.dp))

            if (state !is LoginUiState.Success) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "¿No tienes cuenta?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PureWhite.copy(alpha = 0.5f)
                    )
                    TextButton(onClick = onNavigateToRegister) {
                        Text(
                            text = "Regístrate",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MutedTeal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun GlassFormIsland(
    email: String,
    password: String,
    passwordVisible: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    canSubmit: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onSubmit: () -> Unit
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
                                SoftRose.copy(alpha = 0.06f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Iniciar sesión",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite
                )

                Text(
                    text = "Ingresa tus credenciales para continuar",
                    style = MaterialTheme.typography.bodySmall,
                    color = PureWhite.copy(alpha = 0.45f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Correo electrónico") },
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
                    )
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = onPasswordChange,
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
                        onDone = { if (canSubmit) onSubmit() }
                    )
                )

                if (errorMessage != null) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFB00020).copy(alpha = 0.1f),
                        border = BorderStroke(1.dp, Color(0xFFB00020).copy(alpha = 0.2f))
                    ) {
                        Text(
                            text = errorMessage,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFCF6679),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                ElevatedButton(
                    onClick = onSubmit,
                    enabled = canSubmit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = SoftRose,
                        contentColor = PureWhite,
                        disabledContainerColor = SoftRose.copy(alpha = 0.3f),
                        disabledContentColor = PureWhite.copy(alpha = 0.4f)
                    ),
                    elevation = ButtonDefaults.elevatedButtonElevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 2.dp
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp,
                            color = PureWhite
                        )
                    } else {
                        Text(
                            text = "Entrar",
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
private fun glassTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = PureWhite,
    unfocusedTextColor = PureWhite.copy(alpha = 0.8f),
    cursorColor = SoftRose,
    focusedBorderColor = SoftRose.copy(alpha = 0.5f),
    unfocusedBorderColor = PureWhite.copy(alpha = 0.1f),
    focusedLabelColor = SoftRose.copy(alpha = 0.8f),
    unfocusedLabelColor = PureWhite.copy(alpha = 0.35f),
    focusedLeadingIconColor = MutedTeal,
    unfocusedLeadingIconColor = MutedTeal.copy(alpha = 0.5f),
    focusedTrailingIconColor = PureWhite.copy(alpha = 0.6f),
    unfocusedTrailingIconColor = PureWhite.copy(alpha = 0.35f),
    focusedContainerColor = PureWhite.copy(alpha = 0.03f),
    unfocusedContainerColor = Color.Transparent
)

@Preview(name = "Login - Idle", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun IdlePreview() {
    EssenceAppTheme {
        LoginContent(state = LoginUiState.Idle, onAction = {})
    }
}

@Preview(name = "Login - Loading", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun LoadingPreview() {
    EssenceAppTheme {
        LoginContent(state = LoginUiState.Loading, onAction = {})
    }
}

@Preview(name = "Login - Error", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun ErrorPreview() {
    EssenceAppTheme {
        LoginContent(
            state = LoginUiState.Error("Credenciales inválidas. Verifica tu correo y contraseña."),
            onAction = {}
        )
    }
}