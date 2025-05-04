package com.example.diplomawork2

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val particleView = findViewById<ParticleView>(R.id.particleView)
        val nextLevelButton = findViewById<Button>(R.id.next_level_button)
        val restartButton = findViewById<Button>(R.id.restart_button)
        val defaultBackground = R.drawable.ic_launcher_background

        nextLevelButton.visibility = View.GONE  // Hide at the start
        restartButton.visibility = View.GONE // Hide at the start

        val backgroundImageName = intent.getStringExtra("screenImage")
        if (backgroundImageName != null) {
            val resId = resources.getIdentifier(backgroundImageName, "drawable", packageName)
            particleView.setBackgroundResource(resId)
            particleView.setCustomBackground(resId)
        }
        else{
            particleView.setCustomBackground(defaultBackground)
        }

        nextLevelButton.setOnClickListener {
            particleView.nextLevel()
        }

        restartButton.setOnClickListener {
            particleView.restartGame()
        }
    }
}
