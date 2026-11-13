package co.edu.uniquindio.servify.features.profile.edit

import android.content.res.Configuration
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import co.edu.uniquindio.servify.features.profile.edit.component.DeleteAccountDialog
import co.edu.uniquindio.servify.ui.components.dialog.ConfirmDialog
import co.edu.uniquindio.servify.ui.components.input.ServifyTextField
import co.edu.uniquindio.servify.ui.theme.ServifyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showSaveConfirmationDialog by remember { mutableStateOf(false) }

    // GetContent: el usuario elige de la galería y la app solo recibe permiso de lectura temporal
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onPhotoSelected(it.toString()) }
    }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearMessage()
        }
    }

    if (showSaveConfirmationDialog) {
        ConfirmDialog(
            title = "Confirmar Cambios",
            message = "¿Deseas aplicar y guardar los cambios realizados en tu configuración de perfil?",
            confirmText = "Guardar",
            dismissText = "Cancelar",
            icon = Icons.Default.Settings,
            onConfirm = {
                showSaveConfirmationDialog = false
                viewModel.onSaveProfile()
            },
            onDismiss = { showSaveConfirmationDialog = false }
        )
    }

    if (uiState.showDeleteConfirmationDialog) {
        DeleteAccountDialog(
            password = uiState.deletePassword,
            passwordError = uiState.deletePasswordError,
            isDeleting = uiState.isDeletingAccount,
            onPasswordChange = viewModel::onDeletePasswordChanged,
            onConfirm = viewModel::onConfirmDeleteAccount,
            onDismiss = viewModel::onDismissDeleteConfirmation
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Perfil") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Avatar editable
            AvatarPicker(
                avatarUrl = uiState.avatarUrl,
                isUploading = uiState.isUploadingPhoto,
                onPickImage = { pickImageLauncher.launch("image/*") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Formulario
            ServifyTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChanged,
                label = "Nombre completo",
                leadingIcon = Icons.Default.Person,
                modifier = Modifier.fillMaxWidth()
            )

            ServifyTextField(
                value = uiState.email,
                onValueChange = {},
                label = "Correo electrónico",
                leadingIcon = Icons.Default.Person,
                modifier = Modifier.fillMaxWidth()
            )

            ServifyTextField(
                value = uiState.phone,
                onValueChange = viewModel::onPhoneChanged,
                label = "Teléfono",
                leadingIcon = Icons.Default.Phone,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Guardar Cambios
            Button(
                onClick = { showSaveConfirmationDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = !uiState.isSaving
            ) {
                Text(
                    text = if (uiState.isSaving) "Guardando..." else "Guardar Cambios",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón Eliminar Cuenta
            TextButton(
                onClick = { viewModel.onShowDeleteConfirmation() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(
                    text = "Eliminar Mi Cuenta",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun AvatarPicker(
    avatarUrl: String?,
    isUploading: Boolean,
    onPickImage: () -> Unit
) {
    Box(
        modifier = Modifier.size(120.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clickable(enabled = !isUploading) { onPickImage() },
            contentAlignment = Alignment.Center
        ) {
            if (!avatarUrl.isNullOrBlank()) {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Avatar",
                    modifier = Modifier.size(60.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            if (isUploading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(36.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .clickable(enabled = !isUploading) { onPickImage() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Cambiar foto",
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
fun EditProfileScreenPreview() {
    ServifyTheme(darkTheme = false) {
        EditProfileScreen(
            onBackClick = {},
        )
    }
}

@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun EditProfileScreenPreviewDark() {
    ServifyTheme(darkTheme = true) {
        EditProfileScreen(
            onBackClick = {},
        )
    }
}

@Preview(name = "With Delete Dialog", showBackground = true)
@Composable
fun EditProfileScreenWithDialogPreview() {
    ServifyTheme(darkTheme = false) {
        Column {
            Text("Preview with delete dialog state")
        }
    }
}