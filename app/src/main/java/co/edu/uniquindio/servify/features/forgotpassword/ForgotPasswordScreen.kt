package co.edu.uniquindio.servify.features.forgotpassword

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

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

        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEFBFF))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {

        // Back
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 4.dp,
                    top = 4.dp
                )
        ) {

            androidx.compose.material3.IconButton(
                onClick = onBack
            ) {

                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Atrás"
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFD6E2FF)),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Filled.LockReset,
                    contentDescription = null,
                    tint = Color(0xFF1A55E3),
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "Recuperar contraseña",
                color = Color(0xFF1B1B1F),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Ingresa el correo de tu cuenta y te enviaremos un enlace para restablecer tu contraseña.",
                color = Color(0xFF45464F),
                fontSize = 14.sp,
                lineHeight = 21.sp
            )

            Spacer(
                modifier = Modifier.height(32.dp)
            )

            ServifyTextField(
                value = state.email,
                onValueChange = viewModel::onEmailChange,
                modifier = Modifier.fillMaxWidth(),
                label = "Correo electrónico",
                leadingIcon = {
                    Icon(Icons.Filled.Email, null)
                },
                singleLine = true,
                isError = state.error != null,
                supportingText = {
                    state.error?.let {
                        Text(it)
                    }
                }
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Button(
                onClick = viewModel::sendRecoveryEmail,
                enabled =
                    state.email.isNotBlank() &&
                            state.error == null,
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
                    imageVector = Icons.Filled.Send,
                    contentDescription = null
                )

                Spacer(
                    modifier = Modifier.size(8.dp)
                )

                Text("Enviar enlace")
            }

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {

                TextButton(
                    onClick = onBackToLogin
                ) {

                    Text(
                        text = "Volver al inicio de sesión",
                        color = Color(0xFF1A55E3)
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
            .background(Color(0xFFFEFBFF))
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(Color(0xFFDCFCE7)),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Filled.MarkEmailRead,
                contentDescription = null,
                tint = Color(0xFF006B53),
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Text(
            text = "Correo enviado",
            color = Color(0xFF1B1B1F),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Enviamos un enlace de recuperación a ",
            color = Color(0xFF45464F),
            fontSize = 16.sp
        )

        Text(
            text = email,
            color = Color(0xFF1B1B1F),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = "Revisa tu bandeja de entrada.",
            color = Color(0xFF45464F),
            fontSize = 16.sp
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Button(
            onClick = onBackToLogin,
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
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = null
            )

            Spacer(
                modifier = Modifier.size(8.dp)
            )

            Text("Volver al inicio")
        }

        TextButton(
            onClick = onResend
        ) {

            Text(
                text = "Reenviar enlace",
                color = Color(0xFF1A55E3)
            )
        }
    }
}