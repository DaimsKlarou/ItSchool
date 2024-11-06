package com.example.itschool

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.itschool.model.UserModel
import com.example.itschool.model.ClassroomModel
import com.example.itschool.model.GrouproomModel
import com.example.itschool.utils.AndroidUtils
import com.example.itschool.utils.FirebaseUtil
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.auth.User

class LoginUserNameActivity : AppCompatActivity() {
    private lateinit var userName: EditText
    private lateinit var emailUser: EditText
    private lateinit var btnLogin: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var phoneNumber: String
    private lateinit var userModel: UserModel
    private lateinit var matriculate: EditText
    private lateinit var autoCompleteTxt: AutoCompleteTextView
    private lateinit var autoCompleteNiveau : AutoCompleteTextView
    private lateinit var autoCompleteFiliere : AutoCompleteTextView

    private val items = arrayOf("Professeur", "Etudiant")
    private val NiveauItems = arrayOf("Licence 1", "Licence 2", "Licence 3", "Master 1", "master 2")
    private val licenceItems = arrayOf("SIGL", "SRIT", "TWIN")
    private val masterItems = arrayOf("MBDS", "BIHAR", "SIGL", "SITW", "RTEL", "ERIS")
    private var roleUser : String = "Etudiant"
    private lateinit var formation : String
    private lateinit var niveau : String
    private lateinit var filiere : String

