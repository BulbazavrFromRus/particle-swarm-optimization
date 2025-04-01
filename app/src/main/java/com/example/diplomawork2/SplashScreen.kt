package com.example.diplomawork2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


        // Настройка видео-фона
        val videoView = findViewById<VideoView>(R.id.backgroundVideo)
        val videoPath = "android.resource://${packageName}/${R.raw.background_video}" // Замените на имя вашего видео
        videoView.apply {
            setVideoURI(Uri.parse(videoPath))
            setOnPreparedListener { mp ->
                mp.isLooping = true // Повтор видео
                mp.start()
            }
        }

        findViewById<Button>(R.id.startButton).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}