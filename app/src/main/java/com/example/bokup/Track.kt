package com.example.bokup

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    lateinit var dataArrayList: ArrayList<TrackDataClass>
    lateinit var inputRef : DatabaseReference
    lateinit var outputRef : DatabaseReference
    lateinit var bokRef : DatabaseReference
    lateinit var timeBtn : Button
    lateinit var totalCal : TextView
    lateinit var dailyRef: DatabaseReference
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private var currentDate: String = dateFormat.format(Date())


    @SuppressLint("SuspiciousIndentation", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track)

        databaseReference = FirebaseDatabase.getInstance().getReference("BOKDatabase")

        //Buttons and Text
        var backBtn : Button = findViewById(R.id.backBtn)
        var addBtn : Button = findViewById(R.id.addBtn)
        var clearBtn : Button = findViewById(R.id.clearBtn)
        var endBtn : Button = findViewById(R.id.endBtn)
//      var dateBtn : Button = findViewById(R.id.dateBtn)
        var calText : EditText = findViewById(R.id.calText)
        var caloriesString = 0
        var time = 0
        var currentDate = dateFormat.format(Date())

        dailyRef = databaseReference.child("Days").child(currentDate)
        bokRef = databaseReference
        inputRef = dailyRef.child("Input")
        outputRef = dailyRef.child("Output")
        totalCal = findViewById(R.id.totalCal)
        timeBtn = findViewById(R.id.timeBtn)

        recyclerView = findViewById(R.id.rvTrack)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        dataArrayList = arrayListOf<TrackDataClass>()
        getData()
        sumCal()

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

        addBtn.setOnClickListener {
            // Update currentDate to the current system date
            currentDate = dateFormat.format(Date())

            val selectedTime = timeBtn.text.toString().trim()  // Get the selected time
            val caloriesString = calText.text.toString().trim()

            if (selectedTime.isEmpty() || caloriesString.isEmpty()) {
                Toast.makeText(this, "Please enter time and calories", Toast.LENGTH_SHORT).show()
            } else {
                val calories = caloriesString.toIntOrNull() ?: 0
                val calDay = TrackDataClass(selectedTime, caloriesString)
                calDay.dateCal = currentDate  // Set the currentDate

                // Use inputRef which points to the correct day's "Input" node
                inputRef.child(calDay.toString()).setValue(calDay).addOnSuccessListener {
                    Toast.makeText(this, "Success - ADD", Toast.LENGTH_SHORT).show()
                    Log.i("Track", dataArrayList.size.toString())
                    dataArrayList.add(calDay) // Add new data to the local list
                    recyclerView.adapter?.notifyDataSetChanged() // Update RecyclerView
                }
                val intent = Intent(this, Track::class.java)
                startActivity(intent)
                sumCal()
            }
        }


        backBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        clearBtn.setOnClickListener {
            inputRef.removeValue().addOnSuccessListener {
                Toast.makeText(this, "Success - DELETE", Toast.LENGTH_SHORT).show()
            }
            val intent = Intent(this, Track::class.java)
            startActivity(intent)
        }

        endBtn.setOnClickListener {
            dataArrayList.clear()

            // Notify the adapter of the change
            recyclerView.adapter?.notifyDataSetChanged()
            totalCal.visibility = View.GONE
        }

        sharedPreferences = getSharedPreferences("BOKStorage", MODE_PRIVATE)
        var sharedEditor = sharedPreferences.edit()
        sharedEditor.putString("SharedID", "SampleValue")
        sharedEditor.apply()

        sharedPreferences.getString("SharedID", "")

        inputRef.get().addOnCompleteListener({ task ->

            var task = task.result;
            dataArrayList.clear()
            for (e in task.children) {
                var calNum = e.child("cal").getValue(String::class.java)
                var calTime = e.child("timeCal").getValue(String::class.java)
                var calDate = e.child("dateCal").getValue(String::class.java)
                var calPrint = TrackDataClass(calTime.toString(), calNum.toString(), calDate.toString())
                dataArrayList.add(calPrint)

            }

        })

    }

    private fun updateDailyReference() {
        dailyRef = databaseReference.child("Days").child(currentDate)
        inputRef = dailyRef.child("Input")
        outputRef = dailyRef.child("Output")
        // You might want to also clear and reload data for the new date
    }

    private fun getData() {

        databaseReference = FirebaseDatabase.getInstance().getReference("BOKDatabase")

        databaseReference.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){

                    for (dataSnapshot in snapshot.children){


                        val data = dataSnapshot.getValue(TrackDataClass::class.java)
                        dataArrayList.add(data!!)


                    }
                    recyclerView.adapter = TrackAdapter(dataArrayList)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun sumCal(): String {
        // Use the selectedDate to reference the correct day's data in Firebase
        val inputRefForSelectedDate = databaseReference.child("Days").child(currentDate).child("Input")

        inputRefForSelectedDate.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var sum = 0
                for (i in snapshot.children) {
                    val calValueString = i.child("cal").value?.toString()
                    val calValue = calValueString?.toIntOrNull() ?: 0 // Safely convert to Int
                    sum += calValue
                }
                val outputRefForSelectedDate = databaseReference.child("Days").child(currentDate).child("Output")
                outputRefForSelectedDate.child("Total").setValue(sum)
                totalCal.text = sum.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle potential errors here
            }
        })

        return totalCal.toString()
    }


}