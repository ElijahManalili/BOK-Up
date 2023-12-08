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
        var trackBtn : Button = findViewById(R.id.track)
        var progressBtn : Button = findViewById(R.id.progress)
        var targetBtn : Button = findViewById(R.id.target)

       targetBtn.setOnClickListener {
           val intent = Intent(this, Target::class.java)
           startActivity(intent)
       }

       trackBtn.setOnClickListener {
            val intent = Intent(this, Track::class.java)
            startActivity(intent)
        }

        progressBtn.setOnClickListener {
            val intent = Intent(this, Progress::class.java)
            startActivity(intent)
        }
    }
}