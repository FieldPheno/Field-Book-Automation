package com.fieldbook.tracker.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fieldbook.tracker.R

/**
 * Reference:
 * https://developer.android.com/guide/topics/ui/layout/recyclerview
 */
class OptionalSetupAdapter :
    ListAdapter<OptionalSetupAdapter.OptionalSetupModel, OptionalSetupAdapter.ViewHolder>(
        DiffCallback()
    ) {

    data class OptionalSetupModel(
        val setupTitle: String,
        val setupSummary: String,
        val onSelectCallback: (() -> Unit)?,
        val onUnselectCallback: (() -> Unit)?,
        var isChecked: Boolean = false
    )

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val setupItem: LinearLayout = itemView.findViewById(R.id.setup_item)
        val checkbox: CheckBox = itemView.findViewById(R.id.checkbox)
        val setupTitle: TextView = itemView.findViewById(R.id.setup_title)
        val setupSummary: TextView = itemView.findViewById(R.id.setup_summary)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.app_intro_optional_setup_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = currentList[position]
        viewHolder.setupTitle.text = item.setupTitle
        viewHolder.setupSummary.text = item.setupSummary
        viewHolder.checkbox.isChecked = item.isChecked
        viewHolder.checkbox.text

        // perform action based on initial checked status
        performItemAction(item)

        viewHolder.setupItem.setOnClickListener {
            // Toggle the checked state of the item
            item.isChecked = !item.isChecked
            viewHolder.checkbox.isChecked = item.isChecked

            performItemAction(item)
        }
    }

    private fun performItemAction(item: OptionalSetupModel) {
        if (item.isChecked) {
            item.onSelectCallback?.invoke()
        } else {
            item.onUnselectCallback?.invoke()
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<OptionalSetupModel>() {

        override fun areItemsTheSame(
            oldItem: OptionalSetupModel, newItem: OptionalSetupModel
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: OptionalSetupModel, newItem: OptionalSetupModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}