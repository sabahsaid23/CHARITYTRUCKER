package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserAdapter(
    private val users: List<User>,
    private val onItemClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.username.text = user.username ?: "Unknown"
        holder.email.text = user.email ?: "N/A"
        holder.role.text = "Role: ${user.role ?: "N/A"}"
        holder.status.text = "Status: ${user.status ?: "N/A"}"
        holder.createdAt.text = "Created: ${user.createdAt ?: "N/A"}"
        holder.itemView.setOnClickListener { onItemClick(user) }
    }

    override fun getItemCount(): Int = users.size

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.userUsername)
        val email: TextView = itemView.findViewById(R.id.userEmail)
        val role: TextView = itemView.findViewById(R.id.userRole)
        val status: TextView = itemView.findViewById(R.id.userStatus)
        val createdAt: TextView = itemView.findViewById(R.id.userCreatedAt)
    }
} 