package com.example.itschool.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.itschool.ChatActivity
import com.example.itschool.ChatGroupActivity
import com.example.itschool.model.UserModel
import com.example.itschool.utils.AndroidUtils
import com.example.itschool.utils.FirebaseUtil
import com.example.itschool.R
import com.example.itschool.model.GrouproomModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp

class RecentChatGroupRecyclerAdapter(
    options: FirestoreRecyclerOptions<GrouproomModel>,
    private val context: Context
) : FirestoreRecyclerAdapter<GrouproomModel, RecentChatGroupRecyclerAdapter.GrouproomModelViewHolder>(options) {

    override fun onBindViewHolder(holder: GrouproomModelViewHolder, position: Int, model: GrouproomModel) {
        holder.bind(model)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GrouproomModelViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row, parent, false)
        return GrouproomModelViewHolder(view)
    }

    inner class GrouproomModelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomGroup: TextView = itemView.findViewById(R.id.user_name_text)
        val lastMessageText: TextView = itemView.findViewById(R.id.last_message_text)
        val lastMessageTime: TextView = itemView.findViewById(R.id.last_message_time_text)
        val profilePic: ImageView = itemView.findViewById(R.id.profile_pic_image_view)

        fun bind(grouproomModel: GrouproomModel) {
            nomGroup.text = grouproomModel.nomGroup
            lastMessageText.text = grouproomModel.lastMessage ?: "Aucun message"
            lastMessageTime.text = FirebaseUtil.timestampToString(grouproomModel.lastMessageTimestamp ?: Timestamp.now())
        }
    }

}
