package com.example.itschool.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.itschool.R
import com.example.itschool.adapter.GroupRecyclerAdapter.ChatModelViewHolder
import com.example.itschool.model.ChatMessageModel
import com.example.itschool.model.UserModel
import com.example.itschool.utils.AndroidUtils
import com.example.itschool.utils.FirebaseUtil
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import org.w3c.dom.Text

class CreateGroupRecyclerAdapter (
    private val options : FirestoreRecyclerOptions<UserModel>,
    private val context: Context,
) : FirestoreRecyclerAdapter<UserModel, CreateGroupRecyclerAdapter.UserModelViewHolder>(options) {

    private val listeUsersGroup = mutableListOf<String>()

    override fun onBindViewHolder(
        holder: UserModelViewHolder,
        position: Int,
        model: UserModel
    ) {
        Log.i("haushd", "asjd")
        if (model.userId != FirebaseUtil.currentUserId()) {
            holder.userNameText.text = model.username
            holder.numeroText.text = model.phone

            holder.checkUser.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    // Action à exécuter lorsque le RadioButton est sélectionné
                    Log.d("CheckBoxSelection", "Le CheckBox est sélectionné ")
                    // Exécute ton action ici
                    AndroidUtils.showToast(context, "Option unique sélectionnée")

                    if (!listeUsersGroup.contains(model.userId)) {
                        listeUsersGroup.add(model.userId!!)
                        Log.d("CheckBoxSelection", "Utilisateur ajouté : ${model.userId}")
                        Log.d("CheckBoxSelection", "la liste des utilisateurs selectionnees : $listeUsersGroup")
                    }

                } else {
                    listeUsersGroup.remove(model.userId)
                    Log.d("UserSelection", "Utilisateur retiré : ${model.userId}")
                    Log.d("RadioButtonSelection", "Le RadioButton est désélectionné")
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserModelViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_membre_groupe, parent, false)
        return UserModelViewHolder(view)
    }

    inner class UserModelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userNameText: TextView = itemView.findViewById(R.id.user_name_text)
        var numeroText: TextView = itemView.findViewById(R.id.numero_text)
        var checkUser : CheckBox = itemView.findViewById(R.id.checkUser)
    }

    fun getSelectedUserIds(): List<String> = listeUsersGroup
}