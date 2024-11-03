package com.example.itschool.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.itschool.R
import com.example.itschool.model.Assignment
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class AssignmentAdapter(options: FirestoreRecyclerOptions<Assignment>) :
    FirestoreRecyclerAdapter<Assignment, AssignmentAdapter.AssignmentViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_assignment, parent, false)
        return AssignmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AssignmentViewHolder, position: Int, model: Assignment) {
        holder.bind(model)
    }

    inner class AssignmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.title)
        private val date: TextView = itemView.findViewById(R.id.date)
        private val description: TextView = itemView.findViewById(R.id.description)
        private val type: TextView = itemView.findViewById(R.id.type)

        fun bind(assignment: Assignment) {
            type.text = assignment.type
            title.text = assignment.title
            date.text = assignment.date.toString() // Assurez-vous que `date` est un format approprié pour l'affichage
            description.text = assignment.description
        }
    }
}