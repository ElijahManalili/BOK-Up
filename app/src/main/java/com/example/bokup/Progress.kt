package com.example.bokup

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bokup.R.id.backBtn
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Progress : AppCompatActivity() {

    lateinit var databaseReference: DatabaseReference
    lateinit var sharedPreferences : SharedPreferences
    lateinit var recyclerView: RecyclerView
    lateinit var dataArrayList: ArrayList<DataClass>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progress)

        databaseReference = FirebaseDatabase.getInstance().getReference("BOKDatabase")


        recyclerView = findViewById(R.id.rvProgress)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        dataArrayList = arrayListOf<DataClass>()
        getData()

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

        databaseReference.child("Day").child("Input").get().addOnCompleteListener({ task ->

            var task = task.result;
            dataArrayList.clear()
            for (e in task.children) {
                var calNum = e.child("cal").getValue(String::class.java)
                var calTime = e.child("timeCal").getValue(String::class.java)
                var calPrint = DataClass(calTime.toString(), calNum.toString())
                dataArrayList.add(calPrint)

            }

        })
    }

    private fun getData() {

        databaseReference = FirebaseDatabase.getInstance().getReference("BOKDatabase")

        databaseReference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){

                    for (dataSnapshot in snapshot.children){


                        val data = dataSnapshot.getValue(DataClass::class.java)
                        dataArrayList.add(data!!)


                    }
                    recyclerView.adapter = AdapterClass(dataArrayList)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}