package com.example.itschool

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.itschool.adapter.RecentChatGroupRecyclerAdapter
import com.example.itschool.adapter.RecentChatRecyclerAdapter
import com.example.itschool.model.ChatroomModel
import com.example.itschool.model.ClassroomModel
import com.example.itschool.model.GrouproomModel
import com.example.itschool.utils.FirebaseUtil
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query

class GroupFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private var adapter: RecentChatGroupRecyclerAdapter? = null
    private var classe : String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_group, container, false)
        recyclerView = view.findViewById(R.id.recyler_view)
        classe = arguments?.getString("classroomId")

        setupRecyclerView()
        return view

    }

    private fun setupRecyclerView() {
        Log.d("GroupFragment", "la classe est ${classe}")
        Log.d("GroupFragment", "le currentUserId est ${FirebaseUtil.currentUserId()}")
        val query: Query = FirebaseUtil.getClassroomReference(classe!!).collection("groups")
            .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING)

        val options = FirestoreRecyclerOptions.Builder<GrouproomModel>()
            .setQuery(query, GrouproomModel::class.java)
            .build()

        adapter = RecentChatGroupRecyclerAdapter(options, requireContext())
        recyclerView.layoutManager = LinearLayoutManager(context)
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
        adapter?.notifyDataSetChanged()
    }
}
