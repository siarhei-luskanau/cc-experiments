package com.bookreads.ui.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bookreads.core.network.api.LeaderboardEntryModel
import com.bookreads.ui.common.resources.Res
import com.bookreads.ui.common.resources.back_button
import com.bookreads.ui.common.resources.ic_arrow_back
import com.bookreads.ui.common.theme.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun LeaderboardScreen(viewModelProvider: () -> LeaderboardViewModel) {
    val viewModel = viewModel { viewModelProvider() }
    LeaderboardContent(
        viewStateFlow = viewModel.viewState,
        onEvent = viewModel::onEvent,
    )
    DisposableEffect(Unit) {
        viewModel.startPolling()
        onDispose { viewModel.stopPolling() }
    }
}

@Composable
internal fun LeaderboardContent(
    viewStateFlow: StateFlow<LeaderboardViewState>,
    onEvent: (LeaderboardViewEvent) -> Unit,
) {
    val viewState = viewStateFlow.collectAsState()
    val selectedWindow =
        (viewState.value as? LeaderboardViewState.Loaded)?.selectedWindow
            ?: LeaderboardWindow.AllTime
    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Leaderboard") },
                navigationIcon = {
                    IconButton(onClick = { onEvent(LeaderboardViewEvent.GoBack) }) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.ic_arrow_back),
                            contentDescription = stringResource(Res.string.back_button),
                        )
                    }
                },
            )
        },
    ) { contentPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
        ) {
            TabRow(selectedTabIndex = selectedWindow.ordinal) {
                LeaderboardWindow.entries.forEach { window ->
                    Tab(
                        selected = selectedWindow == window,
                        onClick = { onEvent(LeaderboardViewEvent.TabSelected(window)) },
                        text = { Text(window.label) },
                    )
                }
            }
            when (val state = viewState.value) {
                LeaderboardViewState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is LeaderboardViewState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("Error: ${state.error.message}")
                    }
                }

                is LeaderboardViewState.Loaded -> {
                    LeaderboardList(
                        entries = state.entries,
                        currentUsername = state.currentUsername,
                    )
                }
            }
        }
    }
}

@Composable
private fun LeaderboardList(
    entries: List<LeaderboardEntryModel>,
    currentUsername: String?,
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(entries) { entry ->
            val isCurrentUser = entry.username == currentUsername
            ListItem(
                modifier =
                    if (isCurrentUser) {
                        Modifier.background(MaterialTheme.colorScheme.primaryContainer)
                    } else {
                        Modifier
                    },
                leadingContent = { Text("#${entry.rank}", style = MaterialTheme.typography.labelLarge) },
                headlineContent = { Text(entry.username) },
                trailingContent = { Text(formatDuration(entry.totalSec)) },
            )
            HorizontalDivider()
        }
    }
}

private fun formatDuration(totalSec: Long): String {
    val hours = totalSec / 3600
    val minutes = (totalSec % 3600) / 60
    return "${hours}h ${minutes}m"
}

@Preview
@Composable
internal fun LeaderboardScreenPreview() {
    val entries =
        listOf(
            LeaderboardEntryModel(rank = 1, username = "alice", totalSec = 7325, sessionCount = 3),
            LeaderboardEntryModel(rank = 2, username = "bob", totalSec = 3600, sessionCount = 1),
        )
    AppTheme {
        LeaderboardContent(
            viewStateFlow =
                MutableStateFlow(
                    LeaderboardViewState.Loaded(
                        entries = entries,
                        selectedWindow = LeaderboardWindow.AllTime,
                        currentUsername = "alice",
                    ),
                ),
            onEvent = {},
        )
    }
}
