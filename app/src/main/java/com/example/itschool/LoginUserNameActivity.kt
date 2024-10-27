package com.example.itschool

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.itschool.MainActivity
import com.example.itschool.model.UserModel
import com.example.itschool.utils.FirebaseUtil
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot

class LoginUserNameActivity : AppCompatActivity() {
    private lateinit var userName: EditText
    private lateinit var emailUser: EditText
    private lateinit var btnLogin: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var phoneNumber: String
    private lateinit var userModel: UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_user_name)

        userName = findViewById(R.id.username)
        emailUser = findViewById(R.id.email_user)
        btnLogin = findViewById(R.id.login)
        progressBar = findViewById(R.id.progress_bar)

        phoneNumber = intent.getStringExtra("phone_number") ?: ""
        getUserNameAndEmail()

        btnLogin.setOnClickListener {
            setUsernameAndEmail()
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
            } else {
                userModel = UserModel(phoneNumber, userName.text.toString(), emailUser.text.toString(), Timestamp.now())
            }
        }.addOnFailureListener {
            setInProgress(false)
            // Gérer l'erreur (optionnel)
        }
    }

    private fun emailValidation(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@esatic.edu.ci$".toRegex()
        return emailRegex.matches(email)
    }

    private fun setUsernameAndEmail() {
        setInProgress(true)
        val username = userName.text.toString()
        val email = emailUser.text.toString()

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

        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener { task ->
            setInProgress(false)
            if (task.isSuccessful) {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }
}
