package com.example.itschool.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.itschool.ChatActivity
import com.example.itschool.model.ChatroomModel
import com.example.itschool.model.UserModel
import com.example.itschool.utils.AndroidUtils
import com.example.itschool.utils.FirebaseUtil
import com.example.itschool.R
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class RecentChatRecyclerAdapter(
    options: FirestoreRecyclerOptions<ChatroomModel>,
    private val context: Context
) : FirestoreRecyclerAdapter<ChatroomModel, RecentChatRecyclerAdapter.ChatroomModelViewHolder>(options) {

    override fun onBindViewHolder(holder: ChatroomModelViewHolder, position: Int, model: ChatroomModel) {
        FirebaseUtil.getOtherUserFromChatroom(model.userIds)
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val lastMessageSentByMe = model.lastMessageSenderId == FirebaseUtil.currentUserId()

                    val otherUserModel = task.result.toObject(UserModel::class.java)

                    otherUserModel?.let { user ->
                        FirebaseUtil.getOtherProfilePicStorageRef(user.userId.toString()).downloadUrl
                            .addOnCompleteListener { t ->
                                if (t.isSuccessful) {
                                    val uri: Uri? = t.result
                                    uri?.let { AndroidUtils.setProfilePic(context, it, holder.profilePic) }
                                }
                            }

                        holder.usernameText.text = user.username
                        holder.lastMessageText.text = if (lastMessageSentByMe) {
                            "You : ${model.lastMessage}"
                        } else {
                            model.lastMessage
                        }
                        holder.lastMessageTime.text = FirebaseUtil.timestampToString(model.lastMessageTimestamp)

                        holder.itemView.setOnClickListener {
                            // Navigation vers l'activité de chat
                            val intent = Intent(context, ChatActivity::class.java)
                            AndroidUtils.passUserModelAsIntent(intent, user)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(intent)
                        }
                    }
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatroomModelViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row, parent, false)
        return ChatroomModelViewHolder(view)
    }

    inner class ChatroomModelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameText: TextView = itemView.findViewById(R.id.user_name_text)
        val lastMessageText: TextView = itemView.findViewById(R.id.last_message_text)
        val lastMessageTime: TextView = itemView.findViewById(R.id.last_message_time_text)
        val profilePic: ImageView = itemView.findViewById(R.id.profile_pic_image_view)
    }
}
