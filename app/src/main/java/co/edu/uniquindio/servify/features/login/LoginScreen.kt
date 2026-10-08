package co.edu.uniquindio.servify.features.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.uniquindio.servify.ui.components.button.ServifyFilledButton
import co.edu.uniquindio.servify.ui.components.button.ServifyTextButton
import co.edu.uniquindio.servify.ui.components.icons.ServifyIcons
import co.edu.uniquindio.servify.ui.components.input.ServifyCheckbox
import co.edu.uniquindio.servify.ui.components.input.ServifyTextField
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurface
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurfaceVariant
import co.edu.uniquindio.servify.ui.theme.ServifyOutlineVariant
import co.edu.uniquindio.servify.ui.theme.ServifyPrimary
import co.edu.uniquindio.servify.ui.theme.ServifySurface
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle
import androidx.compose.foundation.Image

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {

    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ServifySurface)
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

            Image(
                painter = androidx.compose.ui.res.painterResource(id = co.edu.uniquindio.servify.R.drawable.logo),
                contentDescription = "Servify Logo",
                modifier = Modifier.size(64.dp)
            )

            Text(
                text = "Bienvenido de nuevo",
                style = ServifyTextStyle.Title,
                color = ServifyOnSurface
            )

            Text(
                text = "Ingresa a tu cuenta de Servify",
                style = ServifyTextStyle.Small,
                color = ServifyOnSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {


            ServifyTextField(
                value = state.email,
                onValueChange = viewModel::onEmailChange,
                label = "Correo electrónico",
                leadingIcon = Icons.Outlined.Email,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                singleLine = true,
                isError = state.emailError != null,
                supportingText = state.emailError
            )

            ServifyTextField(
                value = state.password,
                onValueChange = viewModel::onPasswordChange,
                label = "Contraseña",
                leadingIcon = Icons.Outlined.Lock,
                trailingIcon =
                    if (state.showPassword)
                        Icons.Outlined.VisibilityOff
                    else
                        Icons.Outlined.Visibility,
                trailingIconDescription = "Mostrar contraseña",
                onTrailingIconClick = viewModel::togglePasswordVisibility,
                visualTransformation =
                    if (state.showPassword)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                singleLine = true,
                isError = state.passwordError != null,
                supportingText = state.passwordError
            )

            Row(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .toggleable(
                        value = state.rememberMe,
                        role = Role.Checkbox,
                        onValueChange = { viewModel.toggleRememberMe() }
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                ServifyCheckbox(
                    checked = state.rememberMe
                )

                Text(
                    text = "Recordarme",
                    style = ServifyTextStyle.Small,
                    color = ServifyOnSurfaceVariant
                )
            }

            ServifyFilledButton(
                text = "Iniciar sesión",
                onClick = {
                    viewModel.login()
                    onLoginSuccess()
                },
                enabled = state.isValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {

                ServifyTextButton(
                    text = "¿Olvidaste tu contraseña?",
                    onClick = onForgotPasswordClick
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(ServifyOutlineVariant)
                )

                Text(
                    text = "o continúa con",
                    style = ServifyTextStyle.Caption,
                    color = ServifyOnSurfaceVariant
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(ServifyOutlineVariant)
                )
            }

            GoogleButton(
                onClick = onLoginSuccess
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(
                    4.dp,
                    Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "¿No tienes cuenta?",
                    style = ServifyTextStyle.Small,
                    color = ServifyOnSurfaceVariant
                )

                ServifyTextButton(
                    text = "Crear cuenta",
                    onClick = onRegisterClick
                )
            }
        }
    }
}


@Composable
private fun GoogleButton(
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(CircleShape)
            .border(
                width = 1.dp,
                color = ServifyOutlineVariant,
                shape = CircleShape
            )
            .clickable(
                role = Role.Button,
                onClick = onClick
            ),
        horizontalArrangement = Arrangement.spacedBy(
            12.dp,
            Alignment.CenterHorizontally
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = ServifyIcons.GoogleLogo,
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(18.dp)
        )

        Text(
            text = "Continuar con Google",
            style = ServifyTextStyle.SmallMedium,
            color = ServifyOnSurface
        )
    }
}
