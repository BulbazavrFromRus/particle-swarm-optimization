package com.example.diplomawork2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.diplomawork2.databinding.ActivityUserProfileBinding

class UserProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileBinding
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseHelper = DatabaseHelper(this)

        // Получаем username из intent
        val username = intent.getStringExtra("username") ?: ""

        // Отображаем username
        binding.tvUsername.text = username

        // Получаем рекорд из базы и отображаем
        val record = databaseHelper.getRecord(username)
        binding.tvRecord.text = "Record: $record"


    }
}
