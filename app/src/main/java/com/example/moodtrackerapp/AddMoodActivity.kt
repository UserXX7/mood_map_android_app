package com.example.moodtrackerapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.text.Editable
import android.text.TextWatcher

class AddMoodActivity : AppCompatActivity() {

    lateinit var tvHappy: TextView
    lateinit var tvSad: TextView
    lateinit var tvAngry: TextView
    lateinit var tvTired: TextView
    lateinit var tvStressed: TextView
    lateinit var tvExcited: TextView
    lateinit var tvMoodMessage: TextView

    lateinit var seekIntensity: SeekBar
    lateinit var tvIntensityValue: TextView
    lateinit var etMoodNote: EditText
    lateinit var btnSaveMood: Button

    lateinit var btnBackHome: Button
    lateinit var tvNoteCounter: TextView

    var intensity = 5
    var selectedMood = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_mood)

        tvHappy = findViewById(R.id.tvHappy)
        tvSad = findViewById(R.id.tvSad)
        tvAngry = findViewById(R.id.tvAngry)
        tvTired = findViewById(R.id.tvTired)
        tvStressed = findViewById(R.id.tvStressed)
        tvExcited = findViewById(R.id.tvExcited)
        tvMoodMessage = findViewById(R.id.tvMoodMessage)

        seekIntensity = findViewById(R.id.seekIntensity)
        tvIntensityValue = findViewById(R.id.tvIntensityValue)
        etMoodNote = findViewById(R.id.etMoodNote)
        tvNoteCounter = findViewById(R.id.tvNoteCounter)

        etMoodNote.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                tvNoteCounter.text = "${s?.length ?: 0}/200"
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        btnSaveMood = findViewById(R.id.btnSaveMood)

        btnBackHome = findViewById(R.id.btnBackHome)

        btnBackHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        tvHappy.setOnClickListener {
            selectMood("😊 Happy", tvHappy)
        }

        tvSad.setOnClickListener {
            selectMood("😢 Sad", tvSad)
        }

        tvAngry.setOnClickListener {
            selectMood("😡 Angry", tvAngry)
        }

        tvTired.setOnClickListener {
            selectMood("😴 Tired", tvTired)
        }

        tvStressed.setOnClickListener {
            selectMood("😟 Stressed", tvStressed)
        }

        tvExcited.setOnClickListener {
            selectMood("😄 Excited", tvExcited)
        }

        seekIntensity.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                intensity = progress
                tvIntensityValue.text = "Intensity: $intensity/10"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        btnSaveMood.setOnClickListener {
            if (selectedMood.isEmpty()) {
                Toast.makeText(this, "Please select a mood", Toast.LENGTH_SHORT).show()
            } else {
                val note = etMoodNote.text.toString()
                val date = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault()).format(Date())

                val moodEntry = "$selectedMood\nIntensity: $intensity/10\n$date\n$note"

                val userPreferences = getSharedPreferences("UserData", MODE_PRIVATE)
                val currentUserEmail = userPreferences.getString("currentUserEmail", "guest")

                val moodKey = "moods_$currentUserEmail"

                val sharedPreferences = getSharedPreferences("MoodData", MODE_PRIVATE)
                val oldData = sharedPreferences.getString(moodKey, "")

                val newData = if (oldData!!.isEmpty()) {
                    moodEntry
                } else {
                    moodEntry + "###" + oldData
                }

                sharedPreferences.edit().putString(moodKey, newData).apply()

                Toast.makeText(this, "Mood Saved", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, HistoryActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun selectMood(mood: String, selectedView: TextView) {
        selectedMood = mood

        tvHappy.setBackgroundResource(R.drawable.mood_card)
        tvSad.setBackgroundResource(R.drawable.mood_card)
        tvAngry.setBackgroundResource(R.drawable.mood_card)
        tvTired.setBackgroundResource(R.drawable.mood_card)
        tvStressed.setBackgroundResource(R.drawable.mood_card)
        tvExcited.setBackgroundResource(R.drawable.mood_card)

        selectedView.setBackgroundResource(R.drawable.mood_card_selected)

        val message = when (mood) {
            "😊 Happy" -> "😊 Great! Keep enjoying your positive energy today."
            "😢 Sad" -> "😢 It’s okay to feel sad. Be kind to yourself and take things slowly."
            "😡 Angry" -> "😡 Take a deep breath. Step away for a moment if you need to reset."
            "😴 Tired" -> "😴 Your body may need rest. Try to recharge when you can."
            "😟 Stressed" -> "😟 Pause, breathe, and focus on one thing at a time."
            "😄 Excited" -> "😄 That’s awesome! Use that energy for something meaningful."
            else -> "Select a mood to see a helpful message."
        }

        tvMoodMessage.text = message
    }
}