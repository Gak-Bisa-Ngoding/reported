package com.rleonb.reported

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rleonb.reported.domain.models.ReportedTheme
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

class MainActivityViewModel() : ViewModel() {
    val uiState = flowOf(MainActivityUiState.Success(ReportedTheme.FollowSystem))
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MainActivityUiState.Loading
        )
}

sealed class MainActivityUiState {
    data object Loading : MainActivityUiState()
    data class Success(val theme: ReportedTheme) : MainActivityUiState()
}
