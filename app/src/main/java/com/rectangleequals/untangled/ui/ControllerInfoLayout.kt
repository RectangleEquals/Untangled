package com.rectangleequals.untangled.ui

import android.content.Context
import com.rectangleequals.untangled.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

data class GroupContainer(val label: String, val span: Int, val rows: List<GroupRow>)
data class GroupRow(val label: String, val data: String)

class GroupRowAdapter(private val rows: List<GroupRow>, span: Int, context: Context, groupRowContainer: LinearLayout) :
    RecyclerView.Adapter<GroupRowAdapter.ViewHolder>() {

    var recyclerView: RecyclerView

    init {
        // Create a RecyclerView and set the adapter
        recyclerView = RecyclerView(context)
        recyclerView.layoutManager = GridLayoutManager(context, span) // Adjust the span count as needed
        recyclerView.adapter = this

        // Add the RecyclerView to the groupRowContainer
        groupRowContainer.addView(recyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_group_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val row = rows[position]
        if(row is GroupRow) {
            holder.labelTextView.text = row.label
            holder.dataTextView.text = row.data
        }
    }

    override fun getItemCount(): Int {
        return rows.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val labelTextView: TextView = itemView.findViewById(R.id.labelTextView)
        val dataTextView: TextView = itemView.findViewById(R.id.dataTextView)
    }
}
