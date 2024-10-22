package com.example.itschool

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class LoginOtpActivity : AppCompatActivity() {

    private lateinit var phoneNumber : String
    private lateinit var auth: FirebaseAuth
    private lateinit var codeOtp : EditText
    private lateinit var nextBtn : Button
    private lateinit var progressBar : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_opt_send)

        // Initialize Firebase Auth
        auth = Firebase.auth

        phoneNumber = getIntent().getExtras()?.getString("phone_number").toString()
        Toast.makeText(getApplicationContext(), phoneNumber, Toast.LENGTH_SHORT).show()

        progressBar = findViewById(R.id.progress_bar)
        codeOtp = findViewById(R.id.code_otp)
        nextBtn = findViewById(R.id.next_btn)

        progressBar.setVisibility(ProgressBar.GONE)

        val db = FirebaseFirestore.getInstance()
        sendOtp(phoneNumber, false)
    }

    fun sendOtp(phone : String, isResend : Boolean){
        setInProgress(true)

    }

    fun setInProgress(inProgress : Boolean){
        if(inProgress){
            progressBar.setVisibility(ProgressBar.VISIBLE)
            nextBtn.setVisibility(Button.GONE)
        }else {
            nextBtn.setVisibility(Button.VISIBLE)
            progressBar.setVisibility(ProgressBar.GONE)
        }
    }
}