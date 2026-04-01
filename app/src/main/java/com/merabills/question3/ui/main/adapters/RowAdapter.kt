package com.merabills.question3.ui.main.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.merabills.question3.R
import com.merabills.question3.models.Row
import com.merabills.question3.models.Square

class RowAdapter(
    private val viewPool: RecyclerView.RecycledViewPool,
    private val onSquareClicked: (Square) -> Unit
) : ListAdapter<Row, RowAdapter.RowViewHolder>(RowDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row, parent, false)
        return RowViewHolder(view, viewPool, onSquareClicked)
    }

    override fun onBindViewHolder(holder: RowViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class RowViewHolder(
        itemView: View,
        viewPool: RecyclerView.RecycledViewPool,
        onSquareClicked: (Square) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val recyclerView: RecyclerView = itemView.findViewById(R.id.innerRecyclerView)
        private val adapter = SquareAdapter(onSquareClicked)

        init {
            val lm = LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false)
            lm.initialPrefetchItemCount = 15
            recyclerView.layoutManager = lm
            recyclerView.setRecycledViewPool(viewPool)
            recyclerView.adapter = adapter
            recyclerView.setHasFixedSize(true)
            recyclerView.isNestedScrollingEnabled = false
            recyclerView.itemAnimator = null
        }

        fun bind(row: Row) {
            adapter.submitList(row.squares)
        }
    }

    class RowDiffCallback : DiffUtil.ItemCallback<Row>() {
        override fun areItemsTheSame(oldItem: Row, newItem: Row): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Row, newItem: Row): Boolean =
            oldItem == newItem
    }
}
