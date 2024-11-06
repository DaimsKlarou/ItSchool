package com.example.itschool

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.itschool.utils.FirebaseUtil

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            if(FirebaseUtil.isLoggedIn()){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else{
                val intent = Intent(this, LoginPhoneNumberActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 3000)

    }
}