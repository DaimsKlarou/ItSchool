package com.example.itschool

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.itschool.R
import com.example.itschool.model.UserModel
import com.example.itschool.utils.FirebaseUtil
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var searchButton: ImageButton
    private val chatFragment = ChatFragment()
    private val profileFragment = ProfileFragment()
    private val groupFragment = GroupFragment()
    private val evaluationFragment = EvaluationFragment()
    private val args = Bundle()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        searchButton = findViewById(R.id.main_search_btn)

        searchButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, SearchUserActivity::class.java))
        }

        var currentUser = UserModel()
        FirebaseUtil.currentUserDetails().get().addOnSuccessListener {
            currentUser = it.toObject(UserModel::class.java)!!
        }

        FirebaseUtil.currentClasseIdInString{ classId ->
            if (classId != null) {
                Log.d("MainActivityArguments", "l'Id de la classe est $classId")
                args.putString("classroomId", classId)
                Log.d("MainActivityArguments", "les arguments sur l'utilisateur est $currentUser")
                args.putString("userRole", currentUser.role)
                Log.d("MainActivityArguments", "Args est $args")

                groupFragment.arguments = args
                Log.d("MainActivityArguments", "Arguments for GroupFragment: ${groupFragment.arguments}")

                evaluationFragment.arguments = args
            }else {
                Log.d("MainActivityArguments", "Impossible de récupérer la classe de l'utilisateur")
            }
        }



        bottomNavigationView.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { item ->

            when (item.itemId) {
                R.id.menu_chat -> supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frame_layout, chatFragment).commit()

                R.id.menu_groups -> supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frame_layout, groupFragment).commit()

                R.id.menu_evaluation -> supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frame_layout, evaluationFragment).commit()

                R.id.menu_profile -> supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frame_layout, profileFragment).commit()
            }
            true
        })

        bottomNavigationView.selectedItemId = R.id.menu_chat

        getFCMToken()
        FirebaseUtil.currentUserDetails().update("isOnline", true)
    }

    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                FirebaseUtil.currentUserDetails().update("fcmToken", token)
            }
        }
    }

}
