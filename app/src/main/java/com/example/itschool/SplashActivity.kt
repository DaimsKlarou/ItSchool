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
import com.example.itschool.model.UserModel
import com.example.itschool.utils.AndroidUtils
import com.example.itschool.utils.FirebaseUtil

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        if(FirebaseUtil.isLoggedIn() && getIntent().getExtras() != null){
            //from notification

            val userId = getIntent().getExtras()?.getString("userId")
            FirebaseUtil.allUserCollectionReference().document(userId!!).get().addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val model : UserModel = task.result.toObject(UserModel::class.java)!!

                    val mainIntent = Intent(this, MainActivity::class.java)
                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(mainIntent)

                    val intent = Intent(this, ChatActivity::class.java).apply {
                        AndroidUtils.passUserModelAsIntent(this, model)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    startActivity(intent)
                    finish()
                }
            }
        } else{
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
}