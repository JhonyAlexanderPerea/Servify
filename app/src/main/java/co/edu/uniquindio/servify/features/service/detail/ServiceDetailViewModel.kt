package co.edu.uniquindio.servify.features.service.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.servify.domain.model.Comment
import co.edu.uniquindio.servify.domain.model.Location
import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.repository.UserRepository
import co.edu.uniquindio.servify.domain.usecase.comment.AddCommentUseCase
import co.edu.uniquindio.servify.domain.usecase.comment.ObserveCommentsUseCase
import co.edu.uniquindio.servify.domain.usecase.location.GetAddressFromLocationUseCase
import co.edu.uniquindio.servify.domain.usecase.publication.GetServiceDetailUseCase
import co.edu.uniquindio.servify.domain.usecase.publication.ObserveServicesUseCase
import co.edu.uniquindio.servify.domain.usecase.publication.ToggleInterestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AddressState {
    object Hidden : AddressState
    object Loading : AddressState
    data class Success(val address: String) : AddressState
    object Error : AddressState
}

/** Eventos de un solo consumo para feedback en la UI (Snackbar). */
sealed interface CommentSubmitEvent {
    data object Success : CommentSubmitEvent
    data class Error(val message: String) : CommentSubmitEvent
}

data class ServiceDetailUiState(
    val service: Publication? = null,
    val comments: List<Comment> = emptyList(),
    val newCommentText: String = "",
    val newCommentRating: Int = 5,
    val isLoading: Boolean = false,
    val isSubmittingComment: Boolean = false,
    val commentErrorMessage: String? = null,
    val isOwnService: Boolean = false,
    val addressState: AddressState = AddressState.Hidden,
    val userMessage: String? = null
)

private data class CommentFormData(
    val text: String,
    val rating: Int,
    val isSubmitting: Boolean,
    val errorMessage: String?
)

@HiltViewModel
class ServiceDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeServicesUseCase: ObserveServicesUseCase,
    observeCommentsUseCase: ObserveCommentsUseCase,
    private val addCommentUseCase: AddCommentUseCase,
    private val toggleInterestUseCase: ToggleInterestUseCase,
    private val userRepository: UserRepository,
    private val getAddressFromLocationUseCase: GetAddressFromLocationUseCase
) : ViewModel() {

    val serviceId: String = savedStateHandle.get<String>("serviceId") ?: "pub_1"

    private val _newCommentText = MutableStateFlow("")
    private val _newCommentRating = MutableStateFlow(5)
    private val _isSubmittingComment = MutableStateFlow(false)
    private val _commentErrorMessage = MutableStateFlow<String?>(null)
    private val _addressState = MutableStateFlow<AddressState>(AddressState.Hidden)
    private val _commentSubmitEvents = MutableSharedFlow<CommentSubmitEvent>(
        replay = 1,
        extraBufferCapacity = 1
    )
    private var lastFetchedLocation: Location? = null

    val commentSubmitEvents: SharedFlow<CommentSubmitEvent> = _commentSubmitEvents.asSharedFlow()

    init {
        viewModelScope.launch {
            observeServicesUseCase().collect { services ->
                val service = services.find { it.id == serviceId }
                val location = service?.location
                if (location != null && location != lastFetchedLocation) {
                    lastFetchedLocation = location
                    _addressState.value = AddressState.Loading
                    val address = getAddressFromLocationUseCase(location)
                    _addressState.value = if (address != null) {
                        AddressState.Success(address)
                    } else {
                        AddressState.Error
                    }
                } else if (location == null) {
                    _addressState.value = AddressState.Hidden
                }
            }
        }
    }

    private val _commentFormFlow = combine(
        _newCommentText,
        _newCommentRating,
        _isSubmittingComment,
        _commentErrorMessage
    ) { text, rating, submitting, errorMessage ->
        CommentFormData(text, rating, submitting, errorMessage)
    }

    val uiState: StateFlow<ServiceDetailUiState> = combine(
        observeServicesUseCase(),
        observeCommentsUseCase(serviceId),
        userRepository.observeCurrentUser(),
        _commentFormFlow,
        _addressState
    ) { services, comments, currentUser, formData, addressState ->

        val foundService = services.find { it.id == serviceId }
        val isOwn = foundService?.belongsTo(currentUser.id, currentUser.email) == true

        ServiceDetailUiState(
            service = foundService,
            comments = comments,
            newCommentText = formData.text,
            newCommentRating = formData.rating,
            isLoading = false,
            isSubmittingComment = formData.isSubmitting,
            commentErrorMessage = formData.errorMessage,
            isOwnService = isOwn,
            addressState = addressState
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ServiceDetailUiState(isLoading = true)
    )

    fun onToggleInterest() {
        viewModelScope.launch {
            toggleInterestUseCase(serviceId)
        }
    }

    fun onCommentTextChanged(text: String) {
        _newCommentText.value = text
        if (_commentErrorMessage.value != null) _commentErrorMessage.value = null
    }

    fun onRatingChanged(rating: Int) {
        _newCommentRating.value = rating.coerceIn(1, 5)
    }

    fun onSubmitComment() {
        val text = _newCommentText.value.trim()
        if (text.isBlank() || _isSubmittingComment.value) return

        viewModelScope.launch {
            _isSubmittingComment.value = true
            _commentErrorMessage.value = null

            try {
                val currentUser = userRepository.observeCurrentUser().first()
                val newComment = Comment(
                    id = "c_${System.currentTimeMillis()}",
                    publicationId = serviceId,
                    author = currentUser,
                    rating = _newCommentRating.value,
                    text = text,
                    createdAt = System.currentTimeMillis()
                )

                addCommentUseCase(newComment)

                // Solo se limpia el formulario si la transacción terminó bien.
                _newCommentText.value = ""
                _newCommentRating.value = 5
                _commentSubmitEvents.emit(CommentSubmitEvent.Success)
            } catch (e: Exception) {
                val message = e.message ?: "unknown_error"
                _commentErrorMessage.value = message
                _commentSubmitEvents.emit(CommentSubmitEvent.Error(message))
            } finally {
                _isSubmittingComment.value = false
            }
        }
    }

    /** Permite a la UI descartar el aviso de error una vez mostrado. */
    fun onCommentErrorShown() {
        _commentErrorMessage.value = null
    }
}
