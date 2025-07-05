package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProjectAdapter(
    private val projects: List<Project>,
    private val onItemClick: (Project) -> Unit
) : RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_project, parent, false)
        return ProjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val project = projects[position]
        holder.name.text = project.name ?: "Unnamed Project"
        holder.status.text = "Status: ${project.status ?: "N/A"}"
        holder.targetAmount.text = "Target: $${project.targetAmount ?: "N/A"}"
        holder.receivedAmount.text = "Received: $${project.receivedAmount ?: "N/A"}"
        holder.createdAt.text = "Created: ${project.createdAt ?: "N/A"}"
        holder.itemView.setOnClickListener { onItemClick(project) }
    }

    override fun getItemCount(): Int = projects.size

    class ProjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.projectName)
        val status: TextView = itemView.findViewById(R.id.projectStatus)
        val targetAmount: TextView = itemView.findViewById(R.id.projectTargetAmount)
        val receivedAmount: TextView = itemView.findViewById(R.id.projectReceivedAmount)
        val createdAt: TextView = itemView.findViewById(R.id.projectCreatedAt)
    }
} 