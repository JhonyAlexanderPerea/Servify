package co.edu.uniquindio.servify.features.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.uniquindio.servify.ui.components.button.ServifyFilledButton
import co.edu.uniquindio.servify.ui.components.button.ServifyIconButton
import co.edu.uniquindio.servify.ui.components.feedback.ServifyProgressBar
import co.edu.uniquindio.servify.ui.components.input.ServifyCheckbox
import co.edu.uniquindio.servify.ui.components.input.ServifyTextField
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurface
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurfaceVariant
import co.edu.uniquindio.servify.ui.theme.ServifyPrimary
import co.edu.uniquindio.servify.ui.theme.ServifySurface
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle

@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: RegisterViewModel = viewModel()
) {

    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ServifySurface)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            ServifyIconButton(
                icon = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Atrás",
                onClick = {
                    if (state.step == 1) {
                        onBack()
                    } else {
                        viewModel.previousStep()
                    }
                }
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Paso ${state.step} de 2",
                    style = ServifyTextStyle.Caption,
                    color = ServifyOnSurfaceVariant
                )

                ServifyProgressBar(
                    progress = state.step / 2f
                )
            }
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

            if (state.step == 1) {

                RegisterStepOne(
                    state = state,
                    viewModel = viewModel
                )

            } else {

                RegisterStepTwo(
                    state = state,
                    viewModel = viewModel,
                    onRegisterSuccess = onRegisterSuccess
                )
            }
        }
    }
}

@Composable
private fun RegisterStepOne(
    state: RegisterUiState,
    viewModel: RegisterViewModel
) {

    Spacer(
        modifier = Modifier.height(16.dp)
    )

    Text(
        text = "Crear cuenta",
        style = ServifyTextStyle.Title,
        color = ServifyOnSurface
    )

    Spacer(
        modifier = Modifier.height(4.dp)
    )

    Text(
        text = "Únete a Servify y encuentra servicios confiables",
        style = ServifyTextStyle.Small,
        color = ServifyOnSurfaceVariant
    )

    Spacer(
        modifier = Modifier.height(24.dp)
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        ServifyTextField(
            value = state.name,
            onValueChange = viewModel::onNameChange,
            label = "Nombre",
            leadingIcon = Icons.Outlined.Person,
            singleLine = true
        )

        ServifyTextField(
            value = state.lastName,
            onValueChange = viewModel::onLastNameChange,
            label = "Apellido",
            leadingIcon = Icons.Outlined.Person,
            singleLine = true
        )

        ServifyTextField(
            value = state.email,
            onValueChange = viewModel::onEmailChange,
            label = "Correo electrónico",
            leadingIcon = Icons.Outlined.Email,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            singleLine = true
        )

        ServifyFilledButton(
            text = "Continuar",
            icon = Icons.AutoMirrored.Outlined.ArrowForward,
            onClick = viewModel::nextStep,
            enabled = state.firstStepValid,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun RegisterStepTwo(
    state: RegisterUiState,
    viewModel: RegisterViewModel,
    onRegisterSuccess: () -> Unit
) {

    val passwordsMismatch =
        state.confirmPassword.isNotEmpty() &&
                state.password != state.confirmPassword

    Spacer(
        modifier = Modifier.height(16.dp)
    )

    Text(
        text = "Seguridad y ubicación",
        style = ServifyTextStyle.Title,
        color = ServifyOnSurface
    )

    Spacer(
        modifier = Modifier.height(4.dp)
    )

    Text(
        text = "Casi listo. Ingresa tu contraseña y ciudad",
        style = ServifyTextStyle.Small,
        color = ServifyOnSurfaceVariant
    )

    Spacer(
        modifier = Modifier.height(24.dp)
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        ServifyTextField(
            value = state.password,
            onValueChange = viewModel::onPasswordChange,
            label = "Contraseña",
            leadingIcon = Icons.Outlined.Lock,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            singleLine = true,
            supportingText = "Mínimo 8 caracteres"
        )

        ServifyTextField(
            value = state.confirmPassword,
            onValueChange = viewModel::onConfirmPasswordChange,
            label = "Confirmar contraseña",
            leadingIcon = Icons.Outlined.Lock,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            singleLine = true,
            isError = passwordsMismatch,
            supportingText =
                if (passwordsMismatch) "Las contraseñas no coinciden"
                else null
        )

        ServifyTextField(
            value = state.city,
            onValueChange = viewModel::onCityChange,
            label = "Ciudad",
            leadingIcon = Icons.Outlined.LocationOn,
            singleLine = true,
            supportingText = "Ej: Armenia, Quindío"
        )

        Row(
            modifier = Modifier
                .padding(top = 8.dp)
                .toggleable(
                    value = state.termsAccepted,
                    role = Role.Checkbox,
                    onValueChange = { viewModel.toggleTerms() }
                ),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            ServifyCheckbox(
                checked = state.termsAccepted,
                modifier = Modifier.padding(top = 2.dp)
            )

            Text(
                text = buildAnnotatedString {

                    append("Acepto los ")

                    withStyle(
                        SpanStyle(
                            color = ServifyPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    ) {
                        append("Términos y condiciones")
                    }

                    append(" y la ")

                    withStyle(
                        SpanStyle(
                            color = ServifyPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    ) {
                        append("Política de privacidad")
                    }
                },
                style = ServifyTextStyle.SmallRelaxed,
                color = ServifyOnSurfaceVariant
            )
        }

        ServifyFilledButton(
            text = "Crear cuenta",
            icon = Icons.Outlined.Check,
            onClick = {
                viewModel.register()
                onRegisterSuccess()
            },
            enabled = state.secondStepValid,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
