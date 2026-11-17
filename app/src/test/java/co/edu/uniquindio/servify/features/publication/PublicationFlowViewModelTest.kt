package co.edu.uniquindio.servify.features.publication

import androidx.lifecycle.SavedStateHandle
import co.edu.uniquindio.servify.data.repository.memory.InMemoryAiClassifierRepository
import co.edu.uniquindio.servify.data.repository.memory.InMemoryGeocodingRepository
import co.edu.uniquindio.servify.data.repository.memory.InMemoryImageRepository
import co.edu.uniquindio.servify.data.remote.connectivity.ConnectivityMonitor
import co.edu.uniquindio.servify.domain.model.AiClassification
import co.edu.uniquindio.servify.domain.model.CatalogCategory
import co.edu.uniquindio.servify.domain.repository.AiClassifierRepository
import co.edu.uniquindio.servify.domain.rules.KeywordCategoryRules
import co.edu.uniquindio.servify.domain.usecase.publication.ClassifyServiceWithAiUseCase
import co.edu.uniquindio.servify.domain.usecase.publication.CreatePublicationUseCase
import co.edu.uniquindio.servify.domain.usecase.publication.GetPublicationByIdUseCase
import co.edu.uniquindio.servify.domain.usecase.publication.UpdatePublicationUseCase
import co.edu.uniquindio.servify.domain.usecase.user.GetCurrentUserUseCase
import co.edu.uniquindio.servify.features.publication.create.PublicationFlowViewModel
import co.edu.uniquindio.servify.testutil.TestEnvironment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PublicationFlowViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val env = TestEnvironment()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // Fake implementation of ConnectivityMonitor for testing (no context needed)
    private class FakeConnectivityMonitor(private var state: Boolean = true) : ConnectivityMonitor(context = null) {
        private val _isConnected = MutableStateFlow(state)
        override val isConnected = _isConnected.asStateFlow()
        override suspend fun checkConnectivity(): Boolean = _isConnected.value
        fun setConnected(connected: Boolean) {
            state = connected
            _isConnected.value = connected
        }
    }

    private val mockConnectivityMonitor = FakeConnectivityMonitor(true)

    private fun newViewModel(
        classifier: AiClassifierRepository = InMemoryAiClassifierRepository()
    ) = PublicationFlowViewModel(
        savedStateHandle = SavedStateHandle(),
        getPublicationByIdUseCase = GetPublicationByIdUseCase(env.publications),
        createPublicationUseCase = CreatePublicationUseCase(env.publications, env.generateNotification),
        updatePublicationUseCase = UpdatePublicationUseCase(env.publications),
        getCurrentUserUseCase = GetCurrentUserUseCase(env.users),
        awardPointsUseCase = env.awardPoints,
        classifyServiceWithAiUseCase = ClassifyServiceWithAiUseCase(classifier),
        geocodingRepository = InMemoryGeocodingRepository(),
        imageRepository = InMemoryImageRepository(),
        connectivityMonitor = mockConnectivityMonitor
    )

    @Test
    fun does_not_classify_with_a_short_title() = runTest(dispatcher.scheduler) {
        val classifier = CountingClassifier()
        val viewModel = newViewModel(classifier)

        viewModel.onTitleChanged("Cort")
        viewModel.onDescriptionChanged("Descripción suficientemente larga para clasificar el servicio.")
        advanceUntilIdle()

        assertEquals(0, classifier.calls)
        assertNull(viewModel.uiState.value.aiSuggestion)
        assertFalse(viewModel.uiState.value.isAiAnalyzing)
    }

    @Test
    fun does_not_classify_with_a_short_description() = runTest(dispatcher.scheduler) {
        val classifier = CountingClassifier()
        val viewModel = newViewModel(classifier)

        viewModel.onTitleChanged("Título válido para clasificar")
        viewModel.onDescriptionChanged("Cort")
        advanceUntilIdle()

        assertEquals(0, classifier.calls)
        assertNull(viewModel.uiState.value.aiSuggestion)
        assertFalse(viewModel.uiState.value.isAiAnalyzing)
    }

    @Test
    fun classify_after_delay_when_title_and_description_are_valid() = runTest(dispatcher.scheduler) {
        val classifier = CountingClassifier()
        val viewModel = newViewModel(classifier)

        viewModel.onTitleChanged("Plomero urgente fuga agua")
        viewModel.onDescriptionChanged("Reparación urgente de fuga de agua en cocina, disponible 24 horas.")
        advanceTimeBy(800)
        advanceUntilIdle()

        assertEquals(1, classifier.calls)
        assertNotNull(viewModel.uiState.value.aiSuggestion)
        assertEquals("hogar", viewModel.uiState.value.aiSuggestion?.category)
        assertFalse(viewModel.uiState.value.isAiAnalyzing)
    }

    @Test
    fun use_ai_category_updates_form_fields() = runTest(dispatcher.scheduler) {
        val classifier = CountingClassifier()
        val viewModel = newViewModel(classifier)

        viewModel.onTitleChanged("Plomero urgente fuga agua")
        viewModel.onDescriptionChanged("Reparación urgente de fuga de agua en cocina, disponible 24 horas.")
        advanceTimeBy(800)
        advanceUntilIdle()

        viewModel.onUseAiCategory()
        advanceUntilIdle()

        assertEquals("hogar", viewModel.uiState.value.categoryId)
        assertEquals("Plomería", viewModel.uiState.value.subcategoryId)
    }

    @Test
    fun validate_form_fails_with_empty_fields() = runTest(dispatcher.scheduler) {
        val viewModel = newViewModel()
        // Set location to empty to test the location error
        viewModel.onLocationChanged("")

        val isValid = viewModel.validateForm()

        assertFalse(isValid)
        assertNotNull(viewModel.uiState.value.titleError)
        assertNotNull(viewModel.uiState.value.descriptionError)
        assertNotNull(viewModel.uiState.value.categoryError)
        assertNotNull(viewModel.uiState.value.locationError)
    }

    @Test
    fun validate_form_passes_with_all_fields_filled() = runTest(dispatcher.scheduler) {
        val viewModel = newViewModel()

        viewModel.onTitleChanged("Plomero urgente fuga agua")
        viewModel.onDescriptionChanged("Reparación urgente de fuga de agua en cocina, disponible 24 horas.")
        viewModel.onCategoryChanged("Hogar")
        viewModel.onSubcategoryChanged("Plomería")
        viewModel.onMinPriceChanged("80000")
        viewModel.onMaxPriceChanged("120000")
        viewModel.onLocationChanged("Armenia, Quindío")
        viewModel.onAddImage("https://example.com/photo.jpg")

        val isValid = viewModel.validateForm()

        assertEquals(true, isValid)
    }

    @Test
    fun save_publication_shows_error_when_offline() = runTest(dispatcher.scheduler) {
        val offlineMonitor = FakeConnectivityMonitor(false)
        val viewModel = PublicationFlowViewModel(
            savedStateHandle = SavedStateHandle(),
            getPublicationByIdUseCase = GetPublicationByIdUseCase(env.publications),
            createPublicationUseCase = CreatePublicationUseCase(env.publications, env.generateNotification),
            updatePublicationUseCase = UpdatePublicationUseCase(env.publications),
            getCurrentUserUseCase = GetCurrentUserUseCase(env.users),
            awardPointsUseCase = env.awardPoints,
            classifyServiceWithAiUseCase = ClassifyServiceWithAiUseCase(InMemoryAiClassifierRepository()),
            geocodingRepository = InMemoryGeocodingRepository(),
            imageRepository = InMemoryImageRepository(),
            connectivityMonitor = offlineMonitor
        )

        viewModel.onTitleChanged("Plomero urgente fuga agua")
        viewModel.onDescriptionChanged("Reparación urgente de fuga de agua en cocina, disponible 24 horas.")
        viewModel.onCategoryChanged("Hogar")
        viewModel.onSubcategoryChanged("Plomería")
        viewModel.onMinPriceChanged("80000")
        viewModel.onMaxPriceChanged("120000")
        viewModel.onLocationChanged("Armenia, Quindío")

        viewModel.onSavePublication()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isSuccess)
        assertTrue(viewModel.uiState.value.userMessage?.contains("conexión") == true)
    }

    private class CountingClassifier : AiClassifierRepository {
        var calls = 0
        override suspend fun classify(title: String, description: String, catalog: List<CatalogCategory>): AiClassification? {
            calls++
            return AiClassification("hogar", "Plomería", 90)
        }
    }
}