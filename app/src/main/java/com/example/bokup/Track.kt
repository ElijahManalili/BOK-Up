package com.example.bokup

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import androidx.recyclerview.widget.RecyclerView


lateinit var databaseReference : DatabaseReference
lateinit var titleList :Array<String>
lateinit var sharedPreferences : SharedPreferences
lateinit var recyclerView: RecyclerView
lateinit var dataList: ArrayList<DataClass>
lateinit var timeList :Array<String>
class Track : AppCompatActivity() {
/*public lateinit var calorieList: ArrayList<String>
public lateinit var timeList: ArrayList<String>*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track)



    /*titleList = arrayOf()
    recyclerView = findViewById(R.id.recyclerView)
    recyclerView.layoutManager = LinearLayoutManager(this)
    recyclerView.setHasFixedSize(true)
    dataList = arrayListOf<DataClass>()
    getData()
*/
        var listData = arrayListOf<DataClass>()


        //Buttons and Text
        var backBtn : Button = findViewById(R.id.backBtn)
        var addBtn : Button = findViewById(R.id.addBtn)
        var calText : EditText = findViewById(R.id.calText)
        var calTime : EditText = findViewById(R.id.calTime)

        backBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        addBtn.setOnClickListener {
            databaseReference = FirebaseDatabase.getInstance().getReference("BOKDatabase")
            var calDay = DataClass(calTime.text.toString(), calText.text.toString())
            var dataKey = databaseReference.push().getKey()
            databaseReference.child("Day").child(dataKey.toString()).setValue(calDay)
                .addOnSuccessListener {
                    Toast.makeText(this, "Success - ADD", Toast.LENGTH_SHORT).show()
                }
            /*calorieList.add(calText.text.toString())
            timeList.add(calTime.text.toString())*/
        }

       /* //Get
        databaseReference.child("Day").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                for (e in snapshot.children) {
                    var sample = e.child("Fieldname").getValue(String::class.java)
                    var dc = DataClass(sample.toString(), calText.text.toString())
                    listData.add(dc)
                }
                val cal = snapshot.child("Cal").getValue(String::class.java)

            }

        }*/
        sharedPreferences = getSharedPreferences("BOKStorage", MODE_PRIVATE)
        var sharedEditor = sharedPreferences.edit()
        sharedEditor.putString("SharedID", "SampleValue")
        sharedEditor.apply()

        sharedPreferences.getString("SharedID", "")
    }
    /* fun getData() {
        for (i in titleList.indices) {
            var dataClass = DataClass(timeList[i], titleList[i])
            dataList.add(dataClass)
        }
        recyclerView.adapter = AdapterClass(dataList)

    }*/
}