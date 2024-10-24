package com.example.itschool.utils

import android.content.Context
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG

class AndroidUtils {

    companion object {
        fun showToast(context: Context, message: String) {
            Toast.makeText(context, message, LENGTH_LONG).show()
        }
    }

}