package co.edu.uniquindio.servify.features.publication

import androidx.lifecycle.SavedStateHandle
import co.edu.uniquindio.servify.data.repository.memory.InMemoryAiClassifierRepository
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

    private fun newViewModel(
        classifier: AiClassifierRepository = InMemoryAiClassifierRepository()
    ) = PublicationFlowViewModel(
        savedStateHandle = SavedStateHandle(),
        getPublicationByIdUseCase = GetPublicationByIdUseCase(env.publications),
        createPublicationUseCase = CreatePublicationUseCase(env.publications, env.generateNotification),
        updatePublicationUseCase = UpdatePublicationUseCase(env.publications),
        getCurrentUserUseCase = GetCurrentUserUseCase(env.users),
        awardPointsUseCase = env.awardPoints,
        classifyServiceWithAiUseCase = ClassifyServiceWithAiUseCase(classifier)
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

        viewModel.onTitleChanged("Reparación de computadores")
        viewModel.onDescriptionChanged("Muy corta")
        advanceUntilIdle()

        assertEquals(0, classifier.calls)
        assertNull(viewModel.uiState.value.aiSuggestion)
    }

    @Test
    fun shows_a_suggestion_after_the_debounce() = runTest(dispatcher.scheduler) {
        val viewModel = newViewModel()

        viewModel.onTitleChanged("Reparación de computadores y laptops")
        viewModel.onDescriptionChanged(
            "Formateo, mantenimiento, instalación de programas y reparación de equipos."
        )
        advanceUntilIdle()

        val suggestion = viewModel.uiState.value.aiSuggestion
        assertNotNull(suggestion)
        assertEquals("Tecnología", suggestion?.category)
        assertEquals("Reparación de equipos", suggestion?.subcategory)
        assertFalse(viewModel.uiState.value.isAiAnalyzing)
    }

    @Test
    fun debounce_cancels_previous_classifications() = runTest(dispatcher.scheduler) {
        val classifier = CountingClassifier()
        val viewModel = newViewModel(classifier)

        viewModel.onTitleChanged("Reparación de computadores")
        viewModel.onDescriptionChanged("Formateo y mantenimiento de equipos.")
        advanceTimeBy(400)
        viewModel.onTitleChanged("Reparación de laptops")
        advanceTimeBy(400)
        viewModel.onDescriptionChanged("Formateo y mantenimiento de laptops y equipos.")
        advanceUntilIdle()

        assertEquals(1, classifier.calls)
        assertNotNull(viewModel.uiState.value.aiSuggestion)
    }

    @Test
    fun use_ai_category_fills_category_and_subcategory_and_clears_the_suggestion() =
        runTest(dispatcher.scheduler) {
            val viewModel = newViewModel()
            viewModel.onTitleChanged("Fuga de agua urgente")
            viewModel.onDescriptionChanged("Reparación de tuberías y grifos en el hogar.")
            advanceUntilIdle()

            viewModel.onUseAiCategory()

            val state = viewModel.uiState.value
            assertEquals("Hogar", state.categoryId)
            assertEquals("Plomería", state.subcategoryId)
            assertNull(state.aiSuggestion)
            assertNull(state.categoryError)
            assertNull(state.subcategoryError)
        }

    @Test
    fun dismiss_ai_suggestion_clears_it_without_changing_the_category() =
        runTest(dispatcher.scheduler) {
            val viewModel = newViewModel()
            viewModel.onTitleChanged("Fuga de agua urgente")
            viewModel.onDescriptionChanged("Reparación de tuberías y grifos en el hogar.")
            advanceUntilIdle()
            viewModel.onCategoryChanged("Tecnología")

            viewModel.onDismissAiSuggestion()

            assertEquals("Tecnología", viewModel.uiState.value.categoryId)
            assertNull(viewModel.uiState.value.aiSuggestion)
        }

    @Test
    fun step_one_advances_with_only_the_title() = runTest(dispatcher.scheduler) {
        val viewModel = newViewModel()

        viewModel.onTitleChanged("Título válido para el servicio")
        viewModel.onNextStep()

        assertEquals(2, viewModel.uiState.value.currentStep)
        assertNull(viewModel.uiState.value.categoryError)
    }

    @Test
    fun step_two_requires_category_and_subcategory() = runTest(dispatcher.scheduler) {
        val viewModel = newViewModel()
        viewModel.onTitleChanged("Título válido para el servicio")
        viewModel.onNextStep()
        viewModel.onDescriptionChanged("Descripción suficientemente larga para el servicio.")
        viewModel.onPriceChanged("80000")

        viewModel.onNextStep()

        assertEquals(2, viewModel.uiState.value.currentStep)
        assertEquals("Selecciona una categoría", viewModel.uiState.value.categoryError)
        assertEquals("Selecciona una subcategoría", viewModel.uiState.value.subcategoryError)
        advanceUntilIdle()
    }

    private class CountingClassifier : AiClassifierRepository {
        var calls = 0

        override suspend fun classify(
            title: String,
            description: String,
            catalog: List<CatalogCategory>
        ): AiClassification? {
            calls++
            return KeywordCategoryRules.classify(title, description, catalog)
        }
    }
}
