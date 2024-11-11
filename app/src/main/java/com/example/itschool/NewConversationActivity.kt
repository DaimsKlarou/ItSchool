package com.example.itschool

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.itschool.adapter.RecentChatGroupRecyclerAdapter
import com.example.itschool.adapter.SearchUserRecyclerAdapter
import com.example.itschool.model.GrouproomModel
import com.example.itschool.model.UserModel
import com.example.itschool.utils.AndroidUtils
import com.example.itschool.utils.FirebaseUtil
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query

class NewConversationActivity : AppCompatActivity() {

    private lateinit var searchInput: EditText
    private lateinit var searchButton: ImageButton
    private lateinit var backButton: ImageButton
    private lateinit var recyclerView: RecyclerView
    private var userModel: UserModel? = null
    private var classe: String? = null

    private var adapter: SearchUserRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_conversation)

        Log.d("SearchUserActivity", "onCreate called")
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener { task ->
            userModel = task.result.toObject(UserModel::class.java)
            Log.d("SearchUserActivity", "UserModel: ${userModel?.classe}")
        }

        classe = intent.getStringExtra("classe")

        searchInput = findViewById(R.id.search_username_input)
        searchButton = findViewById(R.id.search_user_btn)
        recyclerView = findViewById(R.id.add_user_to_recycler_view)

        searchInput.requestFocus()

        initalisationFrame()

        searchButton.setOnClickListener {
            val searchTerm = searchInput.text.toString()
            if (searchTerm.isEmpty() || searchTerm.length < 3) {
                searchInput.error = "Invalid Username"
                return@setOnClickListener
            }
            setupSearchRecyclerView(searchTerm)
        }
    }

    private fun setupSearchRecyclerView(searchTerm: String) {
        val query: Query = FirebaseUtil.allUserCollectionReference()
            .whereGreaterThanOrEqualTo("username", searchTerm)
            .whereLessThanOrEqualTo("username", searchTerm + '\uf8ff')
            .whereEqualTo("classe", userModel?.classe)


        Log.d("SearchUserActivity", "Query valide: $query")

        val options = FirestoreRecyclerOptions.Builder<UserModel>()
            .setQuery(query, UserModel::class.java)
            .setLifecycleOwner(this)
            .build()
        Log.d("SearchUserActivity", "Options valide: $options")

        adapter = SearchUserRecyclerAdapter(options, applicationContext)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        adapter?.startListening()
    }

    private fun initalisationFrame() {
        Log.d("NewConversationActivity", "la classe est ${classe}")
        Log.d("GroupFragment", "le currentUserId est ${FirebaseUtil.currentUserId()}")
        val query: Query = FirebaseUtil.allUserInClasse(classe!!)

        val options = FirestoreRecyclerOptions.Builder<UserModel>()
            .setQuery(query, UserModel::class.java)
            .build()

        Log.d("SearchUserActivity", "Options valide: $options")

        adapter = SearchUserRecyclerAdapter(options, applicationContext)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        adapter?.startListening()
    }

    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter?.stopListening()
    }

    override fun onResume() {
        super.onResume()
        adapter?.startListening()
    }
}
