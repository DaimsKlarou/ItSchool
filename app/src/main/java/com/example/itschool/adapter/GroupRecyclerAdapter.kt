package com.example.itschool.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.itschool.R
import com.example.itschool.model.ChatMessageModel
import com.example.itschool.utils.FirebaseUtil
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class GroupRecyclerAdapter(
    options: FirestoreRecyclerOptions<ChatMessageModel>,
    private val context: Context
) : FirestoreRecyclerAdapter<ChatMessageModel, GroupRecyclerAdapter.ChatModelViewHolder>(options) {

    override fun onBindViewHolder(
        holder: ChatModelViewHolder,
        position: Int,
        model: ChatMessageModel
    ) {
        Log.i("haushd", "asjd")
        if (model.senderId == FirebaseUtil.currentUserId()) {
            holder.leftChatLayout.visibility = View.GONE
            holder.rightChatLayout.visibility = View.VISIBLE
            holder.rightChatTextView.text = model.message
            holder.timeRight.text = FirebaseUtil.timestampToString(model.timestamp)
        } else {
            holder.rightChatLayout.visibility = View.GONE
            holder.leftChatLayout.visibility = View.VISIBLE
            holder.leftChatTextView.text = model.message
            FirebaseUtil.userSendMessageToGroup(model.senderId!!){
                Log.i("GroupRecyclerAdapter", "le nom de notre utilisateur est $it")
                holder.sender.text = it
            }
            holder.timeLeft.text = FirebaseUtil.timestampToString(model.timestamp)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatModelViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.chat_message_group_recycler_row, parent, false)
        return ChatModelViewHolder(view)
    }

    inner class ChatModelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val leftChatLayout: LinearLayout = itemView.findViewById(R.id.left_chat_layout)
        val rightChatLayout: LinearLayout = itemView.findViewById(R.id.right_chat_layout)
        val sender : TextView = itemView.findViewById<TextView>(R.id.sender)
        val leftChatTextView: TextView = itemView.findViewById(R.id.left_chat_textview)
        val rightChatTextView: TextView = itemView.findViewById(R.id.right_chat_textview)
        val timeRight : TextView = itemView.findViewById<TextView>(R.id.time_item_right)
        val timeLeft : TextView = itemView.findViewById<TextView>(R.id.time_item_left)
    }
}
