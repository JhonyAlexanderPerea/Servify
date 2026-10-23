package co.edu.uniquindio.servify.features.service.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.servify.domain.model.Comment
import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.repository.UserRepository
import co.edu.uniquindio.servify.domain.usecase.comment.AddCommentUseCase
import co.edu.uniquindio.servify.domain.usecase.comment.ObserveCommentsUseCase
import co.edu.uniquindio.servify.domain.usecase.publication.GetServiceDetailUseCase
import co.edu.uniquindio.servify.domain.usecase.publication.ObserveServicesUseCase
import co.edu.uniquindio.servify.domain.usecase.publication.ToggleInterestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ServiceDetailUiState(
    val service: Publication? = null,
    val comments: List<Comment> = emptyList(),
    val newCommentText: String = "",
    val newCommentRating: Int = 5,
    val isLoading: Boolean = false,
    val isSubmittingComment: Boolean = false,
    val userMessage: String? = null
)

@HiltViewModel
class ServiceDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeServicesUseCase: ObserveServicesUseCase,
    observeCommentsUseCase: ObserveCommentsUseCase,
    private val addCommentUseCase: AddCommentUseCase,
    private val toggleInterestUseCase: ToggleInterestUseCase,
    private val userRepository: UserRepository
) : ViewModel() {

    val serviceId: String = savedStateHandle.get<String>("serviceId") ?: "pub_1"

    private val _newCommentText = MutableStateFlow("")
    private val _newCommentRating = MutableStateFlow(5)
    private val _isSubmittingComment = MutableStateFlow(false)

    val uiState: StateFlow<ServiceDetailUiState> = combine(
        observeServicesUseCase(),
        observeCommentsUseCase(serviceId),
        _newCommentText,
        _newCommentRating,
        _isSubmittingComment
    ) { services, comments, commentText, rating, submitting ->

        val foundService = services.find { it.id == serviceId }

        ServiceDetailUiState(
            service = foundService,
            comments = comments,
            newCommentText = commentText,
            newCommentRating = rating,
            isLoading = false,
            isSubmittingComment = submitting
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
    }

    fun onRatingChanged(rating: Int) {
        _newCommentRating.value = rating.coerceIn(1, 5)
    }

    fun onSubmitComment() {
        val text = _newCommentText.value.trim()
        if (text.isBlank()) return

        viewModelScope.launch {
            _isSubmittingComment.value = true
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

            _newCommentText.value = ""
            _newCommentRating.value = 5
            _isSubmittingComment.value = false
        }
    }
}
