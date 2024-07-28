package com.rleonb.reported

import android.app.Application
import compose.icons.android.evaicons.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class ReportedApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // BuildConfig will be created after first run of the app
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
