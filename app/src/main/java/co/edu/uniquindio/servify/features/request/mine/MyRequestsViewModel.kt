package co.edu.uniquindio.servify.features.request.mine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.servify.domain.model.RequestStatus
import co.edu.uniquindio.servify.domain.usecase.auth.ObserveSessionUseCase
import co.edu.uniquindio.servify.domain.usecase.request.MyRequest
import co.edu.uniquindio.servify.domain.usecase.request.ObserveMyRequestsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/** Fila de "Mis solicitudes" lista para pintar. */
data class RequestItem(
    val id: String,
    val publicationId: String,
    val title: String,
    val providerName: String,
    val status: RequestStatus,
    val dateLabel: String,
    val amountLabel: String,
    val note: String?
)

data class MyRequestsUiState(
    val isLoading: Boolean = true,
    val requests: List<RequestItem> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MyRequestsViewModel @Inject constructor(
    observeSessionUseCase: ObserveSessionUseCase,
    observeMyRequestsUseCase: ObserveMyRequestsUseCase
) : ViewModel() {

    val uiState: StateFlow<MyRequestsUiState> = observeSessionUseCase()
        .filterNotNull()
        .map { it.id }
        .distinctUntilChanged()
        .flatMapLatest { userId -> observeMyRequestsUseCase(userId) }
        .map { list -> MyRequestsUiState(isLoading = false, requests = list.map { it.toItem() }) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MyRequestsUiState()
        )
}

private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

private fun MyRequest.toItem() = RequestItem(
    id = request.id,
    publicationId = request.publicationId,
    title = publicationTitle,
    providerName = providerName,
    status = request.status,
    dateLabel = dateFormatter.format(
        Instant.ofEpochMilli(request.scheduledDateTime).atZone(ZoneId.systemDefault())
    ),
    amountLabel = "$" + "%,d".format(request.totalAmount.toInt()).replace(',', '.'),
    note = request.note?.takeIf { it.isNotBlank() }
)
