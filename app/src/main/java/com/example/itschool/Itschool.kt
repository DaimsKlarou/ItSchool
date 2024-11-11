package com.example.itschool

import android.app.Application
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.itschool.model.UserModel
import com.example.itschool.utils.FirebaseUtil
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Itschool : Application(), DefaultLifecycleObserver {

    lateinit var currentUser : UserModel

    override fun onCreate() {
        super<Application>.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        if (FirebaseUtil.isLoggedIn()) {
             FirebaseUtil.currentUserDetails().update("online", true)

            FirebaseUtil.infoCurrentUser {
                Log.d("VerificationsGlobale", "les info sur l'utilisateur sont : ${it}")
                currentUser = it
                Log.d("VerificationsGlobale", "les info sur l'utilisateur sont : ${currentUser}")
            }
        }
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        if (FirebaseUtil.isLoggedIn()) {
            FirebaseUtil.currentUserDetails().update("online", true)
        }
    }


    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        if (FirebaseUtil.isLoggedIn()) {
            FirebaseUtil.currentUserDetails().update("online", false)
            FirebaseUtil.currentUserDetails().update("lastConnection", Timestamp.now())
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        if (FirebaseUtil.isLoggedIn()) {
            FirebaseUtil.currentUserDetails().update("online", true)
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        if (FirebaseUtil.isLoggedIn()) {
            FirebaseUtil.currentUserDetails().update("online", false)
            FirebaseUtil.currentUserDetails().update("lastConnection", Timestamp.now() )
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        ProcessLifecycleOwner.get().lifecycle.removeObserver(this)
        if (FirebaseUtil.isLoggedIn()) {
            FirebaseUtil.currentUserDetails().update("online", false)
            FirebaseUtil.currentUserDetails().update("lastConnection", Timestamp.now())
        }
    }

}
