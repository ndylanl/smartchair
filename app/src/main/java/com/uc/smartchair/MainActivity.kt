package com.uc.smartchair

import android.content.ContentValues
import android.media.RingtoneManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.database.*
import com.uc.smartchair.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var database: DatabaseReference? = null
    private var firebaseDatabase: FirebaseDatabase? = null
    private lateinit var binding: ActivityMainBinding
    private var time = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseDatabase = FirebaseDatabase.getInstance()
        database = firebaseDatabase?.reference?.child("Loadcell")

        getData()
        binding.standCV.visibility = View.GONE

        binding.dismissBTN.setOnClickListener(){
            binding.standCV.visibility = View.GONE
            database?.child("time")?.setValue(0)
        }

    }

    fun getData() {
        firebaseDatabase = FirebaseDatabase.getInstance()
        database = firebaseDatabase?.reference?.child("Loadcell")

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                binding.weightTV.text = (dataSnapshot.child("weight").value.toString().toInt()/1000).toString() + " kg"
                time = dataSnapshot.child("time").value.toString().toInt()
                if(time > 1800000){
                    binding.standCV.visibility = View.VISIBLE
                    val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    val track = RingtoneManager.getRingtone(applicationContext, notification)
                    track.play()
                }
                if(time < 5000){
                    binding.standCV.visibility = View.INVISIBLE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        database?.addValueEventListener(postListener)
    }
}