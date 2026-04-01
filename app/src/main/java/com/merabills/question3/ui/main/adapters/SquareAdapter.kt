package com.merabills.question3.ui.main.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.merabills.question3.R
import com.merabills.question3.models.Square

private val INDEX_STRINGS = Array(10001) { it.toString() }

class SquareAdapter(private val onSquareClicked: (Square) -> Unit) :
    ListAdapter<Square, SquareAdapter.SquareViewHolder>(SquareDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SquareViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_square, parent, false)
        return SquareViewHolder(view, onSquareClicked)
    }

    override fun onBindViewHolder(holder: SquareViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class SquareViewHolder(itemView: View, private val onSquareClicked: (Square) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.squareText)
        private var boundSquare: Square? = null

        init {
            itemView.setOnClickListener {
                boundSquare?.let { onSquareClicked(it) }
            }
        }

        fun bind(square: Square) {
            this.boundSquare = square
            textView.text = if (square.index in INDEX_STRINGS.indices) {
                INDEX_STRINGS[square.index]
            } else {
                square.index.toString()
            }
            textView.setBackgroundColor(square.color)
        }
    }

    class SquareDiffCallback : DiffUtil.ItemCallback<Square>() {
        override fun areItemsTheSame(oldItem: Square, newItem: Square): Boolean =
            oldItem.index == newItem.index

        override fun areContentsTheSame(oldItem: Square, newItem: Square): Boolean =
            oldItem == newItem
    }
}
