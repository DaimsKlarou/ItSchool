package com.example.itschool

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.itschool.model.Assignment
import com.example.itschool.utils.AndroidUtils
import com.example.itschool.utils.FirebaseUtil
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class AssignmentActivity : AppCompatActivity() {

    private lateinit var autoCompletetype: AutoCompleteTextView
    private lateinit var editTextTitle: EditText
    private lateinit var editTextDate: EditText
    private lateinit var editTextClasse: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var buttonCreate: Button
    private val TypeItems = arrayOf("Devoir", "Interrogation", "Examen")

    private lateinit var type: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_assigment)

        autoCompletetype = findViewById(R.id.editTextType)
        editTextTitle = findViewById(R.id.editTextTitle)
        editTextDate = findViewById(R.id.editTextDate)
        editTextClasse = findViewById(R.id.editTextClasse)
        editTextDescription = findViewById(R.id.editTextDescription)
        buttonCreate = findViewById(R.id.buttonCreate)

        buttonCreate.setOnClickListener {
            createAssignment()
        }


        val adapterType = ArrayAdapter(this, R.layout.list_item, TypeItems)
        autoCompletetype.setAdapter(adapterType)

        autoCompletetype.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            type = selectedItem
        }
    }

    private fun createAssignment() {
        val title = editTextTitle.text.toString().trim()
        val date = editTextDate.text.toString().trim()
        val classe = editTextClasse.text.toString().trim()
        val description = editTextDescription.text.toString().trim()

        if (type.isEmpty() || title.isEmpty() || date.isEmpty() || classe.isEmpty() || description.isEmpty()) {
            AndroidUtils.showToast(this, "Veuillez remplir tous les champs.")
            return
        }

        // Convertir la date au format DateTime de Google
        val assignmentDate = Date(2024, 11 ,2 )
        val assignment = Assignment(type, title, assignmentDate, classe, description, FirebaseUtil.currentUserId())

        // Sauvegarder l'assignment dans Firestore
        saveAssignment(assignment)
    }

    private fun saveAssignment(assignment: Assignment) {
        val db = FirebaseFirestore.getInstance()
        db.collection("assignments").add(assignment)
            .addOnSuccessListener {
                AndroidUtils.showToast(this, "Évaluation créée avec succès!")
                finish()
            }
            .addOnFailureListener { e ->
               AndroidUtils.showToast(this, "Erreur: ${e.message}")
            }
    }

}