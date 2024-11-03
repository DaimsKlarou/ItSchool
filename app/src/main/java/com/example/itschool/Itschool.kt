package com.example.itschool

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.itschool.utils.FirebaseUtil
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Itschool : Application(), DefaultLifecycleObserver {

    override fun onCreate() {
        super<Application>.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        FirebaseUtil.currentUserDetails().update("isOnline", true)
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        FirebaseUtil.currentUserDetails().update("isOnline", true)
    }


    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        FirebaseUtil.currentUserDetails().update("isOnline", false)
        FirebaseUtil.currentUserDetails().update("lastConnection", Timestamp.now() )
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        FirebaseUtil.currentUserDetails().update("isOnline", true)
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        FirebaseUtil.currentUserDetails().update("isOnline", false)
        FirebaseUtil.currentUserDetails().update("lastConnection", Timestamp.now() )
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        ProcessLifecycleOwner.get().lifecycle.removeObserver(this)
        FirebaseUtil.currentUserDetails().update("isOnline", false)
        FirebaseUtil.currentUserDetails().update("lastConnection", Timestamp.now() )
    }
}
