package com.example.moodtrackerapp

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Button
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    lateinit var homeRootLayout: LinearLayout
    lateinit var tvWelcome: TextView

    lateinit var tvUserSummary: TextView
    lateinit var tvMoodQuote: TextView

    lateinit var tvTodayStatus: TextView

    lateinit var tvMoodStats: TextView
    lateinit var btnAddMood: Button
    lateinit var btnViewHistory: Button
    lateinit var btnProfile: Button
    lateinit var btnCalendar: Button

    lateinit var btnDarkMode: Button
    lateinit var btnLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        homeRootLayout = findViewById(R.id.homeRootLayout)
        tvWelcome = findViewById(R.id.tvWelcome)
        tvUserSummary = findViewById(R.id.tvUserSummary)
        tvMoodQuote = findViewById(R.id.tvMoodQuote)
        tvTodayStatus = findViewById(R.id.tvTodayStatus)
        tvMoodStats = findViewById(R.id.tvMoodStats)

        btnProfile = findViewById(R.id.btnProfile)
        btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        btnCalendar = findViewById(R.id.btnCalendar)
        btnCalendar.setOnClickListener {
            val intent = Intent(this, CalenderActivity::class.java)
            startActivity(intent)
        }

        // Toggle DARK MODE
        btnDarkMode = findViewById(R.id.btnDarkMode)

        val settingsPreferences = getSharedPreferences("Settings", MODE_PRIVATE)
        val isDarkMode = settingsPreferences.getBoolean("darkMode", false)

        if (isDarkMode) {
            homeRootLayout.setBackgroundColor(android.graphics.Color.parseColor("#111827"))
            window.decorView.setBackgroundColor(android.graphics.Color.parseColor("#111827"))

            btnDarkMode.text = "Switch to Light Mode"
            btnDarkMode.setBackgroundResource(R.drawable.button_dark_mode_border)
            btnDarkMode.setTextColor(android.graphics.Color.WHITE)

            tvWelcome.setTextColor(android.graphics.Color.WHITE)

            tvUserSummary.setTextColor(android.graphics.Color.WHITE)
            tvTodayStatus.setTextColor(android.graphics.Color.WHITE)
            tvMoodStats.setTextColor(android.graphics.Color.WHITE)
            tvMoodQuote.setTextColor(android.graphics.Color.WHITE)

            tvUserSummary.setBackgroundResource(R.drawable.dashboard_card_dark)
            tvTodayStatus.setBackgroundResource(R.drawable.dashboard_card_dark)
            tvMoodStats.setBackgroundResource(R.drawable.dashboard_card_dark)
            tvMoodQuote.setBackgroundResource(R.drawable.dashboard_card_dark)

        } else {
            homeRootLayout.setBackgroundColor(android.graphics.Color.parseColor("#F5F7FB"))
            window.decorView.setBackgroundColor(android.graphics.Color.parseColor("#F5F7FB"))

            btnDarkMode.text = "Switch to Dark Mode"
            btnDarkMode.setBackgroundResource(R.drawable.button_light_mode_border)
            btnDarkMode.setTextColor(android.graphics.Color.parseColor("#111827"))

            tvWelcome.setTextColor(android.graphics.Color.parseColor("#2C3E50"))

            tvUserSummary.setTextColor(android.graphics.Color.parseColor("#374151"))
            tvTodayStatus.setTextColor(android.graphics.Color.parseColor("#374151"))
            tvMoodStats.setTextColor(android.graphics.Color.parseColor("#374151"))
            tvMoodQuote.setTextColor(android.graphics.Color.parseColor("#374151"))

            tvUserSummary.setBackgroundResource(R.drawable.dashboard_card)
            tvTodayStatus.setBackgroundResource(R.drawable.dashboard_card)
            tvMoodStats.setBackgroundResource(R.drawable.dashboard_card)
            tvMoodQuote.setBackgroundResource(R.drawable.dashboard_card)
        }
        btnDarkMode.setOnClickListener {
            val currentMode = settingsPreferences.getBoolean("darkMode", false)
            settingsPreferences.edit().putBoolean("darkMode", !currentMode).apply()
            recreate()
        } // End Toggle DARK MODE

        val sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "User")

        tvWelcome.text = "Welcome Back, $username 👋"

        val currentUserEmail = sharedPreferences.getString("currentUserEmail", "guest")
        val moodKey = "moods_$currentUserEmail"

        val moodPreferences = getSharedPreferences("MoodData", MODE_PRIVATE)
        val savedData = moodPreferences.getString(moodKey, "")

        if (savedData!!.isEmpty()) {
            tvUserSummary.text = "Total Entries: 0\nLast Mood: No mood recorded yet"
        } else {
            val moodList = savedData.split("###")
            val totalEntries = moodList.size
            val lastMood = moodList[0].lines()[0]

            tvUserSummary.text = "Total Entries: $totalEntries\nLast Mood: $lastMood"
        }

        val today = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())

        if (savedData.contains(today)) {
            tvTodayStatus.text = "Today's Status\nYou already checked in today ✅"
        } else {
            tvTodayStatus.text = "Today's Status\nYou have not added a mood today yet."
        }

        if (savedData!!.isEmpty()) {
            tvMoodStats.text = "Mood Stats\nTotal Entries: 0\nMost Common Mood: No data yet\nCurrent Streak: 0 days"
        } else {
            val moodList = savedData.split("###")
            val totalEntries = moodList.size

            val moodNames = moodList.map {
                it.lines()[0]
            }

            val mostCommonMood = moodNames
                .groupingBy { it }
                .eachCount()
                .maxByOrNull { it.value }
                ?.key ?: "No data yet"

            val uniqueDates = moodList.mapNotNull {
                val lines = it.lines()
                if (lines.size > 2) {
                    lines[2].split(" - ")[0]
                } else {
                    null
                }
            }.distinct()

            val streak = uniqueDates.size

            tvMoodStats.text =
                "Mood Stats\nTotal Entries: $totalEntries\nMost Common Mood: $mostCommonMood\nCurrent Streak: $streak days 🔥"
        }

        val quotes = arrayOf(
            "💡 Reminder\nSmall steps every day can help you understand yourself better.",
            "🌱 Growth\nSmall progress is still progress.",
            "☀️ Positive Thought\nToday is a new chance to reset.",
            "🧘 Mindful Moment\nPause, breathe, and be kind to yourself.",
            "⭐ Motivation\nYou are doing better than you think.",
            "🌈 Reflection\nEvery mood teaches you something about yourself.",
            "💜 Self Care\nYour feelings matter. Be patient with yourself.",
            "🌻 Hope\nEven difficult days can lead to better moments.",
            "🌊 Calm\nTake a deep breath. You do not have to rush.",
            "🔥 Strength\nYou made it this far. Keep going.",
            "🌙 Rest\nIt is okay to slow down and recharge.",
            "📝 Reflection\nWriting your mood can help you understand patterns.",
            "🤍 Kindness\nTalk to yourself like you would talk to a friend.",
            "🚶 Progress\nOne small action today can make tomorrow easier.",
            "🌤️ Balance\nNot every day has to be perfect to be meaningful.",
            "💪 Confidence\nYou are capable of handling today.",
            "🧠 Awareness\nNoticing your mood is the first step toward growth.",
            "✨ Reset\nA new moment can always become a fresh start.",
            "🌺 Peace\nGive yourself permission to feel and heal.",
            "🎯 Focus\nFocus on what you can control today."
        )

        tvMoodQuote.text = quotes.random()

        btnAddMood = findViewById(R.id.btnAddMood)
        btnViewHistory = findViewById(R.id.btnViewHistory)
        btnLogout = findViewById(R.id.btnLogout)

        btnAddMood.setOnClickListener {
            val intent = Intent(this, AddMoodActivity::class.java)
            startActivity(intent)
        }

        btnViewHistory.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        btnLogout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout") { _, _ ->
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}