package com.example.itschool.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.ImageView
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.itschool.model.UserModel
import com.google.firebase.Timestamp
import java.util.Date


class AndroidUtils {

    companion object {
        fun showToast(context: Context, message: String) {
            Toast.makeText(context, message, LENGTH_LONG).show()
        }

        fun passUserModelAsIntent(intent: Intent, model: UserModel) {
            with(intent) {
                putExtra("username", model.username)
                putExtra("phone", model.phone)
                putExtra("userId", model.userId)
                putExtra("fcmToken", model.fcmToken)
                putExtra("isOnline", model.isOnline)
                putExtra("lastConnection", model.lastConnection?.toDate()?.time )
            }
        }

        fun getUserModelFromIntent(intent: Intent): UserModel {
            val userModel: UserModel = UserModel()
            userModel.username = intent.getStringExtra("username").toString()
            userModel.phone = intent.getStringExtra("phone").toString()
            userModel.userId = intent.getStringExtra("userId").toString()
            userModel.fcmToken = intent.getStringExtra("fcmToken").toString()
            userModel.isOnline = intent.getBooleanExtra("isOnline", false)
            // Récupérer lastConnection comme Long puis convertir en Timestamp
            val lastConnectionLong = intent.getLongExtra("lastConnection", 0L)
            userModel.lastConnection = Timestamp(Date(lastConnectionLong))
            return userModel
        }

        fun setProfilePic(context: Context?, imageUri: Uri?, imageView: ImageView?) {
            if (imageView != null) {
                if (context != null) {
                    Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform())
                        .into(imageView)
                }
            }
        }

    }

}