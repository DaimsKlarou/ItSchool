package com.example.itschool.utils

import android.util.Log
import com.example.itschool.model.ClassroomModel
import com.example.itschool.model.GrouproomModel
import com.example.itschool.model.UserModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
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


    /* ############################################################################
    /#
    /#
    /#Section dedier a la classe et a toute les manipulations sur les classes
    /#
    /#
    /###############################################################################
    */

    fun getClassroomReference(classroomId: String): DocumentReference {
        return FirebaseFirestore.getInstance().collection("classes").document(classroomId)
    }
    fun allClassroomCollectionReference(): CollectionReference {
        return FirebaseFirestore.getInstance().collection("classes")
    }

    fun allGroupCollectionReference(classroomId: String): CollectionReference {
        return getClassroomReference(classroomId).collection("groups")
    }

    fun getAssignmentStorageRef(): StorageReference {
        return FirebaseStorage.getInstance().getReference("assignments")
    }

    fun getGroupCollectionReference(classroomId: String, groupId: String): DocumentReference {
        return getClassroomReference(classroomId).collection("groups").document(groupId)
    }

    fun currentClasseId(): String? {
        return FirebaseAuth.getInstance().uid
    }

    fun currentClasseIdInString(callcallback: (String) -> Unit) : String? {
        var classe : String? = null

        FirebaseFirestore.getInstance().collection("classes")
            .whereArrayContains("userIds", currentUserId()!!).get().addOnSuccessListener { task ->
                if (!task.isEmpty) {
                    Log.d("FirebaseUtil", "Document is not empty")
                    Log.d("FirebaseUtil", "Document is ${task.documents[0].id}")
                    callcallback(task.documents[0].id)
                }else {
                    Log.d("FirebaseUtil", "Document is empty")
                    callcallback(null.toString())
                }
            }

        Log.d("FirebaseUtil", "vous verrez la classe courante est ${classe}")
        return classe
    }

    fun currentClasseDetails(classroomId: String): DocumentReference {
        return FirebaseFirestore.getInstance().collection("classes").document(classroomId)
    }

    fun currentGroupId(): String? {
        return FirebaseAuth.getInstance().uid
    }

    fun currentGroupDetails(classroomId: String): DocumentReference {
        return currentClasseDetails(classroomId).collection("groups").document(currentGroupId()!!)
    }

    fun getUsersWithRole(classe: String, onComplete: (List<UserModel>) -> Unit) {
        val db = FirebaseFirestore.getInstance()

        db.collection("users")
            .whereEqualTo("classe", classe)
            .get()
            .addOnSuccessListener { result: QuerySnapshot ->
                val users = result.documents.mapNotNull { it.toObject(UserModel::class.java) }
                onComplete(users)
            }
            .addOnFailureListener { exception ->
                println("Erreur lors de la récupération des utilisateurs : ${exception.message}")
                onComplete(emptyList())
            }
    }

    fun getAllGroupFromClasse(classe: String): DocumentReference {
        return getClassroomReference(classe)
    }

    fun AllClassOfUserProf(profId: String) : ArrayList<String> {

        val listClasse = ArrayList<String>()
        val classes = FirebaseFirestore.getInstance().collection("classes")
            .whereArrayContains("userIds", profId)
        classes.get().addOnSuccessListener { task ->
            for (document in task.documents) {
               val classe = document.toObject(ClassroomModel::class.java)
                Log.d("FirebaseUtil", "la classe est ${classe?.nomClasse}")
                Log.d("FirebaseUtil", "l'id de la classe est ${classe?.classroomId}")
                listClasse.add(classe?.nomClasse!!)
                Log.d("FirebaseUtil", "l'ensemble des classes du professeurs $profId = ${listClasse}")
            }
        }
        return listClasse
    }

    fun AllGroupOfUserProf(profId: String) : ArrayList<String> {

        val listMatiere = ArrayList<String>()
        allClassroomCollectionReference().get().addOnSuccessListener {task ->
            if(!task.isEmpty){
                for (document in task.documents) {
                    document.reference.collection("groups")
                        .whereArrayContains("userIds", profId).get()
                        .addOnSuccessListener { task ->
                            if(!task.isEmpty) {
                                for (documentGroup in task.documents) {
                                    val group = documentGroup.toObject(GrouproomModel::class.java)
                                    listMatiere.add(
                                        group?.nomGroup!!
                                    )
                                }
                            }
                        }
                }
            }
        }

        return listMatiere
    }

    fun getAssignmentsFromClassroom(classroomId: String): CollectionReference {
        return getClassroomReference(classroomId).collection("assignments")
    }


}
