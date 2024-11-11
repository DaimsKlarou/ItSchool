package com.example.itschool

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ViewSwitcher
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.itschool.adapter.ChatRecyclerAdapter
import com.example.itschool.adapter.CreateGroupRecyclerAdapter
import com.example.itschool.model.ChatMessageModel
import com.example.itschool.model.GrouproomModel
import com.example.itschool.model.UserModel
import com.example.itschool.utils.AndroidUtils
import com.example.itschool.utils.FirebaseUtil
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.Query

class NewGroupActivity : AppCompatActivity() {

    private lateinit var createGroupButton: Button
    private lateinit var viewSwitcher: ViewSwitcher
    private lateinit var groupTitle : EditText
    private lateinit var groupDescription : EditText
    private lateinit var nextButton : Button
    private lateinit var classe : AutoCompleteTextView
    private lateinit var adapter : CreateGroupRecyclerAdapter
    private lateinit var recyclerView : RecyclerView
    private lateinit var boxSelectClasse : TextInputLayout

    private lateinit var listeClasse : ArrayList<String>
    private lateinit var listeUser : List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_new_group)

        val app = application as Itschool

        viewSwitcher = findViewById(R.id.view_switcher)
        nextButton = findViewById(R.id.nextStep)
        groupTitle = findViewById(R.id.groupTitle)
        groupDescription = findViewById(R.id.descriptionText)
        createGroupButton = findViewById(R.id.validebtn)
        classe = findViewById(R.id.classeId)
        recyclerView = findViewById(R.id.list_user_recyclerView)
        boxSelectClasse = findViewById(R.id.valeurSelect)

        if (app.currentUser.role == "Professeur") {
            listeClasse = FirebaseUtil.AllClassOfUserProf(app.currentUser.userId!!)
            classe.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, listeClasse))
            boxSelectClasse.visibility = View.VISIBLE
        } else {
            boxSelectClasse.visibility = View.GONE
        }

        nextButton.setOnClickListener {
            viewSwitcher.showNext()
        }

        listeUser = setupGroupRecyclerView(app.currentUser.classe!!)

        createGroupButton.setOnClickListener{
            if(app.currentUser.role == "Professeur") {
                savedGroupProf(app)

            } else {
                savedGroupEtudiant(app)

            }

            finish()
        }

    }

    private fun savedGroupEtudiant(app : Itschool) {

        val groupModel = GrouproomModel()
        groupModel.nomGroup = groupTitle.text.toString()
        groupModel.description = groupDescription.text.toString()
        groupModel.userIds = ArrayList(listeUser.toMutableList().apply { add(app.currentUser.userId!!) })
        groupModel.grouproomId = app.currentUser.userId
        groupModel.createdBy = app.currentUser.userId
        groupModel.profCreated = false

        Log.d("NewGroupActivity", "savedGroup: " + groupModel)
        FirebaseUtil.searchClasseWithName(app.currentUser.classe!!) {
            Log.d("NewGroupActivity", "Les info sur la classe sont : ${it}")
            groupModel.classId = it.classroomId

            FirebaseUtil.getClassroomReference(groupModel.classId!!).collection("groups").add(groupModel).addOnCompleteListener {
                AndroidUtils.showToast(this, "Votre groupe a bien ete cree avec success !!!")
            }

        }
        Log.d("NewGroupActivity", "La classe des etudiants est : ${listeUser} de la classe ${groupModel.classId}")

        finish()

    }

    private fun savedGroupProf(app : Itschool) {
        AndroidUtils.showToast(this, "Votre groupe a bien ete cree avec success !!!")
        val groupModel = GrouproomModel()
        groupModel.nomGroup = groupTitle.text.toString()
        groupModel.lastMessage = groupDescription.text.toString()
        groupModel.classId = classe.text.toString()
        groupModel.userIds = ArrayList(listeUser.toMutableList().apply { add(app.currentUser.userId!!) })
        groupModel.createdBy = app.currentUser.userId
        groupModel.profCreated = true

        Log.d("NewGroupActivity", "savedGroup: " + groupModel.toString())
        FirebaseUtil.searchClasseWithName(classe.text.toString()) {
            Log.d("NewGroupActivity", "Les info sur la classe sont : ${it}")
            groupModel.classId = it.classroomId
        }
        Log.d("NewGroupActivity", "savedGroup: " + groupModel.toString())
        Log.d("NewGroupActivity", "La classe des etudiants est : ${listeUser} de la classe ${classe.text.toString()}")

        FirebaseUtil.getClassroomReference(classe.text.toString())
        finish()
    }

    private fun setupGroupRecyclerView(classe : String) : List<String> {
        val query : Query = FirebaseUtil.allUserInClasse(classe)

        val options = FirestoreRecyclerOptions.Builder<UserModel>()
            .setQuery(query, UserModel::class.java)
            .setLifecycleOwner(this)
            .build()

        adapter = CreateGroupRecyclerAdapter(options, applicationContext)
        val manager = LinearLayoutManager(this).apply { reverseLayout = true }
        recyclerView.layoutManager = manager
        recyclerView.adapter = adapter
        adapter.startListening()
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                recyclerView.smoothScrollToPosition(0)
            }
        })

        return adapter.getSelectedUserIds()
    }


}