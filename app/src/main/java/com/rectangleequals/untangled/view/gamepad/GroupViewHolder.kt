package com.rectangleequals.untangled.view.gamepad

import android.graphics.Color
import android.view.View
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TextView
import com.rectangleequals.untangled.R
import com.rectangleequals.untangled.model.gamepad.GroupContainer

class GroupViewHolder(private val activity: MainActivity, parentView: View) {
    var groupContainer: TableLayout = parentView.findViewById(R.id.GroupContainer)
    var groupLabel: TextView = groupContainer.findViewById(R.id.GroupLabel)
    var groupRowContainer: LinearLayout = parentView.findViewById(R.id.GroupRowContainer)

    fun update(data: GroupContainer) {
        groupLabel.text = data.label
        updateRows(data)
    }

    private fun updateRows(group: GroupContainer) {
        removeRows()
        var currentRow: LinearLayout? = null

        for (groupRow in group.rows) {
            val rowView = activity.layoutInflater.inflate(R.layout.layout_group_row, null)
            val labelTextView = rowView.findViewById<TextView>(R.id.labelTextView)
            val dataTextView = rowView.findViewById<TextView>(R.id.dataTextView)

            labelTextView.text = groupRow.label
            dataTextView.text = groupRow.data
            dataTextView.setTextColor(if (groupRow.data == "true") Color.RED else Color.WHITE)

            if (currentRow == null || currentRow.childCount == group.span) {
                currentRow = LinearLayout(activity)
                currentRow.orientation = LinearLayout.HORIZONTAL
                groupRowContainer.addView(currentRow)
            }

            currentRow.addView(rowView)
        }
    }

    private fun removeRows() {
        groupRowContainer.removeAllViews()
    }
}