package com.example.diplomawork2

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

class SplashScreen : AppCompatActivity() {

    private lateinit var username: String
    private lateinit var  animationDrawable: AnimationDrawable;


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


        username = intent.getStringExtra("username") ?: ""

        // Получаем корневой элемент вашего макета (или тот View, к которому применен фон)
        val splashScreenLayout = findViewById<ConstraintLayout>(R.id.splash_screen_layout)

        // Получаем AnimationDrawable из вашего ресурса
        animationDrawable = splashScreenLayout.background as AnimationDrawable

        // Устанавливаем параметры для плавного перехода (опционально, но рекомендуется)
        animationDrawable.setEnterFadeDuration(2000) // Длительность появления нового кадра
        animationDrawable.setExitFadeDuration(4000)  // Длительность исчезновения старого кадра




        findViewById<Button>(R.id.startButton).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        findViewById<Button>(R.id.settings_button).setOnClickListener{
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.account_button).setOnClickListener {
            val intent = Intent(this, UserProfileActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
        }
    }

   override fun onResume() {
        super.onResume()
        // Запускаем анимацию здесь, чтобы она возобновлялась при возвращении на экран
        if (::animationDrawable.isInitialized) { // Проверяем, инициализирована ли переменная
            animationDrawable.start()
        }
    }

  override  fun onPause() {
        super.onPause()
        // Останавливаем анимацию, когда Activity не видно, для экономии ресурсов
        if (::animationDrawable.isInitialized) {
            animationDrawable.stop()
        }
    }
}