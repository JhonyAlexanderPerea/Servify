package co.edu.uniquindio.servify.features.forgotpassword

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.uniquindio.servify.ui.components.button.ServifyFilledButton
import co.edu.uniquindio.servify.ui.components.button.ServifyIconButton
import co.edu.uniquindio.servify.ui.components.button.ServifyTextButton
import co.edu.uniquindio.servify.ui.components.input.ServifyTextField
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurface
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurfaceVariant
import co.edu.uniquindio.servify.ui.theme.ServifyPrimary
import co.edu.uniquindio.servify.ui.theme.ServifyPrimaryContainer
import co.edu.uniquindio.servify.ui.theme.ServifySuccessContainer
import co.edu.uniquindio.servify.ui.theme.ServifySurface
import co.edu.uniquindio.servify.ui.theme.ServifyTertiary
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle

@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    onBackToLogin: () -> Unit,
    viewModel: ForgotPasswordViewModel = viewModel()
) {

    val state by viewModel.uiState.collectAsState()

    if (state.sent) {

        RecoverySentScreen(
            email = state.email,
            onBackToLogin = onBackToLogin,
            onResend = viewModel::resend
        )

    } else {

        RecoveryFormScreen(
            email = state.email,
            error = state.error,
            onEmailChange = viewModel::onEmailChange,
            onSend = viewModel::sendRecoveryEmail,
            onBack = onBack,
            onBackToLogin = onBackToLogin
        )
    }
}

@Composable
private fun RecoveryFormScreen(
    email: String,
    error: String?,
    onEmailChange: (String) -> Unit,
    onSend: () -> Unit,
    onBack: () -> Unit,
    onBackToLogin: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ServifySurface)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {

        // Back (px-2 py-1)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 8.dp,
                    vertical = 4.dp
                )
        ) {

            ServifyIconButton(
                icon = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Atrás",
                onClick = onBack
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    bottom = 32.dp
                )
        ) {

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(ServifyPrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Filled.LockReset,
                        contentDescription = null,
                        tint = ServifyPrimary,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Text(
                    text = "Recuperar contraseña",
                    style = ServifyTextStyle.Title,
                    color = ServifyOnSurface
                )

                Text(
                    text = "Ingresa el correo de tu cuenta y te enviaremos un enlace para restablecer tu contraseña.",
                    style = ServifyTextStyle.SmallRelaxed,
                    color = ServifyOnSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(
                modifier = Modifier.height(32.dp)
            )

            // Form (gap-5)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                ServifyTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    label = "Correo electrónico",
                    leadingIcon = Icons.Outlined.Email,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    ),
                    singleLine = true,
                    isError = error != null,
                    supportingText = error
                )

                ServifyFilledButton(
                    text = "Enviar enlace",
                    icon = Icons.AutoMirrored.Outlined.Send,
                    onClick = onSend,
                    enabled = email.isNotBlank() && error == null,
                    modifier = Modifier.fillMaxWidth()
                )

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {

                    ServifyTextButton(
                        text = "Volver al inicio de sesión",
                        onClick = onBackToLogin
                    )
                }
            }
        }
    }
}

@Composable
private fun RecoverySentScreen(
    email: String,
    onBackToLogin: () -> Unit,
    onResend: () -> Unit
) {


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ServifySurface)
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
                .background(ServifySuccessContainer),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Filled.MarkEmailRead,
                contentDescription = null,
                tint = ServifyTertiary,
                modifier = Modifier.size(48.dp)
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Correo enviado",
                style = ServifyTextStyle.Title,
                color = ServifyOnSurface
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = buildAnnotatedString {

                    append("Enviamos un enlace de recuperación a ")

                    withStyle(
                        SpanStyle(
                            color = ServifyOnSurface,
                            fontWeight = FontWeight.Medium
                        )
                    ) {
                        append(email)
                    }

                    append(". Revisa tu bandeja de entrada.")
                },
                style = ServifyTextStyle.BodyRelaxed,
                color = ServifyOnSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        ServifyFilledButton(
            text = "Volver al inicio",
            icon = Icons.AutoMirrored.Outlined.ArrowBack,
            onClick = onBackToLogin
        )

        ServifyTextButton(
            text = "Reenviar enlace",
            onClick = onResend
        )
    }
}
