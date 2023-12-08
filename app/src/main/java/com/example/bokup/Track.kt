package com.example.bokup

import android.app.TimePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

lateinit var databaseReference : DatabaseReference
lateinit var sharedPreferences : SharedPreferences
lateinit var recyclerView: RecyclerView
lateinit var dataList: ArrayList<DataClass>

class Track : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track)

        //Buttons and Text
        var backBtn : Button = findViewById(R.id.backBtn)
        var addBtn : Button = findViewById(R.id.addBtn)
        var delBtn: Button = findViewById(R.id.delBtn)
        var targetBtn : Button = findViewById(R.id.targetBtn)
        var timeBtn : Button = findViewById(R.id.timeBtn)
        var calText : EditText = findViewById(R.id.calText)

        timeBtn.setOnClickListener {
            // Open Time Picker Dialog
            val currentTime = Calendar.getInstance()
            val hour = currentTime.get(Calendar.HOUR_OF_DAY)
            val minute = currentTime.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                timeBtn.setText(String.format("%02d:%02d", selectedHour, selectedMinute))
            }, hour, minute, false) // false for 12 hour format

            timePickerDialog.show()
        }

        backBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        targetBtn.setOnClickListener {
            val intent = Intent(this, Target::class.java)
            startActivity(intent)
        }

        addBtn.setOnClickListener {
            val time = timeBtn.text.toString().trim()
            val calories = calText.text.toString().trim()

            // Check if time or calories is empty
            if (time.isEmpty() || calories.isEmpty()) {
                Toast.makeText(this, "Please enter both calories and time", Toast.LENGTH_SHORT).show()
            } else {
                // Proceed with adding to database
                databaseReference = FirebaseDatabase.getInstance().getReference("BOKDatabase")
                var calDay = DataClass(time, calories)
                var dataKey = databaseReference.push().getKey()
                databaseReference.child("Day").child(dataKey.toString()).setValue(calDay)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Success - ADD", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        delBtn.setOnClickListener {
            databaseReference.child("Day").child("2021159586").removeValue().addOnSuccessListener {
                Toast.makeText(this, "Success - DELETE", Toast.LENGTH_SHORT).show()
            }
        }

        sharedPreferences = getSharedPreferences("BOKStorage", MODE_PRIVATE)
        var sharedEditor = sharedPreferences.edit()
        sharedEditor.putString("SharedID", "SampleValue")
        sharedEditor.apply()

        sharedPreferences.getString("SharedID", "")
    }

    // Other functions and classes...
}
