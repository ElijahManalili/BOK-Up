package com.example.bokup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Buttons
        var trackBttn : Button = findViewById(R.id.track)
        var progressBttn : Button = findViewById(R.id.progress)

       trackBttn.setOnClickListener {
            val intent = Intent(this, Track::class.java)
            startActivity(intent)
        }

        progressBttn.setOnClickListener {
            val intent = Intent(this, Progress::class.java)
            startActivity(intent)
        }
    }
}