package co.edu.uniquindio.servify.features.home

import co.edu.uniquindio.servify.data.repository.memory.InMemoryCategoryRepository
import co.edu.uniquindio.servify.data.seed.SeedUsers
import co.edu.uniquindio.servify.domain.usecase.category.ObserveCategoriesUseCase
import co.edu.uniquindio.servify.domain.usecase.notification.ObserveNotificationsUseCase
import co.edu.uniquindio.servify.domain.usecase.publication.ToggleInterestUseCase
import co.edu.uniquindio.servify.testutil.MainDispatcherRule
import co.edu.uniquindio.servify.testutil.TestEnvironment
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val env = TestEnvironment()

    private fun viewModel() = HomeViewModel(
        observeServicesUseCase = env.observeServices,
        observeCategoriesUseCase = ObserveCategoriesUseCase(InMemoryCategoryRepository()),
        observeSessionUseCase = env.observeSession,
        observeNotificationsUseCase = ObserveNotificationsUseCase(env.notifications),
        toggleInterestUseCase = ToggleInterestUseCase(env.publications)
    )

    @Test
    fun exposes_the_current_user_avatar_for_the_top_bar() = runBlocking {
        env.loginAs(TestEnvironment.JHONY_EMAIL)
        val avatarUrl = "https://fake.servify.test/servify/avatars/${SeedUsers.JHONY_ID}/foto"
        env.users.update(SeedUsers.JHONY_ID) { it.copy(avatarUrl = avatarUrl) }

        val state = viewModel().uiState.first { !it.isLoading }

        assertEquals(avatarUrl, state.userAvatarUrl)
    }
}
