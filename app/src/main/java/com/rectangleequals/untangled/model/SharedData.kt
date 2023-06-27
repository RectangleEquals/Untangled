package com.rectangleequals.untangled.model

import android.app.Activity
import com.rectangleequals.untangled.controller.BackgroundService
import com.google.gson.*

class SharedData {
    companion object {
        val gson by lazy { Gson() }
        var activityContext: Activity? = null
        var backgroundService: BackgroundService? = null
        var lastEventTime: Long = 0
    }
}
