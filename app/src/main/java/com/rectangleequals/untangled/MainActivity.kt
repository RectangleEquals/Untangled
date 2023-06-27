package com.rectangleequals.untangled

import android.os.Bundle
import android.view.InputDevice
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TextView
import com.rectangleequals.untangled.ui.GroupRowAdapter

class MainActivity : AppCompatActivity() {
    private lateinit var rootLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_controller_info)

        // Find the parent LinearLayout
        rootLayout = findViewById(R.id.ControllerInfoLayout)

        updateUI(GamepadInputEvent(null, null))
    }

    override fun onGenericMotionEvent(event: MotionEvent): Boolean {
        if (event.source and InputDevice.SOURCE_GAMEPAD == InputDevice.SOURCE_GAMEPAD
            || event.source and InputDevice.SOURCE_JOYSTICK == InputDevice.SOURCE_JOYSTICK) {
            updateUI(GamepadInputEvent(event, null))
        } else {
            return false
        }
        return super.onGenericMotionEvent(event)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.source and InputDevice.SOURCE_GAMEPAD == InputDevice.SOURCE_GAMEPAD) {
            updateUI(GamepadInputEvent(null, event))
        } else {
            return false
        }
        return super.dispatchKeyEvent(event)
    }

    private fun updateUI(event: GamepadInputEvent) {
        val controllerState = ControllerState(event)
        val groups = controllerState.toUI()

        // Iterate through each child of the parent layout
        for (i in 0 until rootLayout.childCount) {
            val childView = rootLayout.getChildAt(i)

            // Check if the child is a layout_labeled_group
            if (childView is ViewGroup && childView.tag.toString().contains("layout_labeled_group")) {
                // Find the rowContainer within the layout_labeled_group
                val groupContainer: TableLayout = childView.findViewById(R.id.GroupContainer)
                val groupLabel: TextView = childView.findViewById(R.id.GroupLabel)
                val groupRowContainer: LinearLayout = childView.findViewById(R.id.GroupRowContainer)

                // Find the corresponding GroupContainer
                val group = groups[i]

                // Set the group label
                groupLabel.text = group.label

                // Create an instance of the GroupRowAdapter and pass the rows data
                val adapter = GroupRowAdapter(group.rows, group.span, this, groupRowContainer)
            }
        }
    }
}