package com.example.itschool

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.itschool.utils.AndroidUtils
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit

class LoginOtpActivity : AppCompatActivity() {

    private lateinit var phoneNumber : String
    private lateinit var auth: FirebaseAuth
    private lateinit var codeOtp : EditText
    private lateinit var nextBtn : Button
    private lateinit var progressBar : ProgressBar
    private val mAuth = FirebaseAuth.getInstance()
    private lateinit var verificationCode : String
    lateinit var resendingToken: ForceResendingToken

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_opt_send)

        // Initialize Firebase Auth
        auth = Firebase.auth

        phoneNumber = intent.extras?.getString("phone_number").toString()
        Toast.makeText(applicationContext, phoneNumber, Toast.LENGTH_SHORT).show()

        progressBar = findViewById(R.id.progress_bar)
        codeOtp = findViewById(R.id.code_otp)
        nextBtn = findViewById(R.id.next_btn)

        progressBar.visibility = ProgressBar.GONE

        val db = FirebaseFirestore.getInstance()
        sendOtp(phoneNumber, false)
    }

    private fun sendOtp(phone : String, isResend : Boolean){
        setInProgress(true)

        val builder = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                   singIn(credential)
                    Log.d("LoginOptActivityLogs", "onVerificationCompleted: $credential")
                    setInProgress(false)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    AndroidUtils.showToast(applicationContext, e.message.toString())
                    Log.d("LoginOptActivityLogs", "onVerificationFailed: ${e.message}")
                    setInProgress(false)
                }

                override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(p0, p1)
                    verificationCode = p0
                    resendingToken = p1
                    AndroidUtils.showToast(applicationContext, "OTP sent successfully, verificationCode is $verificationCode ")
                    setInProgress(false)
                }
            })

        if(isResend){
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build())
        } else{
            PhoneAuthProvider.verifyPhoneNumber(builder.build())
        }


    }

    private fun singIn(credential: PhoneAuthCredential) {
        val code = credential.smsCode
        if (code != null) {
            codeOtp.setText(code)
        }
        AndroidUtils.showToast(applicationContext, "Sign in successfully")
    }

    fun setInProgress(inProgress : Boolean){
        if(inProgress){
            progressBar.visibility = ProgressBar.VISIBLE
            nextBtn.visibility = Button.GONE
        }else {
            nextBtn.visibility = Button.VISIBLE
            progressBar.visibility = ProgressBar.GONE
        }
    }

}