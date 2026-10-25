package co.edu.uniquindio.servify.features.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.servify.domain.usecase.auth.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Acciones de sesión que necesita el grafo de navegación (cerrar sesión desde Perfil o desde el panel
 * de moderación). Así la navegación no toca casos de uso directamente.
 */
@HiltViewModel
class SessionViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    fun logout() {
        viewModelScope.launch { logoutUseCase() }
    }
}
