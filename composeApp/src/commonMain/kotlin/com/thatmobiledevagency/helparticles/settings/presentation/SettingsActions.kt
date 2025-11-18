package com.thatmobiledevagency.helparticles.settings.presentation

sealed interface SettingsActions {
    data object toggleCacheDisabled: SettingsActions
}