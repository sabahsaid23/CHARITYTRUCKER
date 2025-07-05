package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.appcompat.widget.SearchView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts

class AdminUsersActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserAdapter
    private val users = mutableListOf<User>()
    private val allUsers = mutableListOf<User>()
    private val BASE_URL = "http://10.78.83.106:8080"
    private lateinit var searchView: SearchView
    private val editUserLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            fetchUsers()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_users)

        recyclerView = findViewById(R.id.usersRecyclerView)
        searchView = findViewById(R.id.searchView)
        adapter = UserAdapter(users) { user ->
            val intent = Intent(this, AddEditUserActivity::class.java)
            intent.putExtra("user_id", user.id)
            intent.putExtra("username", user.username)
            intent.putExtra("email", user.email)
            intent.putExtra("role", user.role)
            intent.putExtra("status", user.status)
            intent.putExtra("createdAt", user.createdAt)
            editUserLauncher.launch(intent)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val addStaffFab: FloatingActionButton = findViewById(R.id.addStaffFab)
        addStaffFab.setOnClickListener {
            val intent = Intent(this, AddEditUserActivity::class.java)
            intent.putExtra("mode", "add_staff")
            editUserLauncher.launch(intent)
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterUsers(query)
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                filterUsers(newText)
                return true
            }
        })

        fetchUsers()
    }

    private fun fetchUsers() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(AdminApi::class.java)
        api.getAllUsers().enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful && response.body() != null) {
                    allUsers.clear()
                    allUsers.addAll(response.body()!!)
                    users.clear()
                    users.addAll(allUsers)
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@AdminUsersActivity, "Failed to load users", Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Toast.makeText(this@AdminUsersActivity, "Network error: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun filterUsers(query: String?) {
        val q = query?.trim()?.lowercase() ?: ""
        users.clear()
        if (q.isEmpty()) {
            users.addAll(allUsers)
        } else {
            users.addAll(allUsers.filter {
                (it.username?.lowercase()?.contains(q) == true) ||
                (it.email?.lowercase()?.contains(q) == true) ||
                (it.role?.lowercase()?.contains(q) == true)
            })
        }
        adapter.notifyDataSetChanged()
    }
} 