    private lateinit var classroomModel: ClassroomModel
    private lateinit var classroomId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_user_name)

        userName = findViewById(R.id.username)
        emailUser = findViewById(R.id.email_user)
        btnLogin = findViewById(R.id.login)
        progressBar = findViewById(R.id.progress_bar)
        matriculate = findViewById(R.id.matricule)
        matriculate.visibility = View.GONE
        phoneNumber = intent.extras?.getString("phone_number").toString()
        AndroidUtils.showToast(applicationContext, phoneNumber)

        getUserNameAndEmail()

        btnLogin.setOnClickListener {
            setUsernameAndEmail()
        }

        autoCompleteTxt = findViewById(R.id.auto_complete_txt)
        val adapterItems = ArrayAdapter(this, R.layout.list_item, items)
        autoCompleteTxt.setAdapter(adapterItems)

        autoCompleteTxt.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            if (selectedItem == "Professeur") {
                matriculate.visibility = View.VISIBLE
                roleUser = "Professeur"
                matriculate.setText("")

            } else {
                matriculate.visibility = View.GONE
                roleUser = "Etudiant"
            }
        }

        //selection du niveau et de la filiere pour former la classe
        autoCompleteNiveau = findViewById(R.id.niveau)
        val adapterNiveau = ArrayAdapter(this, R.layout.list_item, NiveauItems)
        autoCompleteNiveau.setAdapter(adapterNiveau)
        autoCompleteFiliere = findViewById(R.id.filiere)

        autoCompleteNiveau.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            niveau = selectedItem

            val itemsToShow = if (filiereValidation(niveau)) {
                licenceItems
            } else {
                masterItems
            }

            val adapter = ArrayAdapter(this, R.layout.list_item, itemsToShow)
            autoCompleteFiliere.setAdapter(adapter)
            autoCompleteFiliere.text.clear()
        }


        autoCompleteFiliere.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            filiere = selectedItem

            formation = "$niveau $filiere"
            AndroidUtils.showToast(this, "la formation choisi est $formation")
        }

    }

    private fun setInProgress(inProgress: Boolean) {
        progressBar.visibility = if (inProgress) ProgressBar.VISIBLE else ProgressBar.GONE
        btnLogin.visibility = if (inProgress) Button.GONE else Button.VISIBLE
    }

    private fun getUserNameAndEmail() {
        setInProgress(true)
        FirebaseUtil.currentUserDetails().get().addOnSuccessListener { document ->
            setInProgress(false)
            val user = document.toObject(UserModel::class.java)
            if (user != null) {
                userModel = user
                userName.setText(userModel.username)
                emailUser.setText(userModel.email)
                phoneNumber = userModel.phone.toString()
                autoCompleteTxt.setText(userModel.role, false)
                if (userModel.matricule != null) {
                    matriculate.visibility = View.VISIBLE
                    matriculate.setText(userModel.matricule)
                }
                if (userModel.role != null) {
                    roleUser = userModel.role.toString()
                }
            } else {
                userModel = UserModel(phone = phoneNumber, username = userName.text.toString(), email = emailUser.text.toString(), createdTimestamp = Timestamp.now(), userId = FirebaseUtil.currentUserId(), matricule = matriculate.text.toString(), role = roleUser)
            }
        }.addOnFailureListener {
            setInProgress(false)
            AndroidUtils.showToast(this, "Something went wrong")
        }
    }

    private fun emailValidation(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@esatic.edu.ci$".toRegex()
        return emailRegex.matches(email)
    }

    private fun filiereValidation(niveau:String) : Boolean {
        val filiereRegex = "^Licence.*".toRegex()
        return filiereRegex.containsMatchIn(niveau)
    }

    private fun setUsernameAndEmail() {
        setInProgress(true)
        val username = userName.text.toString()
        val email = emailUser.text.toString()
        val phone = intent.extras?.getString("phone_number").toString()

        if (username.isEmpty() || email.isEmpty() || username.length < 3) {
            userName.error = "The username or email is empty or too short"
            setInProgress(false)
            return
        }

        if (!emailValidation(email)) {
            emailUser.error = "The email is not valid"
            setInProgress(false)
            return
        }

        userModel.apply {
            this.username = username
            this.email = email
            this.phone = phone
            this.matricule = matriculate.text.toString()
            this.userId = FirebaseUtil.currentUserId()
            this.classe = formation
            this.isOnline = true
            this.role = roleUser
        }

        AndroidUtils.showToast(this, "Your phone number is $phone")

        getOrCreateClassroomModel()

        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener { task ->
            setInProgress(false)
            if (task.isSuccessful) {
                val intent = Intent(this, MainActivity::class.java)
                Log.d("LoginUserName", "La classe de l'utilisateur est ${classroomId}")
                intent.putExtra("classroomId", classroomId)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                startActivity(intent)
            } else {
                AndroidUtils.showToast(this, "Failed to save user data")
            }
        }
    }

    private fun getOrCreateGroupRoomModel() {
        Log.d("LoginUserName", "La matiere du prof est ${matriculate.text.toString()}")
        val classroomId = classroomModel.classroomId.toString()

        AndroidUtils.showToast(this, "L'ID de la classe est $classroomId")
        Log.d("LoginUserName", "L'ID de la classe est $classroomId")

        val query = FirebaseUtil.getClassroomReference(classroomId).collection("groups")
            .whereEqualTo("nomGroup", matriculate.text.toString())

        Log.d("LoginUserName", "la query a bien ete cree avec success ")

        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (!task.result.isEmpty) {
                    Log.d("LoginUserName", "Group already exist")
                    AndroidUtils.showToast(this, "Group already exist")
                } else {
                    val groupRoomModel = GrouproomModel(
                        grouproomId = FirebaseUtil.currentGroupId(),
                        userIds = listOf(FirebaseUtil.currentUserId()),
                        nomGroup = matriculate.text.toString(),
                        classId = classroomId,
                        profId = FirebaseUtil.currentUserId(),
                    )

                    Log.d("LoginUserName", "Group created: $groupRoomModel")
                    Log.d("LoginUserName", "Group created: ${FirebaseUtil.currentGroupId()}")

                    FirebaseUtil.currentGroupDetails(classroomId).set(groupRoomModel)
                        .addOnSuccessListener {
                            Log.d("LoginUserName", "Group created")
                            AndroidUtils.showToast(this, "Group created")
                        }
                }
            } else {
                Log.e("LoginUserName", "Error checking group", task.exception)
            }
        }

    }

    private fun getOrCreateClassroomModel() {
        // Requête pour vérifier si une classe avec le nom spécifié existe déjà
        val query = FirebaseUtil.allClassroomCollectionReference()
            .whereEqualTo("nomClasse", formation)

        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Si des résultats sont trouvés, cela signifie que la classe existe déjà
                if (!task.result.isEmpty) {
                    // Récupérer la première classe correspondant à la requête
                    classroomModel = task.result.documents[0].toObject(ClassroomModel::class.java)!!
                    Log.d("LoginUserName", "Classe existante trouvée: $classroomModel")
                    classroomId = task.result.documents[0].id
                    classroomModel.userIds = classroomModel.userIds.plus(FirebaseUtil.currentUserId())
                    FirebaseUtil.getClassroomReference(classroomId).set(classroomModel).addOnSuccessListener {
                        Log.d("LoginUserName", "L'utilisateur a rejoins la classe")
                        AndroidUtils.showToast(this, "L'utilisateur a rejoins la classe")
                    }
                } else {
                    // Si aucun résultat n'est trouvé, créer une nouvelle classe
                    classroomModel = ClassroomModel(
                        classroomId = FirebaseUtil.currentClasseId(),
                        nomClasse = formation,
                        userIds = listOf(FirebaseUtil.currentUserId()),
                        createdTimestamp = Timestamp.now(),
                    )

                    // Enregistrer la nouvelle classe dans Firestore
                    Log.d("LoginUserName", "Classe créée: $classroomModel")
                    Log.d("LoginUserName", "L'ID de la classe est ${FirebaseUtil.currentClasseId()}")
                    Log.d("LoginUserName", "les details de la classe sont: ${FirebaseUtil.currentClasseDetails(classroomModel.classroomId!!)}")
                    FirebaseUtil.currentClasseDetails(classroomModel.classroomId!!).set(classroomModel)
                        .addOnSuccessListener {
                            Log.d("LoginUserName", "Nouvelle classe créée avec succès: $classroomModel")
                            classroomId = classroomModel.classroomId.toString()
                        }
                        .addOnFailureListener { e ->
                            Log.e("LoginUserName", "Erreur lors de la création de la classe", e)
                        }
                }
                Log.d("LoginUserName", "L'ID de la classe est ${FirebaseUtil.currentClasseId()}")
                if (roleUser == "Professeur"){
                    Log.d("LoginUserName", "Le role de l'utilisateur est professeur nous allons donc creer sa matiere")
                    getOrCreateGroupRoomModel()
                } else{
                    Log.d("LoginUserName", "Le role de l'utilisateur est etudiant nous allons donc l'ajouter a sa classe et son group")
                    addUserToAllGroupsInClass(classroomId, FirebaseUtil.currentUserId()!!)
                }
            } else {
                Log.e("LoginUserName", "Erreur lors de l'exécution de la requête", task.exception)
            }
        }
    }

    private fun addUserToAllGroupsInClass(classroomId: String, userId: String) {
        val groupsRef = FirebaseUtil.allGroupCollectionReference(classroomId)
        groupsRef.get().addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot.documents) {
                document.reference.update("userIds", FieldValue.arrayUnion(userId))
                    .addOnSuccessListener {
                        Log.d("LoginUserName", "Utilisateur ajouté au groupe ${document.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.e("LoginUserName", "Erreur ajout de l'utilisateur au groupe", e)
                    }
            }
        }.addOnFailureListener { e ->
            Log.e("LoginUserName", "Erreur lors de la récupération des groupes", e)
        }
    }

}
