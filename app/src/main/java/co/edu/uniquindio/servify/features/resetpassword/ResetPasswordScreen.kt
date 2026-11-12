package co.edu.uniquindio.servify.features.resetpassword

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import co.edu.uniquindio.servify.ui.components.button.ServifyFilledButton
import co.edu.uniquindio.servify.ui.components.input.ServifyTextField
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle

@Composable
fun ResetPasswordScreen(
    oobCode: String,
    onFinished: () -> Unit,
    viewModel: ResetPasswordViewModel = hiltViewModel()
) {

    val state by viewModel.uiState.collectAsState()

    when {
        state.success -> ResetPasswordSuccessScreen(onFinished = onFinished)
        state.invalidCode -> ResetPasswordInvalidScreen(
            message = state.message,
            onFinished = onFinished
        )
        else -> ResetPasswordFormScreen(
            state = state,
            onPasswordChange = viewModel::onPasswordChange,
            onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
            onToggleVisibility = viewModel::togglePasswordVisibility,
            onSubmit = { viewModel.onSubmit(oobCode) }
        )
    }
}

@Composable
private fun ResetPasswordFormScreen(
    state: ResetPasswordUiState,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onToggleVisibility: () -> Unit,
    onSubmit: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(
                start = 24.dp,
                end = 24.dp,
                bottom = 32.dp
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 32.dp,
                    bottom = 32.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Filled.LockReset,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
            }

            // TODO(i18n): mover a strings.xml
            Text(
                text = "Nueva contraseña",
                style = ServifyTextStyle.Title,
                color = MaterialTheme.colorScheme.onSurface
            )

            // TODO(i18n): mover a strings.xml
            Text(
                text = "Crea una contraseña segura para volver a entrar a tu cuenta.",
                style = ServifyTextStyle.SmallRelaxed,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            val visualTransformation =
                if (state.showPassword)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation()

            ServifyTextField(
                value = state.password,
                onValueChange = onPasswordChange,
                label = "Contraseña",
                leadingIcon = Icons.Outlined.Lock,
                trailingIcon =
                    if (state.showPassword)
                        Icons.Outlined.VisibilityOff
                    else
                        Icons.Outlined.Visibility,
                trailingIconDescription = "Mostrar contraseña",
                onTrailingIconClick = onToggleVisibility,
                visualTransformation = visualTransformation,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                singleLine = true,
                isError = state.passwordError != null,
                supportingText = state.passwordError
            )

            ServifyTextField(
                value = state.confirmPassword,
                onValueChange = onConfirmPasswordChange,
                label = "Confirmar contraseña",
                leadingIcon = Icons.Outlined.Lock,
                visualTransformation = visualTransformation,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                singleLine = true,
                isError = state.confirmPasswordError != null,
                supportingText = state.confirmPasswordError
            )

            state.message?.let { message ->
                Text(
                    text = message,
                    style = ServifyTextStyle.Small,
                    color = MaterialTheme.colorScheme.error
                )
            }

            ServifyFilledButton(
                // TODO(i18n): mover a strings.xml
                text = if (state.isLoading) "Guardando…" else "Guardar contraseña",
                icon = Icons.Outlined.Check,
                onClick = onSubmit,
                enabled = state.isValid && !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ResetPasswordSuccessScreen(
    onFinished: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            24.dp,
            Alignment.CenterVertically
        )
    ) {

        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(48.dp)
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // TODO(i18n): mover a strings.xml
            Text(
                text = "Contraseña actualizada",
                style = ServifyTextStyle.Title,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            // TODO(i18n): mover a strings.xml
            Text(
                text = "Ya puedes iniciar sesión con tu nueva contraseña.",
                style = ServifyTextStyle.BodyRelaxed,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        ServifyFilledButton(
            // TODO(i18n): mover a strings.xml
            text = "Ir a iniciar sesión",
            onClick = onFinished,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ResetPasswordInvalidScreen(
    message: String?,
    onFinished: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            24.dp,
            Alignment.CenterVertically
        )
    ) {

        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.errorContainer),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Outlined.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(48.dp)
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // TODO(i18n): mover a strings.xml
            Text(
                text = "Enlace no válido",
                style = ServifyTextStyle.Title,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = message
                    // TODO(i18n): mover a strings.xml
                    ?: "El enlace no es válido o ya venció. Solicita uno nuevo.",
                style = ServifyTextStyle.BodyRelaxed,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        ServifyFilledButton(
            // TODO(i18n): mover a strings.xml
            text = "Volver a iniciar sesión",
            onClick = onFinished,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
