package com.example.diplomawork2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var background1Image: ImageButton
    private lateinit var background2Image: ImageButton
    private lateinit var background3Image: ImageButton
    private lateinit var selectedTextView: TextView
    private lateinit var playButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        background1Image = findViewById(R.id.background1_image)
        background2Image = findViewById(R.id.background2_image)
        background3Image = findViewById(R.id.background3_image)
        selectedTextView = findViewById(R.id.selected_text)
        playButton = findViewById(R.id.play_button)

        background1Image.setOnClickListener {
            selectBackground(R.drawable.background1)
        }

        background2Image.setOnClickListener {
            selectBackground(R.drawable.background2)
        }

        background3Image.setOnClickListener {
            selectBackground(R.drawable.background3)
        }


        playButton.setOnClickListener {
            startGame()
        }
    }

    private fun selectBackground(backgroundResource: Int) {
        val prefs = getSharedPreferences("game_settings", MODE_PRIVATE)
        prefs.edit().putInt("background_resource", backgroundResource).apply()

        selectedTextView.text = "Фон выбран"
        selectedTextView.visibility = View.VISIBLE
        playButton.visibility = View.VISIBLE
    }

    private fun startGame() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
