package co.edu.uniquindio.servify.features.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import co.edu.uniquindio.servify.ui.components.input.ServifyTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

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
            .background(Color(0xFFFEFBFF))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {

        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 8.dp,
                    vertical = 8.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = {
                    if (state.step == 1) {
                        onBack()
                    } else {
                        viewModel.previousStep()
                    }
                }
            ) {

                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Atrás"
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Paso ${state.step} de 2",
                    color = Color(0xFF45464F),
                    fontSize = 12.sp
                )

                LinearProgressIndicator(
                    progress = {
                        state.step / 2f
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = Color(0xFF1A55E3),
                    trackColor = Color(0xFFE3E1EC)
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(
                    horizontal = 24.dp
                ),
            verticalArrangement = Arrangement.Top
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
        color = Color(0xFF1B1B1F),
        fontSize = 24.sp,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
    )

    Text(
        text = "Únete a Servify y encuentra servicios confiables",
        color = Color(0xFF45464F),
        fontSize = 14.sp,
        modifier = Modifier.padding(top = 4.dp)
    )

    Spacer(
        modifier = Modifier.height(24.dp)
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        ServifyTextField(
            value = state.name,
            onValueChange = viewModel::onNameChange,
            modifier = Modifier.fillMaxWidth(),
            label = "Nombre",
            leadingIcon = {
                Icon(Icons.Filled.Person, null)
            },
            singleLine = true
        )

        ServifyTextField(
            value = state.lastName,
            onValueChange = viewModel::onLastNameChange,
            modifier = Modifier.fillMaxWidth(),
            label = "Apellido",
            leadingIcon = {
                Icon(Icons.Filled.Person, null)
            },
            singleLine = true
        )

        ServifyTextField(
            value = state.email,
            onValueChange = viewModel::onEmailChange,
            modifier = Modifier.fillMaxWidth(),
            label = "Correo electrónico",
            leadingIcon = {
                Icon(Icons.Filled.Email, null)
            },
            singleLine = true
        )

        Button(
            onClick = viewModel::nextStep,
            enabled = state.firstStepValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1A55E3),
                contentColor = Color.White
            )
        ) {

            Text("Continuar")

            Spacer(
                modifier = Modifier.size(8.dp)
            )

            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun RegisterStepTwo(
    state: RegisterUiState,
    viewModel: RegisterViewModel,
    onRegisterSuccess: () -> Unit
) {

    Spacer(
        modifier = Modifier.height(16.dp)
    )

    Text(
        text = "Seguridad y ubicación",
        color = Color(0xFF1B1B1F),
        fontSize = 24.sp,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
    )

    Text(
        text = "Casi listo. Ingresa tu contraseña y ciudad",
        color = Color(0xFF45464F),
        fontSize = 14.sp,
        modifier = Modifier.padding(top = 4.dp)
    )

    Spacer(
        modifier = Modifier.height(24.dp)
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        ServifyTextField(
            value = state.password,
            onValueChange = viewModel::onPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            label = "Contraseña",
            leadingIcon = {
                Icon(Icons.Filled.Lock, null)
            },
            supportingText = {
                Text("Mínimo 8 caracteres")
            },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        ServifyTextField(
            value = state.confirmPassword,
            onValueChange = viewModel::onConfirmPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            label = "Confirmar contraseña",
            leadingIcon = {
                Icon(Icons.Filled.Lock, null)
            },
            isError =
                state.confirmPassword.isNotEmpty() &&
                        state.password != state.confirmPassword,
            supportingText = {

                if (
                    state.confirmPassword.isNotEmpty() &&
                    state.password != state.confirmPassword
                ) {
                    Text("Las contraseñas no coinciden")
                }
            },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        ServifyTextField(
            value = state.city,
            onValueChange = viewModel::onCityChange,
            modifier = Modifier.fillMaxWidth(),
            label = "Ciudad",
            leadingIcon = {
                Icon(Icons.Filled.LocationOn, null)
            },
            supportingText = {
                Text("Ej: Armenia, Quindío")
            },
            singleLine = true
        )

        Row(
            verticalAlignment = Alignment.Top
        ) {

            Checkbox(
                checked = state.termsAccepted,
                onCheckedChange = {
                    viewModel.toggleTerms()
                }
            )

            Text(
                text = "Acepto los Términos y condiciones y la Política de privacidad",
                color = Color(0xFF45464F),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        Button(
            onClick = {
                viewModel.register()
                onRegisterSuccess()
            },
            enabled = state.secondStepValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1A55E3),
                contentColor = Color.White
            )
        ) {

            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null
            )

            Spacer(
                modifier = Modifier.size(8.dp)
            )

            Text("Crear cuenta")
        }
    }
}