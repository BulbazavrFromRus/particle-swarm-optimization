package com.example.diplomawork2

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SettingsActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        //This is a RecycleView object where there are being showed screens for game
        val itemsList: RecyclerView = findViewById(R.id.itemsList)

        //Create list of items
        var screens = arrayListOf<Screen>()


        //We create Screen objects in list
        screens.add(Screen(1, "background1", "Black theme", "Plunge into the world of darkness", "Some text about this theme for screen"))
        screens.add(Screen(2, "background2", "White theme", "Plunge into the world of darkness", "Some text about this theme for screen"))
        screens.add(Screen(3, "background3", "Pink theme", "Plunge into the world of darkness", "Some text about this theme for screen"))
        screens.add(Screen(4, "gray", "Gray theme", "Plunge into the world of darkness", "Some text about this theme for screen"))


        itemsList.layoutManager = LinearLayoutManager(this)
        itemsList.adapter = ScreenAdapter(screens, this)


    }
}
