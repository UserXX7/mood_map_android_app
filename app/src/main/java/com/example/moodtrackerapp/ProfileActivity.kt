package com.example.moodtrackerapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    lateinit var tvProfileInfo: TextView
    lateinit var btnBackHome: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        tvProfileInfo = findViewById(R.id.tvProfileInfo)
        btnBackHome = findViewById(R.id.btnBackHome)

        val userPreferences = getSharedPreferences("UserData", MODE_PRIVATE)
        val name = userPreferences.getString("username", "User")
        val email = userPreferences.getString("currentUserEmail", "No email found")

        val moodKey = "moods_$email"
        val moodPreferences = getSharedPreferences("MoodData", MODE_PRIVATE)
        val savedData = moodPreferences.getString(moodKey, "")

        val totalEntries = if (savedData!!.isEmpty()) {
            0
        } else {
            savedData.split("###").size
        }

        tvProfileInfo.text =
            "Name: $name\nEmail: $email\nTotal Mood Entries: $totalEntries"

        btnBackHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}