package com.rleonb.reported.ui.screens.home

import com.rleonb.reported.domain.models.News

sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Error(val exception: Throwable) : HomeUiState

    data class Success(
        val newsList: List<News?>
    ) : HomeUiState
}
