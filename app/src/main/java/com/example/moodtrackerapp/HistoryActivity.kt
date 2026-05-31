package com.example.moodtrackerapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class HistoryActivity : AppCompatActivity() {

    lateinit var listMoodHistory: ListView
    lateinit var btnBackHome: Button
    lateinit var btnClearHistory: Button

    lateinit var moodKey: String
    lateinit var sharedPreferences: android.content.SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        listMoodHistory = findViewById(R.id.listMoodHistory)
        btnBackHome = findViewById(R.id.btnBackHome)
        btnClearHistory = findViewById(R.id.btnClearHistory)

        val userPreferences = getSharedPreferences("UserData", MODE_PRIVATE)
        val currentUserEmail = userPreferences.getString("currentUserEmail", "guest")

        moodKey = "moods_$currentUserEmail"
        sharedPreferences = getSharedPreferences("MoodData", MODE_PRIVATE)

        loadMoodHistory()

        btnBackHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnClearHistory.setOnClickListener {
            sharedPreferences.edit().remove(moodKey).apply()
            Toast.makeText(this, "Mood history cleared", Toast.LENGTH_SHORT).show()
            loadMoodHistory()
        }
    }

    private fun loadMoodHistory() {
        val savedData = sharedPreferences.getString(moodKey, "")

        val moodList = if (savedData!!.isEmpty()) {
            arrayListOf("No mood entries yet.\n\n\n")
        } else {
            ArrayList(savedData.split("###"))
        }

        val adapter = MoodAdapter(this, moodList)
        listMoodHistory.adapter = adapter
    }

    class MoodAdapter(
        private val context: Context,
        private val moodList: ArrayList<String>
    ) : BaseAdapter() {

        override fun getCount(): Int {
            return moodList.size
        }

        override fun getItem(position: Int): Any {
            return moodList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = View.inflate(context, R.layout.row_mood, null)

            val tvRowMood = view.findViewById<TextView>(R.id.tvRowMood)
            val tvRowIntensity = view.findViewById<TextView>(R.id.tvRowIntensity)
            val tvRowDate = view.findViewById<TextView>(R.id.tvRowDate)
            val tvRowNote = view.findViewById<TextView>(R.id.tvRowNote)
            val btnDeleteMood = view.findViewById<Button>(R.id.btnDeleteMood)

            val entry = moodList[position]
            val lines = entry.lines()

            if (entry.startsWith("No mood entries")) {
                tvRowMood.text = "No mood entries yet."
                tvRowIntensity.text = "Start by adding your first mood."
                tvRowDate.text = ""
                tvRowNote.text = ""
            } else {
                tvRowMood.text = if (lines.size > 0) lines[0] else ""
                tvRowIntensity.text = if (lines.size > 1) lines[1] else ""
                tvRowDate.text = if (lines.size > 2) lines[2] else ""

                if (lines.size > 3 && lines[3].isNotEmpty()) {
                    tvRowNote.text = "Note: ${lines[3]}"
                } else {
                    tvRowNote.text = "Note: No note added"
                }
            }

            btnDeleteMood.setOnClickListener {
                moodList.removeAt(position)

                val newData = moodList.joinToString("###")

                val activity = context as HistoryActivity
                activity.sharedPreferences.edit().putString(activity.moodKey, newData).apply()

                notifyDataSetChanged()

                Toast.makeText(context, "Mood entry deleted", Toast.LENGTH_SHORT).show()
            }

            return view
        }
    }
}