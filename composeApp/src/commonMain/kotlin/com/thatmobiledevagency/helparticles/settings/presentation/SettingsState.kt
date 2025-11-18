package com.thatmobiledevagency.helparticles.settings.presentation

import com.thatmobiledevagency.helparticles.core.domain.SettingsConstants

data class SettingsState(
    val cacheDisabled: Boolean = SettingsConstants.cacheDisabled
)
