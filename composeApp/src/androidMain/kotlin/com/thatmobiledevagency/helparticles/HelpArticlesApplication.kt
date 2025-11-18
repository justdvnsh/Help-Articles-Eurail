package com.thatmobiledevagency.helparticles

import android.app.Application
import com.thatmobiledevagency.helparticles.di.initKoin
import com.thatmobiledevagency.helparticles.work.WorkManagerScheduler
import org.koin.android.ext.koin.androidContext

class HelpArticlesApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@HelpArticlesApplication)
        }

        WorkManagerScheduler(this).schedulePeriodicRefresh()
    }
}