package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class AllocationAdapter(private val allocations: List<Allocation>) :
    RecyclerView.Adapter<AllocationAdapter.AllocationViewHolder>() {

    class AllocationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val project: TextView = view.findViewById(R.id.allocationProject)
        val beneficiary: TextView = view.findViewById(R.id.allocationBeneficiary)
        val amount: TextView = view.findViewById(R.id.allocationAmount)
        val date: TextView = view.findViewById(R.id.allocationDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllocationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_allocation, parent, false)
        return AllocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: AllocationViewHolder, position: Int) {
        val allocation = allocations[position]
        
        // Set project name
        holder.project.text = allocation.project?.name ?: "Unknown Project"
        
        // Set beneficiary name
        holder.beneficiary.text = allocation.beneficiary?.name ?: "Unknown Beneficiary"
        
        // Set amount with currency formatting
        val amount = allocation.amount ?: 0.0
        holder.amount.text = "$${String.format("%.2f", amount)}"
        
        // Set date with formatting
        val dateText = allocation.date ?: "Unknown Date"
        holder.date.text = formatDate(dateText)
    }

    override fun getItemCount() = allocations.size

    private fun formatDate(dateString: String): String {
        return try {
            // Try to parse and format the date
            // Assuming the date comes in ISO format or similar
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            date?.let { outputFormat.format(it) } ?: dateString
        } catch (e: Exception) {
            // If parsing fails, return the original string
            dateString
        }
    }
} 