package com.example.moodtrackerapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CalenderActivity : AppCompatActivity() {

    lateinit var tvCalendarList: TextView
    lateinit var btnBackHome: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calender)

        tvCalendarList = findViewById(R.id.tvCalendarList)
        btnBackHome = findViewById(R.id.btnBackHome)

        val userPreferences = getSharedPreferences("UserData", MODE_PRIVATE)
        val currentUserEmail = userPreferences.getString("currentUserEmail", "guest")

        val moodKey = "moods_$currentUserEmail"

        val moodPreferences = getSharedPreferences("MoodData", MODE_PRIVATE)
        val savedData = moodPreferences.getString(moodKey, "")

        if (savedData!!.isEmpty()) {
            tvCalendarList.text = "No calendar entries yet."
        } else {
            val moodList = savedData.split("###")
            val calendarText = StringBuilder()

            for (moodEntry in moodList) {
                val lines = moodEntry.lines()

                if (lines.size >= 3) {
                    val mood = lines[0]
                    val date = lines[2].split(" - ")[0]

                    calendarText.append("$date: $mood\n\n")
                }
            }

            tvCalendarList.text = calendarText.toString()
        }

        btnBackHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}