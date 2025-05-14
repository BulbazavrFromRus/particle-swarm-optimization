package com.example.diplomawork2

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView

class MainActivity : AppCompatActivity() {

    private lateinit var explosionAnimationView: LottieAnimationView
    private lateinit var victoryAnimationView: LottieAnimationView
    private lateinit var lossAnimationView: LottieAnimationView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        explosionAnimationView = findViewById(R.id.explosionAnimationView)
        victoryAnimationView = findViewById(R.id.victoryAnimationView)
        lossAnimationView = findViewById(R.id.lossAnimationView)

        val particleView = findViewById<ParticleView>(R.id.particleView)
        val nextLevelButton = findViewById<Button>(R.id.next_level_button)
        val restartButton = findViewById<Button>(R.id.restart_button)
        val defaultBackground = R.drawable.ic_launcher_background

        nextLevelButton.visibility = View.GONE
        restartButton.visibility = View.GONE

        particleView.setExplosionAnimationView(explosionAnimationView)
        particleView.setVictoryAnimationView(victoryAnimationView)
        particleView.setLossAnimationView(lossAnimationView)

        val backgroundImageName = intent.getStringExtra("screenImage")
        if (backgroundImageName != null) {
            val resId = resources.getIdentifier(backgroundImageName, "drawable", packageName)
            particleView.setBackgroundResource(resId)
            particleView.setCustomBackground(resId)
        } else {
            particleView.setCustomBackground(defaultBackground)
        }

        nextLevelButton.setOnClickListener {
            particleView.hideAllAnimations()
            particleView.nextLevel()
        }

        restartButton.setOnClickListener {
            particleView.hideAllAnimations()
            particleView.restartGame()
        }
    }
}
