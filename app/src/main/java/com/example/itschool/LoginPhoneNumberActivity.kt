package com.example.itschool

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginPhoneNumberActivity : AppCompatActivity() {
    private lateinit var btn_send: Button
    private lateinit var login_country_code: com.hbb20.CountryCodePicker
    private lateinit var login_phone_number: EditText
    private lateinit var login_otp_progressbar: ProgressBar

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_phone_number)

        btn_send = findViewById(R.id.login_otp_btn)
        login_country_code = findViewById(R.id.login_country_code)
        login_phone_number = findViewById(R.id.login_phone_number)
        login_otp_progressbar = findViewById(R.id.login_otp_progressbar)

        login_otp_progressbar.setVisibility(View.GONE)

        login_country_code.registerCarrierNumberEditText(login_phone_number)

        btn_send.setOnClickListener {
            if(!login_country_code.isValidFullNumber()) {
                login_phone_number.error = "Invalid Phone Number"
                return@setOnClickListener
            }
           val intent = Intent(this, LoginOtpActivity::class.java)
            intent.putExtra("phone_number", login_country_code.fullNumberWithPlus)
            startActivity(intent)
        }
    }
}