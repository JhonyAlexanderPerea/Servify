package co.edu.uniquindio.servify.features.service

import androidx.lifecycle.SavedStateHandle
import co.edu.uniquindio.servify.data.repository.InMemoryPublicationRepository
import co.edu.uniquindio.servify.data.repository.memory.CommentRepositoryImpl
import co.edu.uniquindio.servify.data.repository.memory.InMemoryGeocodingRepository
import co.edu.uniquindio.servify.data.repository.memory.NotificationRepositoryImpl
import co.edu.uniquindio.servify.data.repository.memory.UserRepositoryImpl
import co.edu.uniquindio.servify.data.seed.SeedPublications
import co.edu.uniquindio.servify.domain.model.Comment
import co.edu.uniquindio.servify.domain.repository.CommentRepository
import co.edu.uniquindio.servify.domain.service.GamificationService
import co.edu.uniquindio.servify.domain.usecase.comment.AddCommentUseCase
import co.edu.uniquindio.servify.domain.usecase.comment.ObserveCommentsUseCase
import co.edu.uniquindio.servify.domain.usecase.location.GetAddressFromLocationUseCase
import co.edu.uniquindio.servify.domain.usecase.notification.GenerateNotificationUseCase
import co.edu.uniquindio.servify.domain.usecase.publication.ObserveServicesUseCase
import co.edu.uniquindio.servify.domain.usecase.publication.ToggleInterestUseCase
import co.edu.uniquindio.servify.domain.usecase.reputation.EvaluateAchievementsUseCase
import co.edu.uniquindio.servify.features.service.detail.AddressState
import co.edu.uniquindio.servify.features.service.detail.CommentSubmitEvent
import co.edu.uniquindio.servify.features.service.detail.ServiceDetailViewModel
import co.edu.uniquindio.servify.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.IOException

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
    private val generateNotificationUseCase = GenerateNotificationUseCase(NotificationRepositoryImpl())
    private val addCommentUseCase = AddCommentUseCase(
        commentRepository,
        publicationRepository,
        evaluateAchievementsUseCase,
        generateNotificationUseCase
    )
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

    @Test
    fun onSubmitCommentFailure_keepsTextStopsSubmittingAndExposesError() = runTest {
        val failingRepository = object : CommentRepository {
            override fun observeCommentsForPublication(publicationId: String): Flow<List<Comment>> = flowOf(emptyList())
            override suspend fun addComment(comment: Comment) {
                throw IOException("Firestore sin conexión")
            }
            override fun snapshot(): List<Comment> = emptyList()
        }
        val failingAddCommentUseCase = AddCommentUseCase(
            failingRepository,
            publicationRepository,
            evaluateAchievementsUseCase,
            generateNotificationUseCase
        )

        val viewModel = ServiceDetailViewModel(
            savedStateHandle = SavedStateHandle(mapOf("serviceId" to "pub_jhony_plomeria")),
            observeServicesUseCase = observeServicesUseCase,
            observeCommentsUseCase = observeCommentsUseCase,
            addCommentUseCase = failingAddCommentUseCase,
            toggleInterestUseCase = toggleInterestUseCase,
            userRepository = userRepository,
            getAddressFromLocationUseCase = getAddressFromLocationUseCase
        )

        viewModel.onCommentTextChanged("Opinión que no debe perderse")
        viewModel.onRatingChanged(4)
        viewModel.onSubmitComment()
        advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertFalse(state.isSubmittingComment)
        assertEquals("Opinión que no debe perderse", state.newCommentText)
        assertEquals(4, state.newCommentRating)
        assertNotNull(state.commentErrorMessage)
        assertTrue(viewModel.commentSubmitEvents.first() is CommentSubmitEvent.Error)
    }
}
