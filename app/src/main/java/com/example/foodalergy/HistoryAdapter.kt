package com.example.foodalergy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodalergy.data.model.HistoryItem

class HistoryAdapter(private val items: List<HistoryItem>) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textProduct: TextView = itemView.findViewById(R.id.textProduct)
        private val textRisk: TextView = itemView.findViewById(R.id.textRisk)

        fun bind(item: HistoryItem) {
            textProduct.text = "- ${item.productName}"
            textRisk.text = item.riskLevel
            // Optionally color-code status based on item.riskLevel (Safe, Contains Milk, etc.)
        }
    }
}
