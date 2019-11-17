package mwo.lab.tapswap.activities

import android.app.Application
import android.content.Context


class TapSwap : Application() {

    override fun onCreate() {
        super.onCreate()
        TapSwap.appContext = applicationContext
    }

    companion object {
        var appContext: Context? = null
            private set
    }
}