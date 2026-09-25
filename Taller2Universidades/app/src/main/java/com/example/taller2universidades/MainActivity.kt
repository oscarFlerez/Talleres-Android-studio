package com.example.taller2universidades

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    private lateinit var database: UniversidadDatabase
    private lateinit var idField: EditText
    private lateinit var nameField: EditText
    private lateinit var websiteField: EditText
    private lateinit var listOutput: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = UniversidadDatabase(this)
        showUniversities()
    }

    private fun showUniversities() {
        val content = verticalLayout()
        content.setPadding(24, 24, 24, 24)

        val title = textView("Administrar universidades", 25f)
        title.gravity = Gravity.CENTER
        content.addView(title)

        idField = field("ID", InputType.TYPE_CLASS_NUMBER)
        idField.isEnabled = false
        content.addView(idField, margins(0, 24, 0, 10))

        nameField = field("Nombre de la universidad", InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)
        content.addView(nameField, margins(0, 0, 0, 10))

        websiteField = field("Sitio web (www)", InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI)
        content.addView(websiteField, margins(0, 0, 0, 18))

        val saveButton = button("Guardar / actualizar")
        saveButton.setOnClickListener { saveUniversity() }
        content.addView(saveButton)

        val searchButton = button("Buscar por ID")
        searchButton.setOnClickListener { searchUniversity() }
        content.addView(searchButton, margins(0, 8, 0, 0))

        val deleteButton = button("Eliminar")
        deleteButton.setOnClickListener { deleteUniversity() }
        content.addView(deleteButton, margins(0, 8, 0, 0))

        val clearButton = button("Limpiar")
        clearButton.setOnClickListener { clearFields() }
        content.addView(clearButton, margins(0, 8, 0, 0))

        val listButton = button("Listar universidades")
        listButton.setOnClickListener { refreshList() }
        content.addView(listButton, margins(0, 8, 0, 0))

        listOutput = textView("", 16f)
        content.addView(listOutput, margins(0, 24, 0, 0))

        setContentView(scroll(content))
        clearFields()
        refreshList()
    }

    private fun saveUniversity() {
        val name = nameField.text.toString().trim()
        val website = websiteField.text.toString().trim()
        if (name.isEmpty() || website.isEmpty()) {
            notifyUser("Escribe el nombre y el sitio web")
            return
        }

        val id = idField.text.toString().toLongOrNull() ?: database.nextId()
        database.save(University(id, name, website))
        idField.setText(id.toString())
        notifyUser("Universidad guardada")
        refreshList()
    }

    private fun searchUniversity() {
        val id = idField.text.toString().toLongOrNull()
        if (id == null) {
            notifyUser("Escribe un ID valido")
            return
        }

        val university = database.find(id)
        if (university == null) {
            notifyUser("No existe una universidad con ese ID")
            return
        }

        nameField.setText(university.name)
        websiteField.setText(university.website)
        notifyUser("Universidad encontrada")
    }

    private fun deleteUniversity() {
        val id = idField.text.toString().toLongOrNull()
        if (id == null || !database.delete(id)) {
            notifyUser("No se encontro la universidad")
            return
        }

        clearFields()
        refreshList()
        notifyUser("Universidad eliminada")
    }

    private fun clearFields() {
        idField.setText(database.nextId().toString())
        nameField.setText("")
        websiteField.setText("")
    }

    private fun refreshList() {
        if (!::listOutput.isInitialized) return
        val universities = database.all()
        listOutput.text = if (universities.isEmpty()) {
            "No hay universidades guardadas."
        } else {
            universities.joinToString("\n\n") { "${it.id}. ${it.name}\n${it.website}" }
        }
    }

    private fun notifyUser(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun verticalLayout() = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
    }

    private fun scroll(content: View) = ScrollView(this).apply { addView(content) }

    private fun textView(value: String, size: Float) = TextView(this).apply {
        text = value
        textSize = size
        setTextColor(Color.rgb(30, 30, 30))
    }

    private fun field(hint: String, type: Int) = EditText(this).apply {
        this.hint = hint
        inputType = type
        minHeight = 52
    }

    private fun button(label: String) = Button(this).apply {
        text = label
        minHeight = 52
    }

    private fun margins(left: Int, top: Int, right: Int, bottom: Int) =
        LinearLayout.LayoutParams(-1, -2).apply {
            setMargins(left, top, right, bottom)
        }
}