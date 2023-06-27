package com.rectangleequals.untangled

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private lateinit var rootLayout: LinearLayout
    private lateinit var viewHolderList: MutableList<ViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_controller_info)

        SharedData.activityContext = this
        rootLayout = findViewById(R.id.ControllerInfoLayout)

        viewHolderList = mutableListOf()
        createViews()
        updateData(GamepadInputEvent(null, null))
    }

    override fun onGenericMotionEvent(event: MotionEvent): Boolean {
        if (event.source and InputDevice.SOURCE_GAMEPAD == InputDevice.SOURCE_GAMEPAD
            || event.source and InputDevice.SOURCE_JOYSTICK == InputDevice.SOURCE_JOYSTICK) {
            updateData(GamepadInputEvent(event, null))
            return true
        }
        return super.onGenericMotionEvent(event)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.source and InputDevice.SOURCE_GAMEPAD == InputDevice.SOURCE_GAMEPAD) {
            updateData(GamepadInputEvent(null, event))
            return true
        }
        return super.dispatchKeyEvent(event)
    }

    private fun createViews() {
        for (i in 0 until rootLayout.childCount) {
            val childView = rootLayout.getChildAt(i)

            if (childView is ViewGroup && childView.tag.toString().contains("layout_labeled_group")) {
                val viewHolder = ViewHolder()
                viewHolder.groupContainer = childView.findViewById(R.id.GroupContainer)
                viewHolder.groupLabel = viewHolder.groupContainer.findViewById(R.id.GroupLabel)
                viewHolder.groupRowContainer = childView.findViewById(R.id.GroupRowContainer)
                viewHolderList.add(viewHolder)
            }
        }
    }

    private fun updateData(event: GamepadInputEvent) {
        val controllerState = ControllerState(event)
        val groups = controllerState.toUI()

        for (i in viewHolderList.indices) {
            val viewHolder = viewHolderList[i]
            val group = groups[i]

            viewHolder.groupLabel.text = group.label
            viewHolder.groupRowContainer.removeAllViews()

            var currentRow: LinearLayout? = null

            for (row in group.rows) {
                val rowView = layoutInflater.inflate(R.layout.layout_group_row, null)
                val labelTextView = rowView.findViewById<TextView>(R.id.labelTextView)
                val dataTextView = rowView.findViewById<TextView>(R.id.dataTextView)

                labelTextView.text = row.label
                dataTextView.text = row.data
                dataTextView.setTextColor(if (row.data == "true") Color.RED else Color.WHITE)

                if (currentRow == null || currentRow.childCount == group.span) {
                    currentRow = LinearLayout(this)
                    currentRow.orientation = LinearLayout.HORIZONTAL
                    viewHolder.groupRowContainer.addView(currentRow)
                }

                currentRow.addView(rowView)
            }
        }
    }

    private class ViewHolder {
        lateinit var groupContainer: TableLayout
        lateinit var groupLabel: TextView
        lateinit var groupRowContainer: LinearLayout
    }
}
