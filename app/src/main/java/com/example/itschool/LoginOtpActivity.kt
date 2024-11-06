package com.example.itschool

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
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
import java.util.Timer
import java.util.TimerTask
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
    private var timeoutSeconds = 60L
    private lateinit var resendOtp : TextView

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
        resendOtp = findViewById(R.id.resend_code)

        progressBar.visibility = ProgressBar.GONE
        sendOtp(phoneNumber, false)

        nextBtn.setOnClickListener {
            val code = codeOtp.text.toString()
            if (code.isEmpty() || code.length < 6) {
                codeOtp.error = "Wrong OTP"
                codeOtp.requestFocus()
                return@setOnClickListener
            }
            else{
                val credential = PhoneAuthProvider.getCredential(verificationCode, code)
                singIn(credential)
                setInProgress(true)
                sendOtp(phoneNumber, true)
            }
        }

        resendOtp.setOnClickListener {
            sendOtp(phoneNumber, true)
        }
    }

    private fun sendOtp(phone : String, isResend : Boolean){
        startResendTimer()
        setInProgress(true)

        val builder = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                   singIn(credential)
                    Log.d("LoginOptActivityLogs", "onVerificationCompleted: $credential")
                    Log.d("LoginOptActivityLogs", "Sing In successffully!!!")
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
                    Log.d("LoginOptActivityLogs", "onCodeSent: $p0 et le token de resending est $p1")
                    setInProgress(false)
                }
            })

        if(isResend){
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build())
        } else{
            PhoneAuthProvider.verifyPhoneNumber(builder.build())
        }

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

    private fun singIn(credential: PhoneAuthCredential) {
        setInProgress(true)
        mAuth.signInWithCredential(credential).addOnCompleteListener { onCompleteListener ->
            setInProgress(false)
            if (onCompleteListener.isSuccessful) {
                Log.d("LoginOptActivityLogs", "singIn: ${onCompleteListener.result}")
                AndroidUtils.showToast(applicationContext, "Sign in successfully, le numero est $phoneNumber")
                val intent = Intent(this, LoginUserNameActivity::class.java)
                intent.putExtra("phone_number", phoneNumber)
                startActivity(intent)
            } else {
                Log.d("LoginOptActivityLogs", "singIn: ${onCompleteListener.exception}")
                AndroidUtils.showToast(applicationContext, onCompleteListener.exception.toString())
            }
        }
    }

    private fun startResendTimer() {
        resendOtp.isEnabled = false
        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    timeoutSeconds--
                    resendOtp.text = "Resend OTP in $timeoutSeconds seconds"

                    if (timeoutSeconds == 0L) {
                        resendOtp.isEnabled = true
                        resendOtp.text = "Resend OTP"
                        timeoutSeconds = 60L
                        timer.cancel()
                    }
                }
            }
        }, 0, 1000)
    }

}