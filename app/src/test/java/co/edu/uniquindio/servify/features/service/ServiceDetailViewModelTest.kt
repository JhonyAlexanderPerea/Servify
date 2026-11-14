package co.edu.uniquindio.servify.features.service

import androidx.lifecycle.SavedStateHandle
import co.edu.uniquindio.servify.data.repository.InMemoryPublicationRepository
import co.edu.uniquindio.servify.data.repository.memory.CommentRepositoryImpl
import co.edu.uniquindio.servify.data.repository.memory.InMemoryGeocodingRepository
import co.edu.uniquindio.servify.data.repository.memory.UserRepositoryImpl
import co.edu.uniquindio.servify.data.seed.SeedPublications
import co.edu.uniquindio.servify.domain.service.GamificationService
import co.edu.uniquindio.servify.domain.usecase.comment.AddCommentUseCase
import co.edu.uniquindio.servify.domain.usecase.comment.ObserveCommentsUseCase
import co.edu.uniquindio.servify.domain.usecase.location.GetAddressFromLocationUseCase
import co.edu.uniquindio.servify.domain.usecase.publication.ObserveServicesUseCase
import co.edu.uniquindio.servify.domain.usecase.publication.ToggleInterestUseCase
import co.edu.uniquindio.servify.domain.usecase.reputation.EvaluateAchievementsUseCase
import co.edu.uniquindio.servify.features.service.detail.AddressState
import co.edu.uniquindio.servify.features.service.detail.ServiceDetailViewModel
import co.edu.uniquindio.servify.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ServiceDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val publicationRepository = InMemoryPublicationRepository(SeedPublications.publications)
    private val commentRepository = CommentRepositoryImpl()
    private val userRepository = UserRepositoryImpl()
    private val geocodingRepository = InMemoryGeocodingRepository()
    private val gamificationService = GamificationService(userRepository, publicationRepository::snapshot, { emptyList() }) { _, _ -> }

    private val observeServicesUseCase = ObserveServicesUseCase(publicationRepository)
    private val observeCommentsUseCase = ObserveCommentsUseCase(commentRepository)
    private val evaluateAchievementsUseCase = EvaluateAchievementsUseCase(gamificationService)
    private val addCommentUseCase = AddCommentUseCase(commentRepository, publicationRepository, evaluateAchievementsUseCase)
    private val toggleInterestUseCase = ToggleInterestUseCase(publicationRepository)
    private val getAddressFromLocationUseCase = GetAddressFromLocationUseCase(geocodingRepository)

    @Test
    fun loadServiceDetail_loadsServiceAndAddress() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("serviceId" to "pub_jhony_plomeria"))
        val viewModel = ServiceDetailViewModel(
            savedStateHandle = savedStateHandle,
            observeServicesUseCase = observeServicesUseCase,
            observeCommentsUseCase = observeCommentsUseCase,
            addCommentUseCase = addCommentUseCase,
            toggleInterestUseCase = toggleInterestUseCase,
            userRepository = userRepository,
            getAddressFromLocationUseCase = getAddressFromLocationUseCase
        )

        advanceUntilIdle()

        val state = viewModel.uiState.first { it.service != null }
        assertNotNull(state.service)
        assertEquals("pub_jhony_plomeria", state.service?.id)
        assertEquals(AddressState.Success("Dirección de prueba"), state.addressState)
    }
}
