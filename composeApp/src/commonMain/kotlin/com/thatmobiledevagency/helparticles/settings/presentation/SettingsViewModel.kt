package com.thatmobiledevagency.helparticles.settings.presentation

import androidx.lifecycle.ViewModel
import com.thatmobiledevagency.helparticles.core.domain.SettingsConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsViewModel: ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    fun onAction(action: SettingsActions) {
        when (action) {
            SettingsActions.toggleCacheDisabled -> toggleCacheDisabled()
        }
    }

    private fun toggleCacheDisabled() {
        _state.update {
            it.copy(
                cacheDisabled = !it.cacheDisabled
            )
        }
        SettingsConstants.cacheDisabled = _state.value.cacheDisabled
    }

}