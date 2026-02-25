package com.bookreads.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookreads.core.common.DispatcherSet
import com.bookreads.core.pref.PrefService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val initArg: String,
    private val navigationCallback: MainNavigationCallback,
    private val dispatcherSet: DispatcherSet,
    private val prefService: PrefService,
) : ViewModel() {
    val viewState: StateFlow<MainViewState>
        field = MutableStateFlow<MainViewState>(MainViewState.Loading)

    init {
        viewModelScope.launch(dispatcherSet.defaultDispatcher()) {
            prefService.getKey().collect { pref ->
                viewState.value = MainViewState.Success(data = "initArg=$initArg pref=$pref")
            }
        }
    }

    fun onEvent(event: MainViewEvent) {
        viewModelScope.launch {
            when (event) {
                MainViewEvent.NavigateBack -> viewModelScope.launch { navigationCallback.goBack() }
            }
        }
    }
}
