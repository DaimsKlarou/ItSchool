package com.example.itschool

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.itschool.R
import com.example.itschool.model.UserModel
import com.example.itschool.utils.FirebaseUtil
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
    private lateinit var fabAddConversation: FloatingActionButton
    private lateinit var app : Itschool

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        searchButton = findViewById(R.id.main_search_btn)
        fabAddConversation = findViewById(R.id.newConversation)

        searchButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, SearchUserActivity::class.java))
        }

        app = application as Itschool

        FirebaseUtil.currentClasseIdInString{ classId ->
            if (classId != null) {
                Log.d("MainActivityArguments", "l'Id de la classe est $classId")
                args.putString("classroomId", classId)
                Log.d("MainActivityArguments", "les arguments sur l'utilisateur est $app.currentUser")
                args.putString("userRole", app.currentUser.role)
                args.putString("userId", app.currentUser.userId)

                groupFragment.arguments = args
                Log.d("MainActivityArguments", "Arguments for GroupFragment: ${groupFragment.arguments}")

                evaluationFragment.arguments = args
            }else {
                Log.d("MainActivityArguments", "Impossible de récupérer la classe de l'utilisateur")
            }
        }

        Log.d("MainActivityArguments", "Arguments for EvaluationFragment: ${app.currentUser}")
        if(app.currentUser.role == "Professeur"){
            bottomNavigationView.menu.findItem(R.id.menu_classes).isVisible = true
        } else{
            bottomNavigationView.menu.findItem(R.id.menu_classes).isVisible = false
        }

        bottomNavigationView.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { item ->

            when (item.itemId) {
                R.id.menu_chat -> {
                    supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frame_layout, chatFragment).commit()
                    fabAddConversation.visibility = View.VISIBLE
                    searchButton.visibility = View.VISIBLE
                    fabAddConversation.setOnClickListener {
                        val intent = Intent(this@MainActivity, NewConversationActivity::class.java)
                        intent.putExtra("isGroupConversation", false)
                        intent.putExtra("classe", app.currentUser.classe)
                        startActivity(intent)
                    }
                }

                R.id.menu_groups -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frame_layout, groupFragment).commit()
                    fabAddConversation.visibility = View.VISIBLE
                    searchButton.visibility = View.VISIBLE

                    fabAddConversation.setOnClickListener {
                        val intent = Intent(this@MainActivity, NewGroupActivity::class.java)
                        startActivity(intent)
                    }
                }

                R.id.menu_evaluation -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frame_layout, evaluationFragment).commit()
                    fabAddConversation.visibility = View.GONE
                    searchButton.visibility = View.GONE
                }

                R.id.menu_profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frame_layout, profileFragment).commit()
                    fabAddConversation.visibility = View.GONE
                    searchButton.visibility = View.GONE
                }

                R.id.menu_classes -> {
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.main_frame_layout, ClassesFragment()).commit()
                    fabAddConversation.visibility = View.VISIBLE
                    searchButton.visibility = View.GONE
                }
            }
            true
        })

        bottomNavigationView.selectedItemId = R.id.menu_chat

        getFCMToken()
        FirebaseUtil.currentUserDetails().update("online", true)
    }

    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCMToken", "Votre FCM Token est = $token")
                FirebaseUtil.currentUserDetails().update("fcmToken", token)
            }
        }
    }

}
