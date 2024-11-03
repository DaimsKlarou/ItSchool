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
import com.example.itschool.model.ChatroomModel
import com.example.itschool.model.UserModel
import com.example.itschool.model.ClassroomModel
import com.example.itschool.utils.AndroidUtils
import com.example.itschool.utils.FirebaseUtil
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.SnapshotParser
import com.google.firebase.Timestamp

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
    private var role = "Etudiant"
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
                role = "Professeur"
                matriculate.setText("")

            } else {
                matriculate.visibility = View.GONE
                role = "Etudiant"
            }
        }

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

            formation = niveau + " " + filiere
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
                    role = userModel.role.toString()
                }
            } else {
                userModel = UserModel(phone = phoneNumber, username = userName.text.toString(), email = emailUser.text.toString(), createdTimestamp = Timestamp.now(), userId = FirebaseUtil.currentUserId(), matricule = matriculate.text.toString(), role = role)
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

        userModel.username = username
        userModel.email = email
        userModel.phone = phone
        userModel.role = role
        userModel.matricule = matriculate.text.toString()
        userModel.userId = FirebaseUtil.currentUserId()
        userModel.classe = formation
        userModel.isOnline = true

        AndroidUtils.showToast(this, "votre numero est $phone")

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

        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener { task ->
            setInProgress(false)
            if (task.isSuccessful) {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }

    private fun getOrCreateChatroomModel() {

        val query = FirebaseUtil.allClassroomCollectionReference()
            .whereEqualTo("nomClasse", formation)

        Log.d("SearchUserActivity", "Query valide: $query")
        val parser = SnapshotParser<ClassroomModel> { snapshot ->
            snapshot.toObject(classroomModel::class.java)!!
        }

        val options = FirestoreRecyclerOptions.Builder<ClassroomModel>()
            .setQuery(query, parser)
            .build()

        Log.d("SearchUserActivity", "Options valide: $options")

        FirebaseUtil.getClassroomReference(classroomId).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                classroomModel = (task.result.toObject(classroomModel::class.java)
                    ?: ChatroomModel(
                        classroomId,
                        listOf(FirebaseUtil.currentUserId()),
                        Timestamp.now(),
                        ""
                    ).also { FirebaseUtil.getClassroomReference(classroomId).set(it) }) as ClassroomModel
            }
        }
    }
}
