package com.bookreads.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
fun HomeScreen(viewModelProvider: () -> HomeViewModel) {
    val viewModel = viewModel { viewModelProvider() }
    HomeContent(
        viewStateFlow = viewModel.viewState,
        onEvent = viewModel::onEvent,
    )
}

@Composable
internal fun HomeContent(
    viewStateFlow: StateFlow<HomeViewState>,
    onEvent: (HomeViewEvent) -> Unit,
) {
    val viewState = viewStateFlow.collectAsState()
    Scaffold { contentPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Book Reading Leaderboard",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 32.dp),
            )
            when (val state = viewState.value) {
                HomeViewState.Loading -> {
                    Text("Loading...")
                }

                is HomeViewState.Ready -> {
                    if (state.hasActiveSession) {
                        Text(
                            text = "Active reading session in progress",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp),
                        )
                    }
                    OutlinedTextField(
                        value = state.username,
                        onValueChange = { onEvent(HomeViewEvent.UsernameChanged(it)) },
                        label = { Text("Username") },
                        singleLine = true,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                    )
                    Button(
                        onClick = { onEvent(HomeViewEvent.EnterPressed) },
                        enabled = state.username.isNotBlank(),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Enter")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
internal fun HomeScreenPreview() =
    AppTheme {
        HomeContent(
            viewStateFlow = MutableStateFlow(HomeViewState.Ready(username = "alice")),
            onEvent = {},
        )
    }
