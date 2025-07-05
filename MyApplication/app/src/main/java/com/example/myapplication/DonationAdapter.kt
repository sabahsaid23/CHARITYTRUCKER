package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DonationAdapter(
    private val donations: List<Donation>,
    private val onItemClick: (Donation) -> Unit
) : RecyclerView.Adapter<DonationAdapter.DonationViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_donation, parent, false)
        return DonationViewHolder(view)
    }

    override fun onBindViewHolder(holder: DonationViewHolder, position: Int) {
        val donation = donations[position]
        holder.amount.text = "$${donation.amount ?: "N/A"}"
        holder.projectName.text = donation.project?.name ?: "Unknown Project"
        holder.date.text = donation.date ?: "N/A"
        holder.itemView.setOnClickListener { onItemClick(donation) }
    }

    override fun getItemCount(): Int = donations.size

    class DonationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val amount: TextView = itemView.findViewById(R.id.amount)
        val projectName: TextView = itemView.findViewById(R.id.projectName)
        val date: TextView = itemView.findViewById(R.id.date)
    }
} 