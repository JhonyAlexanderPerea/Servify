package co.edu.uniquindio.servify.navigation

import co.edu.uniquindio.servify.domain.model.UserRole
import co.edu.uniquindio.servify.testutil.MainDispatcherRule
import co.edu.uniquindio.servify.testutil.TestEnvironment
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SessionViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val env = TestEnvironment()

    private fun newViewModel() = SessionViewModel(env.observeUserSession)

    private fun awaitState(
        viewModel: SessionViewModel,
        predicate: (SessionState) -> Boolean
    ): SessionState = runBlocking {
        withTimeout(5_000) { viewModel.sessionState.first(predicate) }
    }

    @Test
    fun without_a_session_it_is_not_authenticated() {
        val state = awaitState(newViewModel()) { it !is SessionState.Loading }

        assertTrue(state is SessionState.NotAuthenticated)
    }

    @Test
    fun after_saving_the_session_it_is_authenticated_with_the_role() {
        val viewModel = newViewModel()
        awaitState(viewModel) { it !is SessionState.Loading }

        runBlocking { env.session.saveSession("user_1", UserRole.MODERATOR) }

        val state = awaitState(viewModel) { it is SessionState.Authenticated } as SessionState.Authenticated
        assertEquals("user_1", state.session.userId)
        assertEquals(UserRole.MODERATOR, state.session.role)
    }

    @Test
    fun after_clearing_the_session_it_is_not_authenticated_again() {
        val viewModel = newViewModel()
        runBlocking { env.session.saveSession("user_1", UserRole.USER) }
        awaitState(viewModel) { it is SessionState.Authenticated }

        runBlocking { env.session.clearSession() }

        val state = awaitState(viewModel) { it is SessionState.NotAuthenticated }
        assertTrue(state is SessionState.NotAuthenticated)
    }
}
