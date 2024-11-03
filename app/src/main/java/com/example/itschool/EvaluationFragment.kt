// EvaluationFragment.kt
package com.example.itschool

import com.example.itschool.adapter.AssignmentAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.itschool.model.Assignment
import com.example.itschool.utils.FirebaseUtil
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query

class EvaluationFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AssignmentAdapter
    private lateinit var buttonCreate: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_evaluation, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        setupRecyclerView()

        Log.d("EvaluationFragment", "RecyclerView: $recyclerView")
        buttonCreate = view.findViewById(R.id.addEvaluationButton)
        buttonCreate.setOnClickListener {
           Intent(requireContext(), AssignmentActivity::class.java).also {
               startActivity(it)
           }
        }
        Log.d("EvaluationFragment", "Button: $buttonCreate")
        return view
    }

    private fun setupRecyclerView() {
        val query : Query = FirebaseUtil.allUserCollectionReference()
            .whereEqualTo("userId", FirebaseUtil.currentUserId())

        val options = FirestoreRecyclerOptions.Builder<Assignment>()
            .setQuery(query, Assignment::class.java)
            .build()

        adapter = AssignmentAdapter(options)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

}
