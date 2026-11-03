package co.edu.uniquindio.servify.features.profile

import co.edu.uniquindio.servify.data.seed.SeedUsers
import co.edu.uniquindio.servify.domain.model.Badge
import co.edu.uniquindio.servify.testutil.MainDispatcherRule
import co.edu.uniquindio.servify.testutil.TestEnvironment
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val env = TestEnvironment()

    private fun newViewModel() = ProfileViewModel(env.observeSession, env.observeUserProgress, env.observeMyPublications)

    @Test
    fun is_loading_until_there_is_a_session() {
        val vm = newViewModel()

        assertTrue(vm.uiState.value.isLoading)
    }

    @Test
    fun shows_the_real_data_of_the_logged_user() = runBlocking {
        env.loginAs(TestEnvironment.JHONY_EMAIL)
        val state = newViewModel().uiState.value

        assertEquals(false, state.isLoading)
        assertEquals("Jhony Ramírez", state.fullName)
        assertEquals("JR", state.initials)
        assertEquals("Armenia, Quindío", state.city)
        assertEquals("Experto", state.levelLabel)
        assertEquals(1_240, state.points)
    }

    @Test
    fun shows_the_progress_towards_the_next_level() = runBlocking {
        env.loginAs(TestEnvironment.JHONY_EMAIL)
        val state = newViewModel().uiState.value

        // Experto: 600..1.500 → con 1.240 puntos faltan 260 para Maestro.
        assertEquals("Maestro", state.nextLevelLabel)
        assertEquals(260, state.pointsToNext)
        assertEquals((1_240 - 600) / (1_500 - 600).toFloat(), state.levelFraction, 0.0001f)
    }

    @Test
    fun the_top_level_has_no_next_level() = runBlocking {
        env.loginAs("maria.rios@servify.demo") // 1.850 puntos = Maestro
        val state = newViewModel().uiState.value

        assertEquals("Maestro", state.levelLabel)
        assertEquals(null, state.nextLevelLabel)
        assertEquals(1f, state.levelFraction, 0.0001f)
    }

    @Test
    fun counts_published_and_finalized_from_the_users_publications() = runBlocking {
        env.loginAs(TestEnvironment.JHONY_EMAIL)
        val state = newViewModel().uiState.value

        // Verificadas alguna vez: plomería, electricidad y computadores (finalizada). Pendiente y rechazada no cuentan.
        assertEquals(3, state.publishedCount)
        assertEquals(1, state.finalizedCount)
    }

    @Test
    fun lists_the_unlocked_badges() = runBlocking {
        env.loginAs(TestEnvironment.JHONY_EMAIL)
        val state = newViewModel().uiState.value

        assertTrue(Badge.CIEN_PUNTOS in state.unlockedBadges)
        assertTrue(Badge.USUARIO_CONFIABLE in state.unlockedBadges)
        assertEquals(Badge.entries.size, state.totalBadges)
    }

    @Test
    fun updates_by_itself_when_a_publication_is_finalized() = runBlocking {
        env.loginAs(TestEnvironment.JHONY_EMAIL)
        val vm = newViewModel()

        env.markCompleted(SeedUsers.JHONY_ID, "pub_jhony_plomeria")

        assertEquals(2, vm.uiState.value.finalizedCount)
    }

    @Test
    fun updates_by_itself_when_the_user_earns_points() = runBlocking {
        env.loginAs("valentina.ospina@servify.demo")
        val vm = newViewModel()
        assertEquals("Profesional", vm.uiState.value.levelLabel) // 380 puntos

        env.gamification.award(SeedUsers.VALENTINA_ID, co.edu.uniquindio.servify.domain.rules.PointEvent.PUBLICATION_VERIFIED)

        assertEquals(410, vm.uiState.value.points)
    }
}
