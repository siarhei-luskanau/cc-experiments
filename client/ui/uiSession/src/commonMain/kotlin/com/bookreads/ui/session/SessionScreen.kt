package com.bookreads.ui.session

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bookreads.ui.common.theme.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun SessionScreen(viewModelProvider: () -> SessionViewModel) {
    val viewModel = viewModel { viewModelProvider() }
    SessionContent(
        viewStateFlow = viewModel.viewState,
        onEvent = viewModel::onEvent,
    )
}

@Composable
internal fun SessionContent(
    viewStateFlow: StateFlow<SessionViewState>,
    onEvent: (SessionViewEvent) -> Unit,
) {
    val viewState = viewStateFlow.collectAsState()
    Scaffold { contentPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (val state = viewState.value) {
                SessionViewState.Loading -> CircularProgressIndicator()
                is SessionViewState.Idle -> IdleContent(state = state, onEvent = onEvent)
                is SessionViewState.EnteringBook -> EnteringBookContent(state = state, onEvent = onEvent)
                is SessionViewState.Reading -> ReadingContent(state = state, onEvent = onEvent)
            }
        }
    }
}

@Composable
private fun IdleContent(
    state: SessionViewState.Idle,
    onEvent: (SessionViewEvent) -> Unit,
) {
    Text(
        text = "Hello, ${state.username}",
        style = MaterialTheme.typography.headlineSmall,
    )
    Text("No active reading session")
    Button(
        onClick = { onEvent(SessionViewEvent.StartReadingClicked) },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text("Start Reading")
    }
    TextButton(
        onClick = { onEvent(SessionViewEvent.GoLeaderboard) },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text("View Leaderboard")
    }
    TextButton(
        onClick = { onEvent(SessionViewEvent.GoHome) },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text("Change Username")
    }
}

@Composable
private fun EnteringBookContent(
    state: SessionViewState.EnteringBook,
    onEvent: (SessionViewEvent) -> Unit,
) {
    Text(
        text = "What are you reading?",
        style = MaterialTheme.typography.headlineSmall,
    )
    OutlinedTextField(
        value = state.bookTitle,
        onValueChange = { onEvent(SessionViewEvent.BookTitleChanged(it)) },
        label = { Text("Book title") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OutlinedButton(
            onClick = { onEvent(SessionViewEvent.CancelBookTitle) },
            modifier = Modifier.weight(1f),
        ) {
            Text("Cancel")
        }
        Button(
            onClick = { onEvent(SessionViewEvent.ConfirmBookTitle) },
            enabled = state.bookTitle.isNotBlank(),
            modifier = Modifier.weight(1f),
        ) {
            Text("Start")
        }
    }
}

@Composable
private fun ReadingContent(
    state: SessionViewState.Reading,
    onEvent: (SessionViewEvent) -> Unit,
) {
    Text(
        text = "Hello, ${state.username}",
        style = MaterialTheme.typography.headlineSmall,
    )
    Text(
        text = state.bookTitle,
        style = MaterialTheme.typography.titleMedium,
    )
    Text(
        text = formatElapsed(state.elapsedSec),
        style = MaterialTheme.typography.displayMedium,
    )
    Button(
        onClick = { onEvent(SessionViewEvent.StopReading) },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
    ) {
        Text("Stop Reading")
    }
    TextButton(
        onClick = { onEvent(SessionViewEvent.GoLeaderboard) },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text("View Leaderboard")
    }
}

private fun formatElapsed(totalSec: Long): String {
    val h = totalSec / 3600
    val m = (totalSec % 3600) / 60
    val s = totalSec % 60
    return "${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"
}

@Preview
@Composable
internal fun SessionScreenIdlePreview() =
    AppTheme {
        SessionContent(
            viewStateFlow = MutableStateFlow(SessionViewState.Idle("alice")),
            onEvent = {},
        )
    }

@Preview
@Composable
internal fun SessionScreenReadingPreview() =
    AppTheme {
        SessionContent(
            viewStateFlow = MutableStateFlow(SessionViewState.Reading("alice", "The Hobbit", 3725L)),
            onEvent = {},
        )
    }
