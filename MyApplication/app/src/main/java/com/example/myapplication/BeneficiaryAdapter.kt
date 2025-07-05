package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BeneficiaryAdapter(
    private val beneficiaries: List<Beneficiary>,
    private val onItemClick: (Beneficiary) -> Unit
) : RecyclerView.Adapter<BeneficiaryAdapter.BeneficiaryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeneficiaryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_beneficiary, parent, false)
        return BeneficiaryViewHolder(view)
    }

    override fun onBindViewHolder(holder: BeneficiaryViewHolder, position: Int) {
        val beneficiary = beneficiaries[position]
        holder.name.text = beneficiary.name ?: "Unnamed"
        holder.contact.text = "Contact: ${beneficiary.contact ?: "N/A"}"
        holder.status.text = "Status: ${beneficiary.status ?: "N/A"}"
        holder.itemView.setOnClickListener { onItemClick(beneficiary) }
    }

    override fun getItemCount(): Int = beneficiaries.size

    class BeneficiaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.beneficiaryName)
        val contact: TextView = itemView.findViewById(R.id.beneficiaryContact)
        val status: TextView = itemView.findViewById(R.id.beneficiaryStatus)
    }
} 