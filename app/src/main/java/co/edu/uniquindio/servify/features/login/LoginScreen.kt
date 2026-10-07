package co.edu.uniquindio.servify.features.login

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import co.edu.uniquindio.servify.ui.components.input.ServifyTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

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
            .background(Color(0xFFFEFBFF))
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp)
    ) {

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        // Header
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1A55E3)),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Filled.Shield,
                    contentDescription = "Servify",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "Bienvenido de nuevo",
                color = Color(0xFF1B1B1F),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "Ingresa a tu cuenta de Servify",
                color = Color(0xFF45464F),
                fontSize = 14.sp
            )
        }

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Email
            ServifyTextField(
                value = state.email,
                onValueChange = viewModel::onEmailChange,
                modifier = Modifier.fillMaxWidth(),
                label = "Correo electrónico",
                leadingIcon = {
                    Icon(
                        Icons.Filled.Email,
                        contentDescription = null
                    )
                },
                singleLine = true,
                isError = state.emailError != null,
                supportingText = {
                    state.emailError?.let {
                        Text(it)
                    }
                }
            )

            // Password
            ServifyTextField(
                value = state.password,
                onValueChange = viewModel::onPasswordChange,
                modifier = Modifier.fillMaxWidth(),
                label = "Contraseña",
                leadingIcon = {
                    Icon(
                        Icons.Filled.Lock,
                        contentDescription = null
                    )
                },
                trailingIcon = {

                    IconButton(
                        onClick = viewModel::togglePasswordVisibility
                    ) {

                        Icon(
                            imageVector =
                                if (state.showPassword)
                                    Icons.Filled.VisibilityOff
                                else
                                    Icons.Filled.Visibility,
                            contentDescription = "Mostrar contraseña"
                        )
                    }
                },
                visualTransformation =
                    if (state.showPassword)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                singleLine = true,
                isError = state.passwordError != null,
                supportingText = {
                    state.passwordError?.let {
                        Text(it)
                    }
                }
            )

            // Remember
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = viewModel::toggleRememberMe,
                    modifier = Modifier.size(24.dp)
                ) {

                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (state.rememberMe)
                                    Color(0xFF1A55E3)
                                else
                                    Color.Transparent
                            )
                    ) {

                        if (state.rememberMe) {

                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.size(8.dp)
                )

                Text(
                    text = "Recordarme",
                    color = Color(0xFF45464F),
                    fontSize = 14.sp
                )
            }

            // Login
            Button(
                onClick = {
                    viewModel.login()
                    onLoginSuccess()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = state.isValid,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1A55E3),
                    contentColor = Color.White
                )
            ) {

                Text(
                    text = "Iniciar sesión"
                )
            }

            // Forgot password
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {

                TextButton(
                    onClick = onForgotPasswordClick
                ) {

                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        color = Color(0xFF1A55E3)
                    )
                }
            }

            // Divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(Color(0xFFC8C5D0))
                )

                Text(
                    text = "  o continúa con  ",
                    color = Color(0xFF45464F),
                    fontSize = 12.sp
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(Color(0xFFC8C5D0))
                )
            }

            // Google
            OutlinedButton(
                onClick = onLoginSuccess,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50)
            ) {

                Text(
                    text = "G",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4285F4),
                    fontSize = 18.sp
                )

                Spacer(
                    modifier = Modifier.size(10.dp)
                )

                Text(
                    text = "Continuar con Google",
                    color = Color(0xFF1B1B1F)
                )
            }

            // Register
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "¿No tienes cuenta?",
                    color = Color(0xFF45464F),
                    fontSize = 14.sp
                )

                TextButton(
                    onClick = onRegisterClick
                ) {

                    Text(
                        text = "Crear cuenta",
                        color = Color(0xFF1A55E3)
                    )
                }
            }
        }
    }
}