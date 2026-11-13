package co.edu.uniquindio.servify.e2e

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AuthFlowE2ETest : BaseE2ETest() {

    @Test
    fun onboarding_y_login_de_usuario_abren_el_feed() {
        loginAsUser()

        assertText("Servicios cerca de ti", substring = true)
        assertText("Inicio")
        assertText("Explorar")
        assertText("Publicar")
        assertText("Alertas")
        assertText("Perfil")
    }

    @Test
    fun login_con_credenciales_invalidas_muestra_error() {
        login("jhony.ramirez@gmail.com", "incorrecta")

        assertText("Correo o contraseña incorrectos")
    }

    @Test
    fun registro_de_usuario_nuevo_abre_el_feed() {
        openLoginFromOnboarding()
        clickText("Crear cuenta")
        waitForText("Paso 1 de 2")

        val email = "e2e_${System.currentTimeMillis()}@servify.test"
        typeInField(0, "Usuario")
        typeInField(1, "E2E")
        typeInField(2, email)
        clickText("Continuar")
        waitForText("Seguridad y ubicación")

        typeInField(0, "Servify2026")
        typeInField(1, "Servify2026")
        typeInField(2, "Armenia, Quindío")
        clickText("Acepto los Términos y condiciones", substring = true, scroll = true)
        clickText("Crear cuenta")

        waitForText("Servicios cerca de ti", substring = true)
        assertText("Inicio")
    }

    @Test
    fun recuperacion_de_contrasena_muestra_confirmacion_de_envio() {
        openLoginFromOnboarding()
        clickText("¿Olvidaste tu contraseña?")
        waitForText("Recuperar contraseña")
        waitForTextFields(1)

        typeInField(0, "jhony.ramirez@gmail.com")
        clickText("Enviar enlace")

        assertText("Correo enviado")
        assertText("jhony.ramirez@gmail.com", substring = true)
    }

    @Test
    fun login_de_moderador_abre_el_panel() {
        loginAsModerator()

        assertText("Panel de moderación")
        assertText("moderador@servify.co")
        assertText("Pendientes de revisión", substring = true)
    }
}
