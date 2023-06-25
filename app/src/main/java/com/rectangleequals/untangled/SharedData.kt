package com.rectangleequals.untangled

class SharedData {
    companion object {
        var activityContext: ControllerActivity? = null
        var backgroundService: BackgroundService? = null
        var lastEventTime: Long = 0
    }
}
