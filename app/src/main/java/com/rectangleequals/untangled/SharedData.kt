package com.rectangleequals.untangled

import android.app.Activity

data class GroupRow(val label: String, val data: String)
data class GroupContainer(val label: String, val span: Int, val rows: List<GroupRow>)

class SharedData {
    companion object {
        var activityContext: Activity? = null
        var backgroundService: BackgroundService? = null
        var lastEventTime: Long = 0
    }
}
