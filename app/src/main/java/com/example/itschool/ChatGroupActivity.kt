package com.example.itschool

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.itschool.adapter.ChatRecyclerAdapter
import com.example.itschool.model.ChatMessageModel
import com.example.itschool.model.ChatroomModel
import com.example.itschool.model.UserModel
import com.example.itschool.utils.AndroidUtils
import com.example.itschool.utils.FirebaseUtil
import com.example.itschool.utils.SecurityUtil
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class ChatGroupActivity : AppCompatActivity() {

    private lateinit var otherUser: UserModel
    private lateinit var chatroomId: String
    private lateinit var chatroomModel: ChatroomModel
    private lateinit var adapter: ChatRecyclerAdapter

    private lateinit var messageInput: EditText
    private lateinit var sendMessageBtn: ImageButton
    private lateinit var backBtn: ImageButton
    private lateinit var otherUsername: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var imageView: ImageView
    private lateinit var onlineStatus: TextView
    private lateinit var userName : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_group)

        // Obtenir le modèle UserModel
        otherUser = AndroidUtils.getUserModelFromIntent(intent)
        Log.d("ChatGroupActivity", "OtherUser lastConnection is : ${otherUser }")
        chatroomId = FirebaseUtil.getChatroomId(
            FirebaseUtil.currentUserId().toString(),
            otherUser.userId.toString()
        )

        messageInput = findViewById(R.id.chat_message_input)
        sendMessageBtn = findViewById(R.id.message_send_btn)
        backBtn = findViewById(R.id.back_btn)
        otherUsername = findViewById(R.id.other_username)
        recyclerView = findViewById(R.id.chat_recycler_view)
        imageView = findViewById(R.id.profile_pic_image_view)
        onlineStatus = findViewById(R.id.online_status)

        FirebaseUtil.getOtherProfilePicStorageRef(otherUser.userId.toString()).downloadUrl
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uri: Uri? = task.result
                    uri?.let { AndroidUtils.setProfilePic(this, it, imageView) }
                }
            }

        backBtn.setOnClickListener { onBackPressed() }
        otherUsername.text = otherUser.username

        sendMessageBtn.setOnClickListener {
            val message = messageInput.text.toString().trim()
            if (message.isNotEmpty()) {
                sendMessageToUser(message)
            }
        }

        getOrCreateChatroomModel()
        setupChatRecyclerView()
        val query = FirebaseUtil.getOtherUserIsOnline(otherUser.userId.toString())
        query.get().addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                Log.d("FirebaseUtil", "Other user is online")
                onlineStatus.text = "En ligne"
            } else {
                Log.d("FirebaseUtil", "Other user is offline")
                onlineStatus.text = "en ligne a " + FirebaseUtil.timestampToString(otherUser.lastConnection)
            }
        }.addOnFailureListener { exception ->
            Log.e("FirebaseUtil", "Error getting documents: ", exception)
        }
    }

    private fun setupChatRecyclerView() {
        val query: Query = FirebaseUtil.getChatroomMessageReference(chatroomId)
            .orderBy("timestamp", Query.Direction.DESCENDING)

        val options = FirestoreRecyclerOptions.Builder<ChatMessageModel>()
            .setQuery(query, ChatMessageModel::class.java)
            .build()

        adapter = ChatRecyclerAdapter(options, applicationContext)
        val manager = LinearLayoutManager(this).apply { reverseLayout = true }
        recyclerView.layoutManager = manager
        recyclerView.adapter = adapter
        adapter.startListening()
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                recyclerView.smoothScrollToPosition(0)
            }
        })
    }

    private fun sendMessageToUser(message: String) {
//        // Chiffrement du message avec la clé publique de l'autre utilisateur
//        val recipientPublicKey = SecurityUtil.getPublicKeyForUser(otherUser.userId.toString())
//        val (encryptedMessage, encryptedSecretKey) = SecurityUtil.encryptMessage(message, recipientPublicKey)

        chatroomModel.lastMessageTimestamp = Timestamp.now()
        chatroomModel.lastMessageSenderId = FirebaseUtil.currentUserId()
        chatroomModel.lastMessage = message
        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel)

        val chatMessageModel = ChatMessageModel(message, FirebaseUtil.currentUserId(), Timestamp.now())
        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
            .addOnCompleteListener { task: Task<DocumentReference> ->
                if (task.isSuccessful) {
                    messageInput.setText("")
                    sendNotification(message)
                }
            }
    }

    private fun getOrCreateChatroomModel() {
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                chatroomModel = task.result.toObject(ChatroomModel::class.java)
                    ?: ChatroomModel(
                        chatroomId,
                        listOf(FirebaseUtil.currentUserId(), otherUser.userId),
                        Timestamp.now(),
                        ""
                    ).also { FirebaseUtil.getChatroomReference(chatroomId).set(it) }
            }
        }
    }

    private fun sendNotification(message: String) {
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val currentUser = task.result.toObject(UserModel::class.java)
                try {
                    val jsonObject = JSONObject()
                    val notificationObj = JSONObject().apply {
                        put("title", currentUser?.username)
                        put("body", message)
                    }
                    val dataObj = JSONObject().apply {
                        put("userId", currentUser?.userId)
                    }
                    jsonObject.put("notification", notificationObj)
                    jsonObject.put("data", dataObj)
                    jsonObject.put("to", otherUser.fcmToken)

                    callApi(jsonObject)
                } catch (e: Exception) {
                    Log.e("ChatActivity", "Failed to create JSON for notification", e)
                }
            }
        }
    }

    private fun callApi(jsonObject: JSONObject) {
        val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
        val client = OkHttpClient()
        val url = "https://fcm.googleapis.com/fcm/send"
        val body = RequestBody.create(JSON, jsonObject.toString())
        val request = Request.Builder()
            .url(url)
            .post(body)
            .header("Authorization", "Bearer YOUR_API_KEY")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ChatActivity", "Failed to send notification", e)
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("ChatActivity", "Notification sent successfully: ${response.body?.string()}")
            }
        })
    }

}
