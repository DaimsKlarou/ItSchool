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
import com.example.itschool.model.UserModel
import com.example.itschool.utils.FirebaseUtil
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query

class EvaluationFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AssignmentAdapter
    private lateinit var buttonCreate: Button
    private lateinit var user : UserModel
    private var classe : String? = null
    private var role : String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_evaluation, container, false)

        Log.d("EvaluationFragment", "les arguments sont : $arguments")
        classe = arguments?.getString("classroomId")
        role = arguments?.getString("userRole")

        recyclerView = view.findViewById(R.id.recyclerView)
        setupRecyclerView()

        val user = FirebaseUtil.currentUserDetails().get().addOnSuccessListener {task ->

            val user = task.toObject(UserModel::class.java)
            if (user != null) {
                if (user.role == "Professeur") {
                    setInEvaluation(true)
                    Log.d("EvaluationFragment", "User is professor")
                } else {
                    setInEvaluation(false)
                    Log.d("EvaluationFragment", "User is student")
                }
            } else {
                Log.d("EvaluationFragment", "User is null")
            }

        }

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

    private fun setInEvaluation(value: Boolean) {
        buttonCreate.visibility = if (value) Button.VISIBLE else Button.GONE
    }

    private fun setupRecyclerView() {
        Log.d("EvaluationFragment", "FirebaseUtil.currentClasseId(): ${classe}")
        var query : Query = FirebaseUtil.getAssignmentsFromClassroom(classe!!)

        if(role == "Etudiant") {
            query = FirebaseUtil.getAssignmentsFromClassroom(classe!!)
            Log.d("EvaluationFragment", "Query: $query")
        } else {
            query  = FirebaseUtil.getAssignmentsFromClassroom(classe!!).whereEqualTo("userId", FirebaseUtil.currentUserId()!!)
            Log.d("EvaluationFragment", "Query: $query")
        }

        val options = FirestoreRecyclerOptions.Builder<Assignment>()
            .setQuery(query, Assignment::class.java)
            .build()

        Log.d("EvaluationFragment", "Options: $options")
        Log.d("EvaluationFragment", "RecyclerView: $recyclerView")

        adapter = AssignmentAdapter(options, requireContext())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
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
        adapter?.notifyDataSetChanged()
    }

}
