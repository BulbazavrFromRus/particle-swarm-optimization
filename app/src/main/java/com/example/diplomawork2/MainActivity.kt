package com.example.diplomawork2

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val particleView = findViewById<ParticleView>(R.id.particleView)
        val nextLevelButton = findViewById<Button>(R.id.next_level_button)
        val restartButton = findViewById<Button>(R.id.restart_button)

        nextLevelButton.visibility = View.GONE  // Hide at the start
        restartButton.visibility = View.GONE // Hide at the start

        nextLevelButton.setOnClickListener {
            particleView.nextLevel()
        }

        restartButton.setOnClickListener {
            particleView.restartGame()
        }
    }
}
