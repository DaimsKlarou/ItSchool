package com.example.itschool.utils

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat

object FirebaseUtil {

    fun currentUserId(): String? {
            return FirebaseAuth.getInstance().uid
        }

    fun isLoggedIn(): Boolean {
        return currentUserId() != null
    }

    fun currentUserDetails(): DocumentReference {
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId()!!)
    }

    fun allUserCollectionReference(): CollectionReference {
        return FirebaseFirestore.getInstance().collection("users")
    }

    fun allClassroomCollectionReference(): CollectionReference {
        return FirebaseFirestore.getInstance().collection("classrooms")
    }

    fun getChatroomReference(chatroomId: String): DocumentReference {
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId)
    }

    fun getChatroomMessageReference(chatroomId: String): CollectionReference {
        return getChatroomReference(chatroomId).collection("chats")
    }

    fun getChatroomId(userId1: String, userId2: String): String {
        return if (userId1.hashCode() < userId2.hashCode()) {
            "${userId1}_$userId2"
        } else {
            "${userId2}_$userId1"
        }
    }

    fun getOtherUserIsOnline(otherUserId: String) : Query {
        return allUserCollectionReference().whereEqualTo("userId", otherUserId).whereEqualTo("isOnline", true)
    }

    fun allChatroomCollectionReference(): CollectionReference {
        return FirebaseFirestore.getInstance().collection("chatrooms")
    }

    fun getOtherUserFromChatroom(userIds: List<String?>): DocumentReference {
        return if (userIds[0] == currentUserId()) {
            allUserCollectionReference().document(userIds[1]!!)
        } else {
            allUserCollectionReference().document(userIds[0]!!)
        }
    }

    fun timestampToString(timestamp: Timestamp?): String {
        return SimpleDateFormat("HH:mm").format(timestamp?.toDate()!!)
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
    }

    fun getCurrentProfilePicStorageRef(): StorageReference {
        return FirebaseStorage.getInstance().getReference("profile_pic")
                .child(currentUserId()!!)
    }

    fun getOtherProfilePicStorageRef(otherUserId: String): StorageReference {
        return FirebaseStorage.getInstance().getReference("profile_pic")
                .child(otherUserId)
    }

    fun getChatroomPicStorageRef(chatroomId: String): StorageReference {
        return FirebaseStorage.getInstance().getReference("chatroom_pic")
                .child(chatroomId)
    }

    fun getAssignmentStorageRef(): StorageReference {
        return FirebaseStorage.getInstance().getReference("assignments")
    }

    //Section dedier a la classe et a toute les manipulations sur les classes
    fun getClassroomReference(classroomId: String): DocumentReference {
        return FirebaseFirestore.getInstance().collection("classrooms").document(classroomId)
    }

    fun allGroupCollectionReference(classroomId: String): CollectionReference {
        return getClassroomReference(classroomId).collection("groups")
    }

}
