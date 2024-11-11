package com.example.itschool

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
import com.example.itschool.adapter.GroupRecyclerAdapter
import com.example.itschool.model.ChatMessageModel
import com.example.itschool.model.ChatroomModel
import com.example.itschool.model.GrouproomModel
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot

class ChatGroupActivity : AppCompatActivity() {

    private lateinit var groupId: String
    private lateinit var classeId: String
    private lateinit var adapter: GroupRecyclerAdapter

    // Remplace `otherUser` par `groupModel`
    private lateinit var groupModel: GrouproomModel
    private lateinit var grouproomModel: GrouproomModel

    private lateinit var messageInput: EditText
    private lateinit var sendMessageBtn: ImageButton
    private lateinit var backBtn: ImageButton
    private lateinit var otherUsername: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var imageView: ImageView
    private lateinit var membre: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_group)

        // Obtiens le modèle GrouproomModel
        groupModel = AndroidUtils.getGroupModelFromIntent(intent)
        groupId = groupModel.grouproomId!!
        classeId = groupModel.classId!!

        // Initialise les composants de l’interface
        messageInput = findViewById(R.id.chat_message_input)
        sendMessageBtn = findViewById(R.id.message_send_btn)
        backBtn = findViewById(R.id.back_btn)
        otherUsername = findViewById(R.id.group_name) // Tu pourrais l’utiliser pour le nom du groupe
        recyclerView = findViewById(R.id.chat_recycler_view)
        membre = findViewById(R.id.membre)

        // Affiche le nom du groupe
        otherUsername.text = groupModel.nomGroup

        // Gestion de l’envoi des messages
        sendMessageBtn.setOnClickListener {
            val message = messageInput.text.toString().trim()
            if (message.isNotEmpty()) {
                sendMessageToGroup(message)
            }
        }

        membre.text =  groupModel.userIds?.size.toString() + " membres"

        backBtn.setOnClickListener {
            finish()
        }

        getOrCreateChatroomModel(classeId, groupModel)

    }

    private fun getOrCreateChatroomModel(classeId : String, groupId: GrouproomModel) {
        Log.d("ChatGroupActivity", "getOrCreateChatroomModel")
        Log.d("ChatGroupActivity", "classeId: $classeId, groupId: $groupId")
        FirebaseUtil.getChatGrouproomMessageReference(classeId, groupId.grouproomId!!).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("ChatGroupActivity", "Successfully getOrCreateChatroomModel")
                Log.d("ChatGroupActivity", "Document snapshot: ${task.result.documents}")
                if (task.result.documents.isNotEmpty()) {
                    Log.d("ChatGroupActivity", "Found existing grouproomModel")
                    grouproomModel = task.result.documents[0].toObject(GrouproomModel::class.java)!!
                } else {
                    Log.d("ChatGroupActivity", "Creating new grouproomModel")
                        // Crée la nouvelle salle de chat dans Firestore
                        FirebaseUtil.getChatGrouproomMessageReference(classeId, groupId.grouproomId!!).get().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("ChatGroupActivity", "Successfully getOrCreateChatroomModel")
                                Log.d(
                                    "ChatGroupActivity",
                                    "Document snapshot: ${task.result.documents}"
                                )

                            }
                        }
                }
                setupChatRecyclerView()
            } else {
                Log.e("ChatGroupActivity", "Erreur lors de la récupération de la salle de discussion : ", task.exception)
            }
        }
    }

    private fun setupChatRecyclerView() {
        val query: Query = FirebaseUtil.getChatGrouproomMessageReference(classeId, groupId)
            .orderBy("timestamp", Query.Direction.DESCENDING)

        val options = FirestoreRecyclerOptions.Builder<ChatMessageModel>()
            .setQuery(query, ChatMessageModel::class.java)
            .setLifecycleOwner(this)
            .build()

        adapter = GroupRecyclerAdapter(options, applicationContext)
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


    private fun sendMessageToGroup(message: String) {
        Log.d("ChatGroupActivity", "sendMessageToGroup")
        groupModel.lastMessageTimestamp = Timestamp.now()
        groupModel.lastMessageSenderId = FirebaseUtil.currentUserId()
        groupModel.lastMessage = message

        FirebaseUtil.getChatGrouproomReference(classeId, groupId).set(groupModel)

        val chatMessageModel = ChatMessageModel(message, FirebaseUtil.currentUserId(), Timestamp.now())
        FirebaseUtil.getChatGroupMessagesReferences(classeId, groupId).add(chatMessageModel)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    messageInput.setText("")
                    sendGroupNotification(message)
                }
            }
    }

    private fun sendGroupNotification(message: String) {
        FirebaseUtil.getGroupMembers(groupId).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val currentUser = FirebaseUtil.currentUserId()
                val members = task.result?.documents?.mapNotNull { it.toObject(UserModel::class.java) }

                members?.forEach { member ->
                    if (member.userId != currentUser) { // Ignore l'utilisateur actuel
                        try {
                            val jsonObject = JSONObject()
                            val notificationObj = JSONObject().apply {
                                put("title", groupModel.nomGroup)
                                put("body", message)
                            }
                            val dataObj = JSONObject().apply {
                                put("groupId", groupId)
                            }
                            jsonObject.put("notification", notificationObj)
                            jsonObject.put("data", dataObj)
                            jsonObject.put("to", member.fcmToken)

                            callApi(jsonObject)
                        } catch (e: Exception) {
                            Log.e("ChatGroupActivity", "Failed to create JSON for notification", e)
                        }
                    }
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