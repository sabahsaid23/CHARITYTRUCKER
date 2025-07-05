package com.example.myapplication

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProjectDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_details)

        val name = intent.getStringExtra("project_name") ?: "N/A"
        val desc = intent.getStringExtra("project_desc") ?: "N/A"
        val target = intent.getDoubleExtra("project_target", 0.0)
        val status = intent.getStringExtra("project_status") ?: "N/A"
        val received = intent.getDoubleExtra("project_received", 0.0)
        val created = intent.getStringExtra("project_created") ?: "N/A"

        findViewById<TextView>(R.id.detailsProjectName).text = name
        findViewById<TextView>(R.id.detailsProjectDesc).text = desc
        findViewById<TextView>(R.id.detailsProjectTarget).text = "Target: $target"
        findViewById<TextView>(R.id.detailsProjectStatus).text = "Status: $status"
        findViewById<TextView>(R.id.detailsProjectReceived).text = "Received: $received"
        findViewById<TextView>(R.id.detailsProjectCreated).text = "Created: $created"
    }
} 