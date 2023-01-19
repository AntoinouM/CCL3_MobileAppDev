package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.room.Room

class ActivityAddCard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)

        val db = Room.databaseBuilder(
            this,
            AppDatabase::class.java, "smartBinderDB"
        ).allowMainThreadQueries().build()

        val allCategories = db.categoryDao().getAll()
        val categoryNames = ArrayList<String>()
        //categoryNames.add("")
        for (category in allCategories) {
            categoryNames.add(category.name)
        }
        val topicNames = ArrayList<String>()

        val actvCategory: AutoCompleteTextView = findViewById(R.id.actvCategory)
        val actvTopic: AutoCompleteTextView = findViewById(R.id.actvTopic)
        val categoryOptions = categoryNames.toTypedArray()
        val categoryAdapter =
            ArrayAdapter(this, android.R.layout.select_dialog_item, categoryOptions)
        actvCategory.threshold = 1
        categoryAdapter.setDropDownViewResource(android.R.layout.select_dialog_item)
        actvCategory.setAdapter(categoryAdapter)
        actvCategory.setTextColor(resources.getColor(R.color.black))

        lateinit var activeCategory: Category
        lateinit var activeTopic: Topic
        var categoryExists = false
        var topicExists = false
        lateinit var relatedTopics: List<Topic>

        actvCategory.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (categoryNames.contains(s.toString())) {
                    categoryExists = true
                    for (category in allCategories) {
                        if (s.toString() == category.name) {
                            activeCategory = category
                        }
                    }
                    relatedTopics = db.topicDao().getTopicsOfCategory(activeCategory.id)

                    for (topic in relatedTopics) {
                        topicNames.add(topic.name)
                    }
                    val topicOptions = topicNames.toTypedArray()
                    val topicAdapter = ArrayAdapter(
                        this@ActivityAddCard,
                        android.R.layout.select_dialog_item,
                        topicOptions
                    )
                    actvTopic.threshold = 1
                    topicAdapter.setDropDownViewResource(android.R.layout.select_dialog_item)
                    actvTopic.setAdapter(topicAdapter)
                    actvTopic.setTextColor(resources.getColor(R.color.black))
                } else {
                    categoryExists = false
                    topicNames.clear()
                    actvTopic.setAdapter(null)
                }
            }
        })

        actvTopic.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (topicNames.contains(s.toString())) {
                    topicExists = true
                    for (topic in relatedTopics) {
                        if (s.toString() == topic.name) {
                            activeTopic = topic
                        }
                    }
                }
            }
        })

        val etTitle = findViewById<EditText>(R.id.etTitle)
        val etContent = findViewById<EditText>(R.id.etContent)

        findViewById<Button>(R.id.btnAddCard).setOnClickListener {
            if (topicExists && categoryExists) {
                db.cardDao().insert(
                    Card(0, etTitle.text.toString(), etContent.text.toString(), activeTopic.id)
                )
                Toast.makeText(this, "Card successfully added!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                Toast.makeText(this, "Category or Topic doesn't exist.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}