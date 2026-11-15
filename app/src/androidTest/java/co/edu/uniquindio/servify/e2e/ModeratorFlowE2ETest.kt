package co.edu.uniquindio.servify.e2e

import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ModeratorFlowE2ETest : BaseE2ETest() {

    @Test
    fun moderador_verifica_una_publicacion_pendiente() {
        loginAsModerator()
        assertText("Pendientes de revisión (2)")

        composeRule.onAllNodesWithText("Revisar")[0].performClick()
        waitForText("Revisar publicación")
        assertText("Verificar publicación")
        assertText("Rechazar publicación")

        clickText("Verificar publicación")
        waitForText("¿Deseas continuar?", substring = true)
        clickText("Verificar")

        waitForText("Panel de moderación")
        assertText("Pendientes de revisión (1)")
    }

    @Test
    fun moderador_rechaza_una_publicacion_con_motivo() {
        loginAsModerator()
        assertText("Pendientes de revisión (2)")

        composeRule.onAllNodesWithText("Revisar")[0].performClick()
        waitForText("Revisar publicación")

        clickText("Rechazar publicación")
        waitForText("Motivo del rechazo (obligatorio)")
        clickText("Información insuficiente")
        clickText("Confirmar rechazo")

        waitForText("Panel de moderación")
        assertText("Pendientes de revisión (1)")
    }

    @Test
    fun moderador_cierra_sesion_y_vuelve_al_login() {
        loginAsModerator()

        composeRule.onNodeWithContentDescription("Cerrar sesión").performClick()
        waitForText("¿Estás seguro de que deseas cerrar la sesión de moderador en Servify?")
        // El diálogo repite "Cerrar sesión" en el título y en el botón: el botón es el último.
        composeRule.onAllNodesWithText("Cerrar sesión")[1].performClick()

        waitForText("Omitir")
        clickText("Omitir")
        waitForText("Bienvenido de nuevo")
        assertText("Iniciar sesión")
    }
}
