package com.thatmobiledevagency.helparticles

import androidx.compose.ui.window.ComposeUIViewController
import com.thatmobiledevagency.helparticles.app.App
import com.thatmobiledevagency.helparticles.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) { App() }