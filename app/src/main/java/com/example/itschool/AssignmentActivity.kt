package com.example.itschool

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
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
import java.util.Calendar
import java.util.Date

class AssignmentActivity : AppCompatActivity() {

    private lateinit var autoCompletetype: AutoCompleteTextView
    private lateinit var editTextTitle: EditText
    private lateinit var editTextDate: EditText
    private lateinit var editTextClasse: AutoCompleteTextView
    private lateinit var editTextMatiere: AutoCompleteTextView
    private lateinit var editTextDescription: EditText
    private lateinit var buttonCreate: Button
    private lateinit var dateSelected : Date
    private val TypeItems = arrayOf("Devoir", "Interrogation", "Examen")

    private lateinit var type: String
    private lateinit var classe : String
    private lateinit var listClasse : ArrayList<String>
    private lateinit var matiere : String
    private lateinit var listMatiere : ArrayList<String>

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
        editTextMatiere = findViewById(R.id.editTextMatiere)

        buttonCreate.setOnClickListener {
            createAssignment()
        }

        editTextDate.setOnClickListener {
            showDatePickerDialog()
        }

        listClasse = FirebaseUtil.AllClassOfUserProf(FirebaseUtil.currentUserId()!!)
        listMatiere = FirebaseUtil.AllGroupOfUserProf(FirebaseUtil.currentUserId()!!)

        val adapterType = ArrayAdapter(this, R.layout.list_item, TypeItems)
        autoCompletetype.setAdapter(adapterType)

        val adapterClasse = ArrayAdapter(this, R.layout.list_item, listClasse)
        editTextClasse.setAdapter(adapterClasse)

        val adapterMatiere = ArrayAdapter(this, R.layout.list_item, listMatiere)
        editTextMatiere.setAdapter(adapterMatiere)

        autoCompletetype.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            type = selectedItem
        }

        editTextClasse.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            classe = selectedItem
        }

        editTextMatiere.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            matiere = selectedItem
        }
    }

    private fun createAssignment() {
        val title = editTextTitle.text.toString().trim()
        val date = editTextDate.text.toString().trim()
        val classe = editTextClasse.text.toString().trim()
        val description = editTextDescription.text.toString().trim()

        if (type.isEmpty() || title.isEmpty() || date.isEmpty() || classe.isEmpty() || description.isEmpty() || matiere.isEmpty()) {
            AndroidUtils.showToast(this, "Veuillez remplir tous les champs.")
            return
        }

        val assignment = Assignment(type = type, title=title, date=dateSelected, classe=classe, description=description, userId = FirebaseUtil.currentUserId(), matiere = matiere)
        Log.d("AssignmentActivity", "Assignment a sauvegarder: $assignment")
        // Sauvegarder l'assignment dans Firestore
        saveAssignment(assignment)
    }

    private fun saveAssignment(assignment: Assignment) {

        val query = FirebaseUtil.allClassroomCollectionReference().whereEqualTo("nomClasse", assignment.classe)

        query.get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val classeDocument = querySnapshot.documents[0]

                    Log.d("AssignmentActivity", "Document ID: ${classeDocument}")

                    // Accédez à la sous-collection "assignments" et ajoutez l'évaluation
                    classeDocument.reference.collection("assignments")
                        .add(assignment)
                        .addOnSuccessListener {
                            AndroidUtils.showToast(this, "Évaluation créée avec succès!")
                            Log.d("AssignmentActivity", "Évaluation créée avec succès!")
                            finish()  // Retourner à l'activité précédente
                        }
                        .addOnFailureListener { e ->
                            AndroidUtils.showToast(this, "Erreur: ${e.message}")
                            Log.d("AssignmentActivity", "Erreur lors de la création de l'évaluation: ", e)
                        }
                } else {
                    AndroidUtils.showToast(this, "Classe non trouvée.")
                    Log.e("AssignmentActivity", "Classe non trouvée pour le nom: ${assignment.classe}")
                }
            }
            .addOnFailureListener { e ->
                AndroidUtils.showToast(this, "Erreur: ${e.message}")
                Log.e("AssignmentActivity", "Erreur lors de la recherche de la classe: ", e)
            }
    }


    private fun showDatePickerDialog() {
        // Obtenez la date actuelle
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Créez une instance de DatePickerDialog
        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                // Mettre à jour l'EditText avec la date sélectionnée
                editTextDate.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")

                calendar.set(selectedYear, selectedMonth, selectedDay)
                dateSelected = calendar.time
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

}