package com.example.bokup

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Sampler.Value
import android.util.Log
import android.widget.Adapter
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bokup.R.id.endBtn
import com.example.bokup.R.id.rvTrack
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList


class Track : AppCompatActivity() {


    lateinit var databaseReference: DatabaseReference
    lateinit var sharedPreferences : SharedPreferences
    lateinit var recyclerView: RecyclerView
    lateinit var dataArrayList: ArrayList<DataClass>

    @SuppressLint("SuspiciousIndentation", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track)

        databaseReference = FirebaseDatabase.getInstance().getReference("BOKDatabase")

        //Buttons and Text
        var backBtn : Button = findViewById(R.id.backBtn)
        var addBtn : Button = findViewById(R.id.addBtn)
        var targetBtn : Button = findViewById(R.id.targetBtn)
        var timeBtn : Button = findViewById(R.id.timeBtn)
        var clearBtn : Button = findViewById(R.id.clearBtn)
        var calText : EditText = findViewById(R.id.calText)
        var totalCal : TextView = findViewById(R.id.totalCal)

        recyclerView = findViewById (R.id.rvTrack)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        dataArrayList = arrayListOf<DataClass>()
        getData()

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

//        endBtn.setOnClickListener {
//            val totalSum = dataArrayList.sumOf { it.cal }
//            totalCal.text = ""
//        }

        addBtn.setOnClickListener {
            val time = timeBtn.text.toString().trim()
            val calories = calText.text.toString().trim()

            // Check if time or calories is empty
            if (time.isEmpty() || calories.isEmpty()) {
                Toast.makeText(this, "Please enter both calories and time", Toast.LENGTH_SHORT).show()
            } else {
                // Proceed with adding to database

                var calDay = DataClass(time, calories)
                var dataKey = databaseReference.push().getKey()
                databaseReference.child("Day").child("Input").child(calDay.toString()).setValue(calDay)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Success - ADD", Toast.LENGTH_SHORT).show()
                        Log.i("wowowow_caro", dataArrayList.size.toString())
                   }

            }
            val intent = Intent(this, Track::class.java)
            startActivity(intent)
        }

        clearBtn.setOnClickListener {
            databaseReference.child("Day").child("Input").removeValue().addOnSuccessListener {
                Toast.makeText(this, "Success - DELETE", Toast.LENGTH_SHORT).show()
            }
            val intent = Intent(this, Track::class.java)
            startActivity(intent)
        }

        sharedPreferences = getSharedPreferences("BOKStorage", MODE_PRIVATE)
        var sharedEditor = sharedPreferences.edit()
        sharedEditor.putString("SharedID", "SampleValue")
        sharedEditor.apply()

        sharedPreferences.getString("SharedID", "")

        databaseReference.child("Day").child("Input").get().addOnCompleteListener({task ->

            var task = task.result;
            dataArrayList.clear()
            for(e in task.children) {
                var calNum = e.child("cal").getValue(String::class.java)
                var calTime = e.child("timeCal").getValue(String::class.java)
                var calPrint = DataClass(calTime.toString(), calNum.toString())
                dataArrayList.add(calPrint)

            }

        })
    }

    private fun getData() {

        databaseReference = FirebaseDatabase.getInstance().getReference("BOKDatabase")

        databaseReference.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){

                    for (dataSnapshot in snapshot.children){


                        val data = dataSnapshot.getValue(DataClass::class.java)
                        dataArrayList.add(data!!)

                    }
                    setupRecyclerView()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun setupRecyclerView() {
        val adapter = AdapterClass(dataArrayList,
            onEdit = { DataClass, position ->
                var calDay = DataClass
                var updateInput = mapOf("timeCal" to DataClass.timeCal, "cal" to DataClass.cal)
                databaseReference.child("Day").child("Input").updateChildren(updateInput).addOnSuccessListener {
                    Toast.makeText(this, "Record updated successfully", Toast.LENGTH_SHORT).show()
                    // Optionally update your local data list and notify the adapter
                }.addOnFailureListener {
                    // Handle failure
                    Toast.makeText(this, "Failed to update record", Toast.LENGTH_SHORT).show()
                }
            },
            onDelete = { dataItem, position ->
                databaseReference.child("Day").child("Input").removeValue().addOnSuccessListener {
                    Toast.makeText(this, "Success - DELETE", Toast.LENGTH_SHORT).show()
                }
                val intent = Intent(this, Track::class.java)
                startActivity(intent)
            }
        )


        recyclerView.adapter = adapter
    }

}