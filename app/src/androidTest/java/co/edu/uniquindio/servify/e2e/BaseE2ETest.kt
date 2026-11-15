package co.edu.uniquindio.servify.e2e

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import co.edu.uniquindio.servify.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
abstract class BaseE2ETest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun injectTest() {
        hiltRule.inject()
    }

    protected fun hasText(text: String, substring: Boolean = false): Boolean =
        composeRule.onAllNodesWithText(text, substring = substring)
            .fetchSemanticsNodes().isNotEmpty()

    protected fun waitForText(text: String, timeoutMillis: Long = 15_000, substring: Boolean = false) {
        composeRule.waitUntil(timeoutMillis) { hasText(text, substring) }
    }

    protected fun waitForAnyText(vararg texts: String, timeoutMillis: Long = 15_000) {
        composeRule.waitUntil(timeoutMillis) { texts.any { hasText(it) } }
    }

    protected fun clickText(text: String, substring: Boolean = false, scroll: Boolean = false) {
        waitForText(text, substring = substring)
        val node = composeRule.onNodeWithText(text, substring = substring)
        if (scroll) node.performScrollTo()
        node.performClick()
    }

    protected fun assertText(text: String, substring: Boolean = false) {
        waitForText(text, substring = substring)
        composeRule.onNodeWithText(text, substring = substring).assertIsDisplayed()
    }

    protected fun typeInField(index: Int, text: String) {
        composeRule.onAllNodes(hasSetTextAction())[index].performTextReplacement(text)
    }

    /** Espera a que terminen las transiciones y solo queden los campos de la pantalla actual. */
    protected fun waitForTextFields(count: Int, timeoutMillis: Long = 5_000) {
        composeRule.waitUntil(timeoutMillis) {
            composeRule.onAllNodes(hasSetTextAction()).fetchSemanticsNodes().size == count
        }
    }

    protected fun openLoginFromOnboarding() {
        clickText("Omitir")
        waitForText("Bienvenido de nuevo")
    }

    protected fun login(email: String, password: String) {
        openLoginFromOnboarding()
        typeInField(0, email)
        typeInField(1, password)
        clickText("Iniciar sesión")
    }

    protected fun loginAsUser() {
        login("jhony.ramirez@gmail.com", "12345678")
        waitForText("Servicios cerca de ti", substring = true)
    }

    protected fun loginAsModerator() {
        login("moderador@servify.co", "Moderador2026")
        waitForText("Panel de moderación")
    }
}
