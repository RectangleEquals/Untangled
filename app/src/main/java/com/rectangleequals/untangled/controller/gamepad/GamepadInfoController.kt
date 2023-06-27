package com.rectangleequals.untangled.controller.gamepad

import android.widget.LinearLayout
import com.rectangleequals.untangled.model.gamepad.*
import com.rectangleequals.untangled.view.gamepad.GroupViewHolder
import com.rectangleequals.untangled.view.gamepad.MainActivity

class GamepadInfoController(private val activity: MainActivity, private val controllerInfoLayout: LinearLayout) {
    private var viewHolderList = mutableListOf<GroupViewHolder>()

    fun createViews() {
        for (i in 0 until controllerInfoLayout.childCount) {
            val childView = controllerInfoLayout.getChildAt(i)

            if (childView.tag.toString().contains("layout_labeled_group")) {
                viewHolderList.add(GroupViewHolder(activity, childView))
            }
        }
    }

    fun updateData() {
        updateData(GamepadInputEvent(null, null))
    }

    fun updateData(event: GamepadInputEvent) {
        val controllerState = ControllerState(event)
        val groups = controllerState.toUI()

        for (i in viewHolderList.indices) {
            viewHolderList[i].update(groups[i])
        }
    }

}
