package com.example.bokup

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bokup.R.id.backBtn
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Date
import java.util.Locale

class Progress : AppCompatActivity() {

    lateinit var databaseReference: DatabaseReference
    lateinit var sharedPreferences : SharedPreferences
    lateinit var recyclerView: RecyclerView
    lateinit var progressArrayList: ArrayList<ProgressDataClass>
    lateinit var inputRef : DatabaseReference
    lateinit var outputRef : DatabaseReference
    lateinit var bokRef : DatabaseReference
    lateinit var tvcalTotal : TextView
    lateinit var dailyRef: DatabaseReference
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private var currentDate: String = dateFormat.format(Date())


    private fun getData() {
        // Fetch data from Firebase and populate progressArrayList
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                progressArrayList.clear()
                for (dataSnapshot in snapshot.children) {
                    val data = dataSnapshot.getValue(ProgressDataClass::class.java)
                    progressArrayList.add(data!!)
                }
                recyclerView.adapter = ProgressAdapter(progressArrayList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle potential errors
            }
        })
    }

    private fun fetchTotalCal() {
        val totalCalRef = databaseReference.child("Days").child(currentDate).child("Output").child("Total")

        totalCalRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val total = snapshot.getValue(Int::class.java)
                tvcalTotal.text = total?.toString() ?: "0"
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle potential errors here
            }
        })
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progress)

        tvcalTotal = findViewById(R.id.tvcalTotal)

        databaseReference = FirebaseDatabase.getInstance().getReference("BOKDatabase")
        dailyRef = databaseReference.child("Days").child(currentDate)
        bokRef = databaseReference
        inputRef = dailyRef.child("Input")
        outputRef = dailyRef.child("Output")

        recyclerView = findViewById(R.id.rvProgress)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        progressArrayList = arrayListOf<ProgressDataClass>()
        getData()
        fetchTotalCal()


        //Buttons
        var backBtn : Button = findViewById(backBtn)

        backBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        sharedPreferences = getSharedPreferences("BOKStorage", MODE_PRIVATE)
        var sharedEditor = sharedPreferences.edit()
        sharedEditor.putString("SharedID", "SampleValue")
        sharedEditor.apply()

        sharedPreferences.getString("SharedID", "")

        databaseReference.child("Day").child("Input").get().addOnCompleteListener { task ->

            inputRef.get().addOnCompleteListener({ task ->

                var task = task.result;
                progressArrayList.clear()
                for (e in task.children) {
                    var calDate = e.child("dateCal").getValue(String::class.java)
                    var totalCal = e.child("totalCal").getValue(String::class.java)
                    var calPrint =
                        ProgressDataClass(calDate.toString(), totalCal.toString())
                    progressArrayList.add(calPrint)
                }
                recyclerView.adapter?.notifyDataSetChanged()
            })
        }
    }
